package com.dude.dms.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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

    @ManyToMany(mappedBy = "tags")
    private List<Person> persons;

    @OneToMany(mappedBy = "tag")
    @OrderBy("timestamp")
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

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
