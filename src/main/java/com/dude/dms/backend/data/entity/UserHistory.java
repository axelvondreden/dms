package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class UserHistory extends History {

    @NotNull
    @ManyToOne
    private User user;

    public UserHistory() {

    }

    public UserHistory(User user, User historyUser, String text, boolean created, boolean edited, boolean deleted) {
        super(historyUser, text, created, edited, deleted);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
