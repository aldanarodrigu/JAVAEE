package com.appchat.service;

import com.appchat.dto.ChatResumenDTO;
import com.appchat.dto.HistorialMensajesDTO;
import com.appchat.dto.MensajeDTO;
import com.appchat.model.Chat;
import com.appchat.model.ChatDirecto;
import com.appchat.model.Mensaje;
import com.appchat.model.Usuario;
import com.appchat.model.enums.TipoMensaje;
import com.appchat.repository.ChatRepository;
import com.appchat.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ChatService {

    @Inject
    private ChatRepository chatRepository;

    @Inject
    private UsuarioRepository usuarioRepository;

    @Transactional
    public MensajeDTO enviarMensaje(Long chatId, Long emisorId, String contenido){
        Chat chat = chatRepository.buscarChatDirectoPorId(chatId);
        if(chat == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        Usuario emisor = usuarioRepository.buscarPorId(emisorId);
        
        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(contenido);
        mensaje.setEmisor(emisor);
        mensaje.setChat(chat);
        mensaje.setTipo(TipoMensaje.TEXTO);
        
        chatRepository.guardarMensaje(mensaje);
        
        return mapearMensaje(mensaje);
        
    }
    
    @Transactional
    public List<ChatResumenDTO> listarChatsDelUsuario(Long usuarioId) {
        verificarUsuarioExiste(usuarioId);

        List<ChatDirecto> chats = chatRepository.listarChatsDeUsuario(usuarioId);
        List<ChatResumenDTO> respuestas = new ArrayList<>();

        for (ChatDirecto chat : chats) {
            respuestas.add(mapearResumen(chat, usuarioId));
        }

        return respuestas;
    }
    
    @Transactional
    public HistorialMensajesDTO obtenerHistorialMensajes(Long chatId, Long usuarioId, int page, int size) {
        ChatDirecto chat = chatRepository.buscarChatDirectoPorId(chatId);
        if (chat == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        validarParticipacion(chat, usuarioId);

        int paginaNormalizada = Math.max(page, 0);
        int tamanoNormalizado = size <= 0 ? 20 : size;
        int offset = paginaNormalizada * tamanoNormalizado;

        List<Mensaje> mensajes = chatRepository.buscarMensajesPagina(chatId, offset, tamanoNormalizado);
        long total = chatRepository.contarMensajes(chatId);

        HistorialMensajesDTO respuesta = new HistorialMensajesDTO();
        respuesta.setMensajes(mapearMensajes(mensajes));
        respuesta.setPage(paginaNormalizada);
        respuesta.setSize(tamanoNormalizado);
        respuesta.setTotalElementos(total);
        respuesta.setTotalPaginas((int) Math.ceil((double) total / tamanoNormalizado));
        return respuesta;
    }

    @Transactional
    public ChatResumenDTO crearOAbrirChatDirecto(Long usuarioAutenticadoId, Long usuarioDestinoId) {
        if (usuarioAutenticadoId.equals(usuarioDestinoId)) {
            throw new IllegalArgumentException("No se puede crear un chat directo contigo mismo");
        }

        Usuario usuarioAutenticado = verificarUsuarioExiste(usuarioAutenticadoId);
        Usuario usuarioDestino = verificarUsuarioExiste(usuarioDestinoId);

        ChatDirecto chatExistente = chatRepository.buscarChatDirectoEntreUsuarios(usuarioAutenticadoId, usuarioDestinoId);
        if (chatExistente != null) {
            return mapearResumen(chatExistente, usuarioAutenticadoId);
        }

        ChatDirecto chatNuevo = new ChatDirecto();
        chatNuevo.setUsuarioUno(usuarioAutenticado);
        chatNuevo.setUsuarioDos(usuarioDestino);
        chatRepository.guardarChatDirecto(chatNuevo);

        return mapearResumen(chatNuevo, usuarioAutenticadoId);
    }

    public Usuario resolverUsuarioAutenticado(String principal) {
        if (principal == null || principal.isBlank()) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        Usuario usuario = null;
        try {
            usuario = usuarioRepository.buscarPorId(Long.valueOf(principal));
        } catch (NumberFormatException ignored) {
        }

        if (usuario == null) {
            usuario = usuarioRepository.buscarPorEmail(principal);
        }

        if (usuario == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        return usuario;
    }

    private Usuario verificarUsuarioExiste(Long usuarioId) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return usuario;
    }

    private void validarParticipacion(ChatDirecto chat, Long usuarioId) {
        boolean participa = chat.getUsuarioUno().getId().equals(usuarioId)
                || chat.getUsuarioDos().getId().equals(usuarioId);
        if (!participa) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }

    private ChatResumenDTO mapearResumen(ChatDirecto chat, Long usuarioId) {
        ChatResumenDTO dto = new ChatResumenDTO();
        dto.setId(chat.getId());
        dto.setFechaCreacion(chat.getFechaCreacion());

        Usuario interlocutor = chat.getUsuarioUno().getId().equals(usuarioId)
                ? chat.getUsuarioDos()
                : chat.getUsuarioUno();

        dto.setUsuarioInterlocutorId(interlocutor.getId());
        dto.setUsuarioInterlocutorNombre(interlocutor.getNombre() + " " + interlocutor.getApellido());

        List<Mensaje> mensajes = chat.getMensajes();
        if (!mensajes.isEmpty()) {
            Mensaje ultimo = mensajes.get(mensajes.size() - 1);
            dto.setUltimoMensajeContenido(ultimo.getContenido());
            dto.setUltimoMensajeFecha(ultimo.getFechaEnvio());
        }

        return dto;
    }

    private List<MensajeDTO> mapearMensajes(List<Mensaje> mensajes) {
        List<MensajeDTO> respuestas = new ArrayList<>();

        for (Mensaje mensaje : mensajes) {
            MensajeDTO dto = new MensajeDTO();
            dto.setId(mensaje.getId());
            dto.setFechaEnvio(mensaje.getFechaEnvio());
            dto.setTipo(mensaje.getTipo());
            dto.setEstado(mensaje.getEstado());
            dto.setContenido(mensaje.getContenido());
            dto.setEmisorId(mensaje.getEmisor().getId());
            dto.setEmisorNombre(mensaje.getEmisor().getNombre());
            dto.setEmisorApellido(mensaje.getEmisor().getApellido());
            respuestas.add(dto);
        }

        return respuestas;
    }
    
    private MensajeDTO mapearMensaje(Mensaje mensaje) {
    
        MensajeDTO dto = new MensajeDTO(); // crea el objeto "limpio"

        dto.setId(mensaje.getId());                        // el id del mensaje
        dto.setContenido(mensaje.getContenido());          // el texto
        dto.setFechaEnvio(mensaje.getFechaEnvio());        // cuándo se envió
        dto.setTipo(mensaje.getTipo());                    // TEXTO, IMAGEN, etc
        dto.setEstado(mensaje.getEstado());                // ENVIADO, LEIDO, etc

        // Del emisor solo expone lo necesario, no el objeto entero
        dto.setEmisorId(mensaje.getEmisor().getId());
        dto.setEmisorNombre(mensaje.getEmisor().getNombre());
        dto.setEmisorApellido(mensaje.getEmisor().getApellido());
        // ← NO expone email, password, etc

        return dto;
    }
    
}