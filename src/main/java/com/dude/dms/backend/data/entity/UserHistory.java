package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class UserHistory extends History {

    @Id
    @GeneratedValue
    private Long user_history_id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserHistory() {

    }

    public UserHistory(User user, User historyUser, String text, boolean created, boolean edited, boolean deleted) {
        super(historyUser, text, created, edited, deleted);
        this.user = user;
    }

    public Long getUser_history_id() {
        return user_history_id;
    }

    @Override
    public Long getId() {
        return user_history_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
