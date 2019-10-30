package com.dude.dms.backend.data.rules;

import com.dude.dms.backend.data.Tag;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import java.util.Locale;
import java.util.Set;

@Entity
public class PlainTextRule extends Rule {

    @NotBlank
    private String text;

    private Boolean caseSensitive;

    @ManyToMany
    private Set<Tag> tags;

    public PlainTextRule() {
    }

    public PlainTextRule(@NotBlank String text, boolean caseSensitive, Set<Tag> tags) {
        super(true);
        this.text = text;
        this.caseSensitive = caseSensitive;
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean validate(String line) {
        if (line != null && !line.isEmpty()) {
            if (caseSensitive) {
                return line.contains(text);
            } else {
                Locale locale = LocaleContextHolder.getLocale();
                return line.toLowerCase(locale).contains(text.toLowerCase(locale));
            }
        }
        return false;
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

    public Boolean getCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
}