package com.appchat.repository;

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

    public ChatDirecto buscarChatDirectoEntreUsuarios(Long usuarioUnoId, Long usuarioDosId) {
        try {
            return em.createQuery(
                    "SELECT c FROM ChatDirecto c "
                    + "WHERE (c.usuarioUno.id = :usuarioUnoId AND c.usuarioDos.id = :usuarioDosId) "
                    + "OR (c.usuarioUno.id = :usuarioDosId AND c.usuarioDos.id = :usuarioUnoId)",
                    ChatDirecto.class)
                    .setParameter("usuarioUnoId", usuarioUnoId)
                    .setParameter("usuarioDosId", usuarioDosId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<ChatDirecto> listarChatsDeUsuario(Long usuarioId) {
        return em.createQuery(
                "SELECT c FROM ChatDirecto c "
                + "WHERE c.usuarioUno.id = :usuarioId OR c.usuarioDos.id = :usuarioId "
                + "ORDER BY c.fechaCreacion DESC",
                ChatDirecto.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    public ChatDirecto buscarChatDirectoPorId(Long chatId) {
        return em.find(ChatDirecto.class, chatId);
    }

    public void guardarChatDirecto(ChatDirecto chatDirecto) {
        em.persist(chatDirecto);
    }

    public void guardarMensaje(Mensaje mensaje) {
        em.persist(mensaje);
    }

    public List<Mensaje> buscarMensajesPagina(Long chatId, int offset, int limit) {
        return em.createQuery(
                "SELECT m FROM Mensaje m WHERE m.chat.id = :chatId ORDER BY m.fechaEnvio ASC",
                Mensaje.class)
                .setParameter("chatId", chatId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long contarMensajes(Long chatId) {
        return em.createQuery(
                "SELECT COUNT(m) FROM Mensaje m WHERE m.chat.id = :chatId",
                Long.class)
                .setParameter("chatId", chatId)
                .getSingleResult();
    }
}