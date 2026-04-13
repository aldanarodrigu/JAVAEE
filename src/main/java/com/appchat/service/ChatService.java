package com.appchat.service;

import com.appchat.dto.ChatDirectoRequestDTO;
import com.appchat.dto.ChatDirectoResponseDTO;
import com.appchat.dto.ChatResumenDTO;
import com.appchat.dto.MensajeDTO;
import com.appchat.dto.PageDTO;
import com.appchat.model.Chat;
import com.appchat.model.Participa;
import com.appchat.model.Usuario;
import com.appchat.model.enums.RolGrupo;
import com.appchat.repository.ChatRepository;
import com.appchat.repository.MensajeRepository;
import com.appchat.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ChatService {

    @Inject
    private ChatRepository chatRepository;

    @Inject
    private MensajeRepository mensajeRepository;

    @Inject
    private UsuarioRepository usuarioRepository;

    public List<ChatResumenDTO> listarChats(Long usuarioId) {
        return chatRepository.listarChatsPorUsuario(usuarioId);
    }

    public PageDTO<MensajeDTO> historial(Long chatId, Long usuarioId, int page, int size) {
        if (page < 0 || size <= 0 || size > 100) {
            throw new IllegalArgumentException("Paginacion invalida");
        }

        if (!chatRepository.esParticipante(chatId, usuarioId)) {
            throw new ForbiddenException("No participas en este chat");
        }

        List<MensajeDTO> mensajes = mensajeRepository.historialPaginado(chatId, page, size);
        long total = mensajeRepository.contarPorChat(chatId);
        return new PageDTO<>(mensajes, page, size, total);
    }

    @Transactional
    public ChatDirectoResponseDTO crearOAbrirDirecto(Long usuarioId, ChatDirectoRequestDTO dto) {
        if (dto == null || dto.getUsuarioDestinoId() == null) {
            throw new IllegalArgumentException("usuarioDestinoId es obligatorio");
        }

        if (usuarioId.equals(dto.getUsuarioDestinoId())) {
            throw new IllegalArgumentException("No puedes crear un chat directo contigo mismo");
        }

        Usuario origen = usuarioRepository.buscarPorId(usuarioId);
        Usuario destino = usuarioRepository.buscarPorId(dto.getUsuarioDestinoId());

        if (origen == null || destino == null) {
            throw new NotFoundException("Usuario no encontrado");
        }

        Chat existente = chatRepository.buscarDirectoEntre(origen.getId(), destino.getId());
        if (existente != null) {
            return new ChatDirectoResponseDTO(false, new ChatResumenDTO(existente.getId(), null, null));
        }

        Chat chat = new Chat();
        chat.setFechaCreacion(new Date());
        chatRepository.guardar(chat);

        Participa participaOrigen = new Participa(chat, origen, new Date(), RolGrupo.MIEMBRO);
        Participa participaDestino = new Participa(chat, destino, new Date(), RolGrupo.MIEMBRO);

        chatRepository.guardarParticipacion(participaOrigen);
        chatRepository.guardarParticipacion(participaDestino);

        return new ChatDirectoResponseDTO(true, new ChatResumenDTO(chat.getId(), null, null));
    }

    public Long resolverUsuarioIdDesdePrincipal(String principalName) {
        if (principalName == null || principalName.isBlank()) {
            throw new IllegalArgumentException("Principal vacio");
        }

        try {
            return Long.valueOf(principalName);
        } catch (NumberFormatException e) {
            Usuario usuario = usuarioRepository.buscarPorEmail(principalName);
            if (usuario == null) {
                throw new NotFoundException("No se encontro usuario autenticado");
            }
            return usuario.getId();
        }
    }
}
