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
import com.dude.dms.ui.components.cards.*
import com.dude.dms.ui.components.dialogs.*
import com.dude.dms.ui.components.misc.*
import com.dude.dms.ui.components.standard.RegexField
import com.dude.dms.ui.components.tags.*
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
        tags: MutableSet<Tag> = mutableSetOf(),
        edit: Boolean = false,
        compact: Boolean = false,
        block: TagLayout.() -> Unit = {}
): TagLayout = init(TagLayout(tags, edit, compact), block)

fun HasComponents.tagLabel(tag: Tag, block: TagLabel.() -> Unit = {}): TagLabel = init(TagLabel(tag), block)

fun HasComponents.tagSelector(block: TagSelector.() -> Unit = {}): TagSelector = init(TagSelector(), block)

fun HasComponents.attributeSelector(block: AttributeSelector.() -> Unit = {}): AttributeSelector = init(AttributeSelector(), block)

fun HasComponents.plaintextRuleCreateDialog(block: PlainTextRuleCreateDialog.() -> Unit = {}): PlainTextRuleCreateDialog
        = init(PlainTextRuleCreateDialog(), block)

fun HasComponents.plaintextRuleEditDialog(plainTextRule: PlainTextRule, block: PlainTextRuleEditDialog.() -> Unit = {}): PlainTextRuleEditDialog
        = init(PlainTextRuleEditDialog(plainTextRule), block)

fun HasComponents.plaintextRuleCard(plainTextRule: PlainTextRule, block: PlainTextRuleCard.() -> Unit = {}): PlainTextRuleCard
        = init(PlainTextRuleCard(plainTextRule), block)

fun HasComponents.regexRuleCreateDialog(block: RegexRuleCreateDialog.() -> Unit = {}): RegexRuleCreateDialog = init(RegexRuleCreateDialog(), block)

fun HasComponents.regexRuleEditDialog(regexRule: RegexRule, block: RegexRuleEditDialog.() -> Unit = {}): RegexRuleEditDialog
        = init(RegexRuleEditDialog(regexRule), block)

fun HasComponents.regexRuleCard(regexRule: RegexRule, block: RegexRuleCard.() -> Unit = {}): RegexRuleCard = init(RegexRuleCard(regexRule), block)

fun HasComponents.mailFilterCreateDialog(block: MailFilterCreateDialog.() -> Unit = {}): MailFilterCreateDialog
        = init(MailFilterCreateDialog(), block)

fun HasComponents.mailFilterEditDialog(mailFilter: MailFilter, block: MailFilterEditDialog.() -> Unit = {}): MailFilterEditDialog
        = init(MailFilterEditDialog(mailFilter), block)

fun HasComponents.mailFilterCard(mailFilter: MailFilter, block: MailFilterCard.() -> Unit = {}): MailFilterCard
        = init(MailFilterCard(mailFilter), block)

fun HasComponents.ruleRunnerDialog(result: Map<Doc, Set<TagContainer>>, block: RuleRunnerDialog.() -> Unit = {}): RuleRunnerDialog
        = init(RuleRunnerDialog(result), block)

fun HasComponents.regexDialog(textField: TextField, block: RegexDialog.() -> Unit = {}): RegexDialog = init(RegexDialog(textField), block)

fun HasComponents.regexField(label: String = "", block: RegexField.() -> Unit = {}): RegexField = init(RegexField(label), block)

fun HasComponents.colorPicker(label: String = "", block: ColorPickerFieldRaw.() -> Unit = {}): ColorPickerFieldRaw
        = init(ColorPickerFieldRaw(label), block)

fun HasComponents.docPageSelector(max: Int = 1, block: DocPageSelector.() -> Unit = {}): DocPageSelector = init(DocPageSelector(max), block)

fun HasComponents.viewPageSelector(block: ViewPageSelector.() -> Unit = {}): ViewPageSelector = init(ViewPageSelector(), block)

fun HasComponents.modeSelector(block: ModeSelector.() -> Unit = {}): ModeSelector = init(ModeSelector(), block)

fun HasComponents.docImageEditor(block: DocImageEditor.() -> Unit = {}): DocImageEditor = init(DocImageEditor(), block)

fun HasComponents.docInfoLayout(docImageEditor: DocImageEditor, block: DocInfoLayout.() -> Unit = {}): DocInfoLayout
        = init(DocInfoLayout(docImageEditor), block)

fun HasComponents.docCard(docContainer: DocContainer, block: DocCard.() -> Unit = {}): DocCard = init(DocCard(docContainer), block)

fun HasComponents.docImportCard(docContainer: DocContainer, block: DocImportCard.() -> Unit = {}): DocImportCard
        = init(DocImportCard(docContainer), block)

fun HasComponents.wordEditDialog(wordContainer: WordContainer, block: WordEditDialog.() -> Unit = {}): WordEditDialog
        = init(WordEditDialog(wordContainer), block)

fun HasComponents.attributeValueLayout(imageEditor: DocImageEditor? = null, block: AttributeValueLayout.() -> Unit = {}): AttributeValueLayout
        = init(AttributeValueLayout(imageEditor), block)

fun HasComponents.attributeValueSmallLayout(block: AttributeValueSmallLayout.() -> Unit = {}): AttributeValueSmallLayout
        = init(AttributeValueSmallLayout(), block)

fun HasComponents.attributeCreateDialog(block: AttributeCreateDialog.() -> Unit = {}): AttributeCreateDialog
        = init(AttributeCreateDialog(), block)

fun HasComponents.attributeValueField(
        attributeValue: AttributeValue,
        imageEditor: DocImageEditor? = null,
        block: AttributeValueField.() -> Unit = {}
): AttributeValueField = init(AttributeValueField(attributeValue, imageEditor), block)

fun HasComponents.attributeValueLabel(attributeValue: AttributeValue, block: AttributeValueLabel.() -> Unit = {}): AttributeValueLabel
        = init(AttributeValueLabel(attributeValue), block)

fun HasComponents.docDeleteDialog(docContainer: DocContainer, block: DocDeleteDialog.() -> Unit = {}): DocDeleteDialog = init(DocDeleteDialog(docContainer), block)

fun HasComponents.docImportPreview(block: DocImportPreview.() -> Unit = {}): DocImportPreview = init(DocImportPreview(), block)

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