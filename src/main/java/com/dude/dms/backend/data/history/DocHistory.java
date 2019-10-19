package com.dude.dms.backend.data.history;

import com.dude.dms.backend.data.docs.Doc;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class DocHistory extends History {

    @NotNull
    @ManyToOne
    private Doc doc;

    public DocHistory() {

    }

    public DocHistory(Doc doc, String text, boolean created, boolean edited, boolean deleted) {
        super(text, created, edited, deleted);
        this.doc = doc;
    }

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }
}