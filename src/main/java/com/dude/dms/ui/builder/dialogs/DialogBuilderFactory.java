package com.dude.dms.ui.builder.dialogs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.docs.TextBlock;
import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.service.*;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.builder.Factory;
import com.dude.dms.updater.UpdateChecker;

import java.util.Map;
import java.util.Set;

public class DialogBuilderFactory extends Factory {

    private final DocService docService;
    private final TagService tagService;
    private final PlainTextRuleService plainTextRuleService;
    private final RegexRuleService regexRuleService;
    private final ChangelogService changelogService;
    private final UpdateChecker updateChecker;
    private final TextBlockService textBlockService;

    public DialogBuilderFactory(BuilderFactory builderFactory, DocService docService, TagService tagService, PlainTextRuleService plainTextRuleService,
                                RegexRuleService regexRuleService, ChangelogService changelogService, UpdateChecker updateChecker,
                                TextBlockService textBlockService) {
        super(builderFactory);
        this.tagService = tagService;
        this.plainTextRuleService = plainTextRuleService;
        this.regexRuleService = regexRuleService;
        this.docService = docService;
        this.changelogService = changelogService;
        this.updateChecker = updateChecker;
        this.textBlockService = textBlockService;
    }

    public ChangelogDialogBuilder changelog() {
        return new ChangelogDialogBuilder(changelogService, updateChecker);
    }

    public DocCreateDialogBuilder docCreate() {
        return new DocCreateDialogBuilder();
    }

    public DocEditDialogBuilder docEdit(Doc doc) {
        return new DocEditDialogBuilder(doc, docService, tagService);
    }

    public DocImageDialogBuilder docImage(Doc doc) {
        return new DocImageDialogBuilder(builderFactory, doc, textBlockService);
    }

    public DocTextDialogBuilder docText(Doc doc) {
        return new DocTextDialogBuilder(doc, textBlockService);
    }

    public PlainTextRuleDialogBuilder plainTextRule() {
        return new PlainTextRuleDialogBuilder(tagService, plainTextRuleService);
    }

    public RegexRuleDialogBuilder regexRule() {
        return new RegexRuleDialogBuilder(tagService, regexRuleService);
    }

    public RuleRunnerDialogBuilder ruleRunner(Map<Doc, Set<Tag>> result) {
        return new RuleRunnerDialogBuilder(result, docService);
    }

    public TagCreateDialogBuilder tagCreate() {
        return new TagCreateDialogBuilder(tagService);
    }

    public TagEditDialogBuilder tagEdit(Tag tag) {
        return new TagEditDialogBuilder(tag, tagService);
    }

    public TextBlockEditDialogBuilder textBlockEdit(TextBlock textBlock) {
        return new TextBlockEditDialogBuilder(textBlock, textBlockService);
    }
}