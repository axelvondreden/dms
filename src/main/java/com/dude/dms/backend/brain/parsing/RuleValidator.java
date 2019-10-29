package com.dude.dms.backend.brain.parsing;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.rules.Rule;
import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class RuleValidator<T extends Rule> {

    protected final TagService tagService;

    protected final DocService docService;

    public abstract Set<Tag> getTagsForRule(String rawText, T rule);

    public abstract Set<Tag> getTags(String rawText);

    protected RuleValidator(TagService tagService, DocService docService) {
        this.tagService = tagService;
        this.docService = docService;
    }

    public Map<Doc, Set<Tag>> runRuleForAll(T rule) {
        Map<Doc, Set<Tag>> result = new HashMap<>();
        List<Doc> docs = docService.findAll();
        for (Doc doc : docs) {
            Set<Tag> tags = getTagsForRule(doc.getRawText(), rule);
            tags.removeAll(doc.getTags());
            if (!tags.isEmpty()) {
                result.put(doc, tags);
            }
        }
        return result;
    }
}