package com.dude.dms.ui.builder;

import com.dude.dms.backend.service.*;
import com.dude.dms.ui.builder.cards.CardBuilderFactory;
import com.dude.dms.ui.builder.dialogs.DialogBuilderFactory;
import com.dude.dms.ui.builder.searchresults.SearchResultBuilderFactory;
import com.dude.dms.updater.UpdateChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuilderFactory {

    private final DocService docService;
    private final TagService tagService;
    private final PlainTextRuleService plainTextRuleService;
    private final RegexRuleService regexRuleService;
    private final ChangelogService changelogService;
    private final UpdateChecker updateChecker;
    private final TextBlockService textBlockService;

    @Autowired
    public BuilderFactory(DocService docService, TagService tagService, PlainTextRuleService plainTextRuleService, RegexRuleService regexRuleService, ChangelogService changelogService, UpdateChecker updateChecker, TextBlockService textBlockService) {
        this.docService = docService;
        this.tagService = tagService;
        this.plainTextRuleService = plainTextRuleService;
        this.regexRuleService = regexRuleService;
        this.changelogService = changelogService;
        this.updateChecker = updateChecker;
        this.textBlockService = textBlockService;
    }

    public CardBuilderFactory cards() {
        return new CardBuilderFactory(this, tagService);
    }

    public DialogBuilderFactory dialogs() {
        return new DialogBuilderFactory(this, docService, tagService, plainTextRuleService, regexRuleService, changelogService, updateChecker, textBlockService);
    }

    public SearchResultBuilderFactory searchResults() {
        return new SearchResultBuilderFactory(this, docService);
    }
}