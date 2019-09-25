package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class TagHistory extends History {

    @Id
    @GeneratedValue
    private Long tagHistoryId;

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

    public Long getTagHistoryId() {
        return tagHistoryId;
    }

    @Override
    public Long getId() {
        return tagHistoryId;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
