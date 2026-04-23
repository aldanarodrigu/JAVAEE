package com.appchat.service;

import com.appchat.dto.ChatResumenDTO;
import com.appchat.dto.HistorialMensajesDTO;
import com.appchat.dto.MensajeDTO;
import com.appchat.dto.MensajeWSDTO;
import com.appchat.model.Chat;
import com.appchat.model.Comunidad;
import com.appchat.model.Mensaje;
import com.appchat.model.Usuario;
import com.appchat.model.enums.EstadoMensaje;
import com.appchat.model.enums.TipoChat;
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
    
    @Inject
    private ComunidadService comunidadService;

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

        notificarChat(chat, dto);
        
        return dto;
    }
    
    private void notificarChat(Chat chat, MensajeDTO mensaje) {

        List<Long> destinatarios = obtenerUsuariosDelChat(chat, mensaje.getEmisorId());

        String json = JsonUtil.toJson(mensaje);

        for (Long userId : destinatarios) {
            chatHub.enviarAUsuario(userId, json);
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
    public ChatResumenDTO crearOAbrirChatDirecto(Long usuarioAutenticadoId, Long usuarioDestinoId, Long comunidadId) {
        
        if (usuarioAutenticadoId.equals(usuarioDestinoId)) {
            throw new IllegalArgumentException("No se puede crear un chat directo contigo mismo");
        }

        Usuario usuarioAutenticado = verificarUsuarioExiste(usuarioAutenticadoId);
        Usuario usuarioDestino = verificarUsuarioExiste(usuarioDestinoId);

        Comunidad comunidad = comunidadService.buscarPorId(comunidadId);
        if(comunidad == null){
            throw new WebApplicationException("Comunidad no existe", 404);
        }
        
        if (!comunidadService.sonMiembros(comunidadId, usuarioAutenticadoId, usuarioDestinoId)) {
        throw new WebApplicationException("Ambos usuarios deben pertenecer a la comunidad", 403);
    }
        
        Chat chatExistente = chatRepository.buscarChatDirectoEntreUsuarios(usuarioAutenticadoId, usuarioDestinoId, comunidadId);
        if (chatExistente != null) {
            return mapearResumen(chatExistente, usuarioAutenticadoId);
        }

        Chat chatNuevo = new Chat();
        chatNuevo.setTipo(TipoChat.DIRECTO);
        chatNuevo.setComunidad(comunidad);

        chatNuevo.agregarParticipante(usuarioAutenticado, null);
        chatNuevo.agregarParticipante(usuarioDestino, null);

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

        if (chat.getTipo() == TipoChat.DIRECTO) {

            Usuario otro = chat.getParticipantes().stream()
                    .filter(u -> !u.getId().equals(usuarioId))
                    .findFirst()
                    .orElse(null);

            if (otro != null) {
                dto.setNombre(otro.getNombre() + " " + otro.getApellido());
            }

            dto.setTipo("DIRECTO");

        } else { // canal o grupo

            dto.setNombre(chat.getNombre());
            dto.setFotoUrl(chat.getFotoUrl());
            dto.setTipo("GRUPO");
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

        if (dto == null || dto.getChatId() == null || dto.getContenido() == null) {
            throw new IllegalArgumentException("Mensaje inválido");
        }

        enviarMensaje(
            dto.getChatId(),
            userId,
            dto.getContenido()
        );
    }
    
}