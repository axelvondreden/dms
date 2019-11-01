package com.dude.dms.backend.data.rules;

import com.dude.dms.backend.data.Tag;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.regex.Pattern;

@Entity
public class RegexRule extends Rule {

    @Transient
    private Pattern pattern;

    @NotBlank
    protected String regex;

    @ManyToMany
    private Set<Tag> tags;

    public RegexRule() {
    }

    public RegexRule(@NotBlank String regex, Set<Tag> tags) {
        super(true);
        this.tags = tags;
        this.regex = regex;
        pattern = Pattern.compile(regex);
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean validate(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }
        return pattern.matcher(line).matches();
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
        return "RegexRule{regex='" + regex + "'}";
    }
}