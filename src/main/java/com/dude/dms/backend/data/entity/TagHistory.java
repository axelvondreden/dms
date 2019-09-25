package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class TagHistory extends History {

    @Id
    @GeneratedValue
    private Long tag_history_id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public TagHistory() {

    }

    public TagHistory(Tag tag, User historyUser, String text, boolean created, boolean edited, boolean deleted) {
        super(historyUser, text, created, edited, deleted);
        this.tag = tag;
    }

    public Long getTag_history_id() {
        return tag_history_id;
    }

    @Override
    public Long getId() {
        return tag_history_id;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
