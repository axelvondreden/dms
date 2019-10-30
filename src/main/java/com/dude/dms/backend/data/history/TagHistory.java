package com.dude.dms.backend.data.history;

import com.dude.dms.backend.data.Tag;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class TagHistory extends History {

    @NotNull
    @ManyToOne
    private Tag tag;

    public TagHistory() {

    }

    public TagHistory(Tag tag, String text, boolean created, boolean edited, boolean deleted) {
        super(text, created, edited, deleted);
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}