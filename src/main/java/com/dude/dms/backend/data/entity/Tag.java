package com.dude.dms.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Tag extends DataEntity implements Diffable<Tag>, Historical<TagHistory> {

    @NotBlank
    @Size(max = 50)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Doc> docs;

    @OneToMany(mappedBy = "tag")
    private List<TagHistory> history;

    public Tag() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
