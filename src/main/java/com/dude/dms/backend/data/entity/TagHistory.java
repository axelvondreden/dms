package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class TagHistory extends History {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tagId")
    private Tag tag;

    public TagHistory() {

    }

    public TagHistory(Tag tag, User historyUser, String text, boolean created, boolean edited, boolean deleted) {
        super(historyUser, text, created, edited, deleted);
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
