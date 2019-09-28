package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Entity
public class Doc extends DataEntity implements Diffable<Doc>, Historical<DocHistory>, Tagged {

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotNull
    private String guid;

    @ManyToOne
    private User user;

    @ManyToMany
    private Set<Tag> tags;

    @OneToMany(mappedBy = "doc")
    private List<DocHistory> history;

    public Doc() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public List<DocHistory> getHistory() {
        return history;
    }

    public void setHistory(List<DocHistory> history) {
        this.history = history;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Set<Tag> getTags() {
        return tags;
    }

    @Override
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
