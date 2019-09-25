package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class DocHistory extends History {

    @Id
    @GeneratedValue
    private Long doc_history_id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "doc_id")
    private Doc doc;

    public DocHistory() {

    }

    public DocHistory(Doc doc, User historyUser, String text, boolean created, boolean edited, boolean deleted) {
        super(historyUser, text, created, edited, deleted);
        this.doc = doc;
    }

    public Long getDoc_history_id() {
        return doc_history_id;
    }

    @Override
    public Long getId() {
        return doc_history_id;
    }

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }
}
