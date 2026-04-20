package com.appchat.service;

import com.appchat.dto.ChatResumenDTO;
import com.appchat.dto.HistorialMensajesDTO;
import com.appchat.dto.MensajeDTO;
import com.appchat.dto.MensajeWSDTO;
import com.appchat.model.Chat;
import com.appchat.model.ChatDirecto;
import com.appchat.model.ChatGrupal;
import com.appchat.model.Mensaje;
import com.appchat.model.Usuario;
import com.appchat.model.enums.EstadoMensaje;
import com.appchat.model.enums.TipoMensaje;
import com.appchat.repository.ChatRepository;
import com.appchat.repository.UsuarioRepository;
import com.appchat.util.JsonUtil;
import com.appchat.websocket.ChatHub;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class ChatService {

    @Inject
    private ChatRepository chatRepository;

    @Inject
    private UsuarioRepository usuarioRepository;
    
    @Inject 
    private ChatHub chatHub;

    @Transactional
    public MensajeDTO enviarMensaje(Long chatId, Long emisorId, String contenido) {

        Chat chat = chatRepository.buscarChatPorId(chatId);
        
        if (chat == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        validarParticipacion(chat, emisorId);

        Usuario emisor = usuarioRepository.buscarPorId(emisorId);

        if (emisor == null) {
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(contenido);
        mensaje.setChat(chat);      
        mensaje.setEmisor(emisor);
        mensaje.setTipo(TipoMensaje.TEXTO);
        mensaje.setEstado(EstadoMensaje.ENVIADO);
        
        chatRepository.guardarMensaje(mensaje);

        chatRepository.flush();

        MensajeDTO dto = mapearMensaje(mensaje);

        notificarChat(chat, mensaje, dto);
        
        return dto;
    }
    
    private void notificarChat(Chat chat, Mensaje mensaje, MensajeDTO dtoMensaje) {

        List<Long> destinatarios = obtenerUsuariosDelChat(chat, dtoMensaje.getEmisorId());
        boolean entregado = false;

        String json = JsonUtil.toJson(dtoMensaje);

        for (Long userId : destinatarios) {
            if (!chatHub.obtenerSesiones(userId).isEmpty()) {
                chatHub.enviarAUsuario(userId, json);
                entregado = true;
            }
        }

        if (entregado && mensaje.getEstado() == EstadoMensaje.ENVIADO) {
            mensaje.setEstado(EstadoMensaje.ENTREGADO);
            chatRepository.flush();

            MensajeDTO dtoEntregado = mapearMensaje(mensaje);
            chatHub.enviarAUsuario(mensaje.getEmisor().getId(), JsonUtil.toJson(dtoEntregado));
        }
    }
    
    private List<Long> obtenerUsuariosDelChat(Chat chat, Long emisorId) {
        return chat.getParticipantes().stream().map(Usuario::getId).filter(id -> !id.equals(emisorId)).toList();
    }
    
    @Transactional
    public List<ChatResumenDTO> listarChatsDelUsuario(Long usuarioId) {
        verificarUsuarioExiste(usuarioId);

        List<Chat> chats = chatRepository.listarChatsDeUsuario(usuarioId);
        List<ChatResumenDTO> respuestas = new ArrayList<>();

        for (Chat chat : chats) {
            respuestas.add(mapearResumen(chat, usuarioId));
        }

        return respuestas;
    }
    
    @Transactional
    public HistorialMensajesDTO obtenerHistorialMensajes(Long chatId, Long usuarioId, int page, int size) {
        Chat chat = chatRepository.buscarChatPorId(chatId);
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
        chatRepository.guardarChat(chatNuevo);

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

    private void validarParticipacion(Chat chat, Long usuarioId) {
        if (!chat.esParticipante(usuarioId)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }

    private ChatResumenDTO mapearResumen(Chat chat, Long usuarioId) {
        ChatResumenDTO dto = new ChatResumenDTO();
        dto.setId(chat.getId());
        dto.setFechaCreacion(chat.getFechaCreacion());

        if (chat instanceof ChatDirecto cd) {
            Usuario interlocutor = cd.getUsuarioUno().getId().equals(usuarioId)
                    ? cd.getUsuarioDos()
                    : cd.getUsuarioUno();
            dto.setNombre(interlocutor.getNombre() + " " + interlocutor.getApellido());
            dto.setTipo("DIRECTO");
        } else if (chat instanceof ChatGrupal cg) {
            dto.setNombre(cg.getNombre());
            dto.setFotoUrl(cg.getFotoUrl());
            dto.setTipo("GRUPAL");
        }

        Mensaje ultimo = chatRepository.buscarUltimoMensaje(chat.getId());
        if (ultimo != null) {
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
    
        MensajeDTO dto = new MensajeDTO(); 

        dto.setId(mensaje.getId());                      
        dto.setContenido(mensaje.getContenido());        
        dto.setFechaEnvio(mensaje.getFechaEnvio());
        dto.setTipo(mensaje.getTipo());
        dto.setEstado(mensaje.getEstado());

        dto.setEmisorId(mensaje.getEmisor().getId());
        dto.setEmisorNombre(mensaje.getEmisor().getNombre());
        dto.setEmisorApellido(mensaje.getEmisor().getApellido());

        return dto;
    }

    public void procesarMensajeWebSocket(Long userId, String message) {

        MensajeWSDTO dto = JsonUtil.fromJson(message, MensajeWSDTO.class);

        if (dto == null) {
            throw new IllegalArgumentException("Mensaje inválido");
        }

        if (esAcuseLectura(dto)) {
            if (dto.getMensajeId() == null) {
                throw new IllegalArgumentException("Mensaje inválido");
            }

            marcarMensajeLeidoDesdeWebSocket(userId, dto.getMensajeId(), dto.getChatId());
            return;
        }

        if (dto.getChatId() == null || dto.getContenido() == null) {
            throw new IllegalArgumentException("Mensaje inválido");
        }

        enviarMensaje(
            dto.getChatId(),
            userId,
            dto.getContenido()
        );
    }

    @Transactional
    public MensajeDTO marcarMensajeLeidoDesdeWebSocket(Long usuarioId, Long mensajeId, Long chatId) {
        Mensaje mensaje = chatRepository.buscarMensajePorId(mensajeId);

        if (mensaje == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (chatId != null && !mensaje.getChat().getId().equals(chatId)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        validarParticipacion(mensaje.getChat(), usuarioId);

        if (mensaje.getEmisor().getId().equals(usuarioId)) {
            return mapearMensaje(mensaje);
        }

        if (mensaje.getEstado() != EstadoMensaje.LEIDO) {
            mensaje.setEstado(EstadoMensaje.LEIDO);
            chatRepository.flush();
        }

        MensajeDTO dto = mapearMensaje(mensaje);
        chatHub.enviarAUsuario(mensaje.getEmisor().getId(), JsonUtil.toJson(dto));

        return dto;
    }

    private boolean esAcuseLectura(MensajeWSDTO dto) {
        return dto.getAccion() != null && "LEIDO".equalsIgnoreCase(dto.getAccion());
    }
    
}