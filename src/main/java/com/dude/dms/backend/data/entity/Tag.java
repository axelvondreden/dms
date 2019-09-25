package com.dude.dms.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Tag extends DataEntity {

    @Id
    @GeneratedValue
    private Long tagId;

    @NotBlank
    @Size(max = 50)
    private String name;

    @OneToMany(mappedBy = "tag")
    private List<TagHistory> history;

    public Tag() {

    }

    public Tag(User currentUser) {

    }

    public Long getTagId() {
        return tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return tagId;
    }

    public List<TagHistory> getHistory() {
        return history;
    }

    public void setHistory(List<TagHistory> history) {
        this.history = history;
    }
}
