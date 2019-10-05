package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Doc extends DataEntity implements Diffable<Doc>, Historical<DocHistory> {

    @NotBlank
    @Size(max = 255)
    protected String importTitle;

    @Size(max = 99999999)
    protected String rawText;

    @NotNull
    protected String guid;

    @ManyToMany
    private Set<Tag> tags;

    @OneToMany(mappedBy = "doc")
    @OrderBy("timestamp")
    private List<DocHistory> history;

    public Doc() {

    }

    public Doc(@NotBlank @Size(max = 255) String importTitle, String rawText, @NotNull String guid) {
        this.importTitle = importTitle;
        this.rawText = rawText;
        this.guid = guid;
    }

    public String getImportTitle() {
        return importTitle;
    }

    public void setImportTitle(String importTitle) {
        this.importTitle = importTitle;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
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

    public Set<Tag> getTags() {
        return tags != null ? tags : new HashSet<>();
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}