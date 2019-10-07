package com.dude.dms.backend.data.rules;

import com.dude.dms.backend.data.base.Tag;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
public class PlainTextRule extends Rule {

    @NotBlank
    protected String text;

    @ManyToMany
    private Set<Tag> tags;

    public PlainTextRule() {
    }

    public PlainTextRule(@NotBlank String text, Set<Tag> tags) {
        super(true);
        this.tags = tags;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean validate(String line) {
        return line != null && !line.isEmpty() && line.contains(text);
    }

    @Override
    public Set<Tag> getTags() {
        return tags;
    }

    @Override
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "PlainTextRule{text='" + text + "'}";
    }
}