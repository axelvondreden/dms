package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class DocHistory extends History {

    @NotNull
    @ManyToOne
    private Doc doc;

    public DocHistory() {

    }

    public DocHistory(Doc doc, User historyUser, String text, boolean created, boolean edited, boolean deleted) {
        super(historyUser, text, created, edited, deleted);
        this.doc = doc;
    }

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }
}
