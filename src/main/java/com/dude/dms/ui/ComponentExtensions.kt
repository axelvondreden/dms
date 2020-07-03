package com.dude.dms.ui

import com.dude.dms.backend.containers.TagContainer
import com.dude.dms.backend.containers.WordContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.service.*
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.events.EventManager
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.parsing.PlainTextRuleValidator
import com.dude.dms.brain.parsing.RegexRuleValidator
import com.dude.dms.ui.components.cards.MailFilterCard
import com.dude.dms.ui.components.cards.PlainTextRuleCard
import com.dude.dms.ui.components.cards.RegexRuleCard
import com.dude.dms.ui.components.dialogs.*
import com.dude.dms.ui.components.misc.DocImageEditor
import com.dude.dms.ui.components.misc.DocInfoLayout
import com.dude.dms.ui.components.misc.DocPageSelector
import com.dude.dms.ui.components.misc.ModeSelector
import com.dude.dms.ui.components.standard.RegexField
import com.dude.dms.ui.components.tags.*
import com.github.appreciated.card.Card
import com.github.juchar.colorpicker.ColorPickerFieldRaw
import com.github.mvysny.karibudsl.v10.init
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.textfield.TextField

fun HasComponents.tagLayout(
        tagService: TagService,
        tags: MutableSet<Tag> = mutableSetOf(),
        edit: Boolean = false,
        compact: Boolean = false,
        block: TagLayout.() -> Unit = {}
): TagLayout = init(TagLayout(tagService, tags, edit, compact), block)

fun HasComponents.tagLabel(tag: Tag, block: TagLabel.() -> Unit = {}): TagLabel = init(TagLabel(tag), block)

fun HasComponents.tagSelector(tagService: TagService, block: TagSelector.() -> Unit = {}): TagSelector = init(TagSelector(tagService), block)

fun HasComponents.attributeSelector(
        attributeService: AttributeService,
        eventManager: EventManager,
        block: AttributeSelector.() -> Unit = {}
): AttributeSelector = init(AttributeSelector(attributeService, eventManager), block)

fun HasComponents.plaintextRuleCreateDialog(
        plainTextRuleService: PlainTextRuleService,
        tagService: TagService,
        block: PlainTextRuleCreateDialog.() -> Unit = {}
): PlainTextRuleCreateDialog = init(PlainTextRuleCreateDialog(plainTextRuleService, tagService), block)

fun HasComponents.plaintextRuleEditDialog(
        plainTextRuleService: PlainTextRuleService,
        tagService: TagService,
        plainTextRule: PlainTextRule,
        block: PlainTextRuleEditDialog.() -> Unit = {}
): PlainTextRuleEditDialog = init(PlainTextRuleEditDialog(plainTextRuleService, tagService, plainTextRule), block)

fun HasComponents.plaintextRuleCard(
        docService: DocService,
        tagService: TagService,
        plainTextRuleService: PlainTextRuleService,
        plainTextRule: PlainTextRule,
        plainTextRuleValidator: PlainTextRuleValidator,
        eventManager: EventManager,
        block: PlainTextRuleCard.() -> Unit = {}
): PlainTextRuleCard = init(PlainTextRuleCard(docService, tagService, plainTextRuleService, plainTextRule, plainTextRuleValidator, eventManager), block)

fun HasComponents.regexRuleCreateDialog(
        regexRuleService: RegexRuleService,
        tagService: TagService,
        block: RegexRuleCreateDialog.() -> Unit = {}
): RegexRuleCreateDialog = init(RegexRuleCreateDialog(regexRuleService, tagService), block)

fun HasComponents.regexRuleEditDialog(
        regexRuleService: RegexRuleService,
        tagService: TagService,
        regexRule: RegexRule,
        block: RegexRuleEditDialog.() -> Unit = {}
): RegexRuleEditDialog = init(RegexRuleEditDialog(regexRuleService, tagService, regexRule), block)

fun HasComponents.regexRuleCard(
        docService: DocService,
        tagService: TagService,
        regexRuleService: RegexRuleService,
        regexRule: RegexRule,
        regexRuleValidator: RegexRuleValidator,
        eventManager: EventManager,
        block: RegexRuleCard.() -> Unit = {}
): RegexRuleCard = init(RegexRuleCard(docService, tagService, regexRuleService, regexRule, regexRuleValidator, eventManager), block)

fun HasComponents.mailFilterCreateDialog(
        mailFilterService: MailFilterService,
        mailManager: MailManager,
        block: MailFilterCreateDialog.() -> Unit = {}
): MailFilterCreateDialog = init(MailFilterCreateDialog(mailFilterService, mailManager), block)

fun HasComponents.mailFilterEditDialog(
        mailFilterService: MailFilterService,
        mailFilter: MailFilter,
        mailManager: MailManager,
        block: MailFilterEditDialog.() -> Unit = {}
): MailFilterEditDialog = init(MailFilterEditDialog(mailFilterService, mailFilter, mailManager), block)

fun HasComponents.mailFilterCard(
        mailFilterService: MailFilterService,
        mailFilter: MailFilter,
        mailManager: MailManager,
        block: MailFilterCard.() -> Unit = {}
): MailFilterCard = init(MailFilterCard(mailFilterService, mailFilter, mailManager), block)

fun HasComponents.ruleRunnerDialog(
        docService: DocService,
        result: Map<Doc, Set<TagContainer>>,
        block: RuleRunnerDialog.() -> Unit = {}
): RuleRunnerDialog = init(RuleRunnerDialog(docService, result), block)

fun HasComponents.regexDialog(textField: TextField, block: RegexDialog.() -> Unit = {}): RegexDialog = init(RegexDialog(textField), block)

fun HasComponents.regexField(label: String = "", block: RegexField.() -> Unit = {}): RegexField = init(RegexField(label), block)

fun HasComponents.colorPicker(label: String = "", block: ColorPickerFieldRaw.() -> Unit = {}): ColorPickerFieldRaw = init(ColorPickerFieldRaw(label), block)

fun HasComponents.pageSelector(max: Int = 1, block: DocPageSelector.() -> Unit = {}): DocPageSelector = init(DocPageSelector(max), block)

fun HasComponents.modeSelector(block: ModeSelector.() -> Unit = {}): ModeSelector = init(ModeSelector(), block)

fun HasComponents.docImageEditor(
        lineService: LineService,
        wordService: WordService,
        docParser: DocParser,
        fileManager: FileManager,
        block: DocImageEditor.() -> Unit = {}
): DocImageEditor = init(DocImageEditor(lineService, wordService, docParser, fileManager), block)

fun HasComponents.docInfoLayout(
        tagService: TagService,
        docImageEditor: DocImageEditor,
        block: DocInfoLayout.() -> Unit = {}
): DocInfoLayout = init(DocInfoLayout(tagService, docImageEditor), block)

fun HasComponents.wordEditDialog(
        wordService: WordService,
        wordContainer: WordContainer,
        block: WordEditDialog.() -> Unit = {}
): WordEditDialog = init(WordEditDialog(wordService, wordContainer), block)

fun HasComponents.attributeValueLayout(
        attributeValueService: AttributeValueService,
        imageEditor: DocImageEditor? = null,
        block: AttributeValueLayout.() -> Unit = {}
): AttributeValueLayout = init(AttributeValueLayout(attributeValueService, imageEditor), block)

fun HasComponents.attributeCreateDialog(
        attributeService: AttributeService,
        block: AttributeCreateDialog.() -> Unit = {}
): AttributeCreateDialog = init(AttributeCreateDialog(attributeService), block)

fun HasComponents.attributeValueField(
        attributeValueService: AttributeValueService,
        attributeValue: AttributeValue,
        imageEditor: DocImageEditor? = null,
        block: AttributeValueField.() -> Unit = {}
): AttributeValueField = init(AttributeValueField(attributeValueService, attributeValue, imageEditor), block)

fun HasComponents.attributeValueLabel(
        attributeValue: AttributeValue,
        block: AttributeValueLabel.() -> Unit = {}
): AttributeValueLabel = init(AttributeValueLabel(attributeValue), block)

fun HasComponents.card(block: Card.() -> Unit = {}): Card = init(Card(), block)