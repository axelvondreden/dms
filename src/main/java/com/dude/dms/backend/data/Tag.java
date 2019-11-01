package com.dude.dms.backend.data;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.history.TagHistory;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

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

    @ManyToMany(mappedBy = "tags")
    private List<PlainTextRule> plainTextRules;

    @ManyToMany(mappedBy = "tags")
    private List<RegexRule> regexRules;

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

    public List<PlainTextRule> getPlainTextRules() {
        return plainTextRules;
    }

    public void setPlainTextRules(List<PlainTextRule> plainTextRules) {
        this.plainTextRules = plainTextRules;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        Tag tag = (Tag) obj;
        return name.equals(tag.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    public List<RegexRule> getRegexRules() {
        return regexRules;
    }

    public void setRegexRules(List<RegexRule> regexRules) {
        this.regexRules = regexRules;
    }
}