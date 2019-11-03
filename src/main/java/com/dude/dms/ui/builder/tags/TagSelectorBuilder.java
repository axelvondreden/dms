package com.dude.dms.ui.builder.tags;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.components.tags.TagSelector;

import java.util.ArrayList;

public final class TagSelectorBuilder {

    private final TagService tagService;

    private Iterable<Tag> selected;

    private String rawText;

    TagSelectorBuilder(TagService tagService) {
        this.tagService = tagService;
        selected = new ArrayList<>();
    }

    public TagSelectorBuilder forDoc(Doc doc) {
        return withSelectedTags(doc.getTags()).withDocRawText(doc.getRawText());
    }

    public TagSelectorBuilder forRule(PlainTextRule rule) {
        return withSelectedTags(tagService.findByPlainTextRule(rule));
    }

    public TagSelectorBuilder forRule(RegexRule rule) {
        return withSelectedTags(tagService.findByRegexRule(rule));
    }

    public TagSelectorBuilder withDocRawText(String rawText) {
        this.rawText = rawText;
        return this;
    }

    public TagSelectorBuilder withSelectedTags(Iterable<Tag> selected) {
        this.selected = selected;
        return this;
    }

    public TagSelector build() {
        TagSelector tagSelector = new TagSelector(tagService);
        tagSelector.setSelectedTags(selected);
        tagSelector.setContainedTags(rawText);
        return tagSelector;
    }
}