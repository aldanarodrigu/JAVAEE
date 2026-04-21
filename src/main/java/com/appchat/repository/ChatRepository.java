package com.appchat.repository;

import com.appchat.model.Chat;
import com.appchat.model.ChatDirecto;
import com.appchat.model.Mensaje;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class ChatRepository {

    @PersistenceContext(unitName = "appchatPU")
    private EntityManager em;

    public Chat buscarChatPorId(Long chatId) {
        return em.find(Chat.class, chatId);
    }

    public ChatDirecto buscarChatDirectoEntreUsuarios(Long usuarioUnoId, Long usuarioDosId) {
        try {
            return em.createQuery(
                    "SELECT c FROM ChatDirecto c "
                    + "JOIN FETCH c.usuarioUno "
                    + "JOIN FETCH c.usuarioDos "
                    + "WHERE (c.usuarioUno.id = :unoId AND c.usuarioDos.id = :dosId) "
                    + "OR    (c.usuarioUno.id = :dosId AND c.usuarioDos.id = :unoId)",
                    ChatDirecto.class)
                    .setParameter("unoId", usuarioUnoId)
                    .setParameter("dosId", usuarioDosId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Chat> listarChatsDeUsuario(Long usuarioId) {
        // chats directos donde participa
        List<Chat> directos = em.createQuery(
                "SELECT c FROM ChatDirecto c "
                + "JOIN FETCH c.usuarioUno "
                + "JOIN FETCH c.usuarioDos "
                + "WHERE c.usuarioUno.id = :usuarioId OR c.usuarioDos.id = :usuarioId",
                Chat.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();

        // chats grupales donde participa
        List<Chat> grupales = em.createQuery(
                "SELECT DISTINCT c FROM ChatGrupal c "
                + "JOIN FETCH c.participantes p "
                + "JOIN FETCH p.usuario "
                + "WHERE p.usuario.id = :usuarioId",
                Chat.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();

        List<Chat> todos = new java.util.ArrayList<>();
        todos.addAll(directos);
        todos.addAll(grupales);
        todos.sort((a, b) -> b.getFechaCreacion().compareTo(a.getFechaCreacion()));
        return todos;
    }

    public void guardarChat(Chat chat) {
        em.persist(chat);
    }

    public void guardarMensaje(Mensaje mensaje) {
        em.persist(mensaje);
    }

    public Mensaje buscarMensajePorId(Long mensajeId) {
        return em.find(Mensaje.class, mensajeId);
    }

    public Mensaje buscarUltimoMensaje(Long chatId) {
        try {
            return em.createQuery(
                    "SELECT m FROM Mensaje m "
                    + "JOIN FETCH m.emisor "
                    + "WHERE m.chat.id = :chatId "
                    + "ORDER BY m.fechaEnvio DESC",
                    Mensaje.class)
                    .setParameter("chatId", chatId)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Mensaje> buscarMensajesPagina(Long chatId, int offset, int limit) {
        return em.createQuery(
                "SELECT m FROM Mensaje m "
                + "JOIN FETCH m.emisor "
                + "WHERE m.chat.id = :chatId "
                + "ORDER BY m.fechaEnvio ASC",
                Mensaje.class)
                .setParameter("chatId", chatId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Mensaje> buscarMensajesEnviadosPendientesEntregaParaUsuarioEnChat(Long chatId, Long usuarioId) {
        return em.createQuery(
                "SELECT m FROM Mensaje m "
                + "JOIN FETCH m.emisor "
                + "WHERE m.chat.id = :chatId "
                + "AND m.emisor.id <> :usuarioId "
                + "AND m.estado = :estadoEnviado "
                + "ORDER BY m.fechaEnvio ASC",
                Mensaje.class)
                .setParameter("chatId", chatId)
                .setParameter("usuarioId", usuarioId)
                .setParameter("estadoEnviado", com.appchat.model.enums.EstadoMensaje.ENVIADO)
                .getResultList();
    }

    public List<Mensaje> buscarMensajesPendientesLecturaParaUsuarioEnChat(Long chatId, Long usuarioId) {
        return em.createQuery(
                "SELECT m FROM Mensaje m "
                + "JOIN FETCH m.emisor "
                + "WHERE m.chat.id = :chatId "
                + "AND m.emisor.id <> :usuarioId "
                + "AND m.estado <> :estadoLeido "
                + "ORDER BY m.fechaEnvio ASC",
                Mensaje.class)
                .setParameter("chatId", chatId)
                .setParameter("usuarioId", usuarioId)
                .setParameter("estadoLeido", com.appchat.model.enums.EstadoMensaje.LEIDO)
                .getResultList();
    }

    public long contarMensajes(Long chatId) {
        return em.createQuery(
                "SELECT COUNT(m) FROM Mensaje m WHERE m.chat.id = :chatId",
                Long.class)
                .setParameter("chatId", chatId)
                .getSingleResult();
    }    

    public void flush() {
        em.flush();
    }
}