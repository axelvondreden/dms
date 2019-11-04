package com.dude.dms.backend.data.docs;

import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.backend.data.Diffable;
import com.dude.dms.backend.data.Historical;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.history.DocHistory;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Doc extends DataEntity implements Diffable<Doc>, Historical<DocHistory> {

    protected LocalDate documentDate;

    @Size(max = 99999999)
    protected String rawText;

    @NotNull
    protected String guid;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Tag> tags;

    @OneToMany(mappedBy = "doc")
    private Set<TextBlock> textBlocks;

    @OneToMany(mappedBy = "doc", fetch = FetchType.EAGER)
    private Set<AttributeValue> attributeValues;

    @OneToMany(mappedBy = "doc")
    @OrderBy("timestamp")
    private List<DocHistory> history;

    public Doc() {

    }

    public Doc(@NotNull String guid) {
        this.guid = guid;
    }

    public Doc(String rawText, @NotNull String guid) {
        this.rawText = rawText;
        this.guid = guid;
    }

    public Doc(LocalDate documentDate, @Size(max = 99999999) String rawText, @NotNull String guid) {
        this.documentDate = documentDate;
        this.rawText = rawText;
        this.guid = guid;
    }

    public Doc(LocalDate documentDate, @Size(max = 99999999) String rawText, @NotNull String guid, Set<Tag> tags) {
        this.documentDate = documentDate;
        this.rawText = rawText;
        this.guid = guid;
        this.tags = tags;
    }



    public LocalDate getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(LocalDate documentDate) {
        this.documentDate = documentDate;
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

    public Set<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    public void setTextBlocks(Set<TextBlock> textBlocks) {
        this.textBlocks = textBlocks;
    }

    @Override
    public String toString() {
        return "Doc{guid='" + guid + "'}";
    }

    public Set<AttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(Set<AttributeValue> attributeValues) {
        this.attributeValues = attributeValues;
    }
}