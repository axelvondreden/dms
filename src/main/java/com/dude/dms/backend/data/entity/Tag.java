package com.dude.dms.backend.data.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Tag extends DataEntity implements Diffable<Tag>, Historical<TagHistory> {

    @NotBlank
    @Size(max = 50)
    protected String name;

    @NotBlank
    protected String color;

    @ManyToMany(mappedBy = "tags")
    private List<Doc> docs;

    @OneToMany(mappedBy = "tag")
    @OrderBy("timestamp")
    private List<TagHistory> history;

    public Tag() {

    }

    public Tag(@NotBlank @Size(max = 50) String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public List<TagHistory> getHistory() {
        return history;
    }

    public void setHistory(List<TagHistory> history) {
        this.history = history;
    }

    public List<Doc> getDocs() {
        return docs;
    }

    public void setDocs(List<Doc> docs) {
        this.docs = docs;
    }
}