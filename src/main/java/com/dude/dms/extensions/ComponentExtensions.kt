package com.dude.dms.extensions

import com.dude.dms.backend.containers.DocContainer
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
import com.dude.dms.ui.components.cards.*
import com.dude.dms.ui.components.dialogs.*
import com.dude.dms.ui.components.misc.*
import com.dude.dms.ui.components.standard.RegexField
import com.dude.dms.ui.components.tags.*
import com.dude.dms.updater.UpdateChecker
import com.github.appreciated.card.Card
import com.github.appreciated.card.label.SecondaryLabel
import com.github.juchar.colorpicker.ColorPickerFieldRaw
import com.github.mvysny.karibudsl.v10.init
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.progressbar.ProgressBar
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

fun HasComponents.docPageSelector(max: Int = 1, block: DocPageSelector.() -> Unit = {}): DocPageSelector = init(DocPageSelector(max), block)

fun HasComponents.viewPageSelector(block: ViewPageSelector.() -> Unit = {}): ViewPageSelector = init(ViewPageSelector(), block)

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
        attributeValueService: AttributeValueService,
        docImageEditor: DocImageEditor,
        block: DocInfoLayout.() -> Unit = {}
): DocInfoLayout = init(DocInfoLayout(tagService, attributeValueService, docImageEditor), block)

fun HasComponents.docCard(
        docService: DocService,
        tagService: TagService,
        mailService: MailService,
        docContainer: DocContainer,
        imageDialog: DocImageDialog,
        block: DocCard.() -> Unit = {}
): DocCard = init(DocCard(docService, tagService, mailService, docContainer, imageDialog), block)

fun HasComponents.docImportCard(
        docContainer: DocContainer,
        block: DocImportCard.() -> Unit = {}
): DocImportCard = init(DocImportCard(docContainer), block)

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

fun HasComponents.attributeValueSmallLayout(
        block: AttributeValueSmallLayout.() -> Unit = {}
): AttributeValueSmallLayout = init(AttributeValueSmallLayout(), block)

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

fun HasComponents.docDeleteDialog(
        docService: DocService,
        mailService: MailService,
        docContainer: DocContainer,
        block: DocDeleteDialog.() -> Unit = {}
): DocDeleteDialog = init(DocDeleteDialog(docService, mailService, docContainer), block)

fun HasComponents.docImportPreview(
        tagService: TagService,
        attributeValueService: AttributeValueService,
        lineService: LineService,
        wordService: WordService,
        docParser: DocParser,
        fileManager: FileManager,
        block: DocImportPreview.() -> Unit = {}
): DocImportPreview = init(DocImportPreview(tagService, attributeValueService, lineService, wordService, docParser, fileManager), block)

fun HasComponents.confirmDialog(
        message: String,
        confirmText: String,
        icon: VaadinIcon,
        theme: ButtonVariant,
        event: (ClickEvent<Button>) -> Unit,
        block: ConfirmDialog.() -> Unit = {}
): ConfirmDialog = init(ConfirmDialog(message, confirmText, icon, theme, event), block)

fun HasComponents.card(block: Card.() -> Unit = {}): Card = init(Card(), block)

fun HasComponents.secondaryLabel(text: String?, block: SecondaryLabel.() -> Unit = {}): SecondaryLabel = init(SecondaryLabel(text), block)

fun HasComponents.progressBar(block: ProgressBar.() -> Unit = {}): ProgressBar = init(ProgressBar(), block)