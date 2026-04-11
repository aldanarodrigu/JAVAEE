package com.chat.model;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_groups")
public class ChatGroup extends Chat {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    public ChatGroup() {
    }

    public ChatGroup(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
