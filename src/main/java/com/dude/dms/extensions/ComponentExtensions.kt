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
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.progressbar.ProgressBar
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.textfield.TextField
import dev.mett.vaadin.tooltip.Tooltips
import org.vaadin.gatanaso.MultiselectComboBox

fun HasComponents.tagLayout(
        tags: MutableSet<Tag> = mutableSetOf(),
        edit: Boolean = false,
        compact: Boolean = false,
        block: TagLayout.() -> Unit = {}
) = init(TagLayout(tags, edit, compact), block)

fun HasComponents.tagLabel(tag: Tag, block: TagLabel.() -> Unit = {}) = init(TagLabel(tag), block)

fun HasComponents.tagSelector(block: TagSelector.() -> Unit = {}) = init(TagSelector(), block)

fun HasComponents.attributeSelector(block: AttributeSelector.() -> Unit = {}) = init(AttributeSelector(), block)

fun HasComponents.plaintextRuleCreateDialog(block: PlainTextRuleCreateDialog.() -> Unit = {})
        = init(PlainTextRuleCreateDialog(), block)

fun HasComponents.plaintextRuleEditDialog(plainTextRule: PlainTextRule, block: PlainTextRuleEditDialog.() -> Unit = {})
        = init(PlainTextRuleEditDialog(plainTextRule), block)

fun HasComponents.plaintextRuleCard(plainTextRule: PlainTextRule, block: PlainTextRuleCard.() -> Unit = {})
        = init(PlainTextRuleCard(plainTextRule), block)

fun HasComponents.regexRuleCreateDialog(block: RegexRuleCreateDialog.() -> Unit = {})
        = init(RegexRuleCreateDialog(), block)

fun HasComponents.regexRuleEditDialog(regexRule: RegexRule, block: RegexRuleEditDialog.() -> Unit = {})
        = init(RegexRuleEditDialog(regexRule), block)

fun HasComponents.regexRuleCard(regexRule: RegexRule, block: RegexRuleCard.() -> Unit = {})
        = init(RegexRuleCard(regexRule), block)

fun HasComponents.mailFilterCreateDialog(block: MailFilterCreateDialog.() -> Unit = {})
        = init(MailFilterCreateDialog(), block)

fun HasComponents.mailFilterEditDialog(mailFilter: MailFilter, block: MailFilterEditDialog.() -> Unit = {})
        = init(MailFilterEditDialog(mailFilter), block)

fun HasComponents.mailFilterCard(mailFilter: MailFilter, block: MailFilterCard.() -> Unit = {})
        = init(MailFilterCard(mailFilter), block)

fun HasComponents.ruleRunnerDialog(result: Map<Doc, Set<TagContainer>>, block: RuleRunnerDialog.() -> Unit = {})
        = init(RuleRunnerDialog(result), block)

fun HasComponents.regexDialog(textField: TextField, block: RegexDialog.() -> Unit = {}) = init(RegexDialog(textField), block)

fun HasComponents.regexField(label: String = "", block: RegexField.() -> Unit = {}) = init(RegexField(label), block)

fun HasComponents.colorPicker(label: String = "", block: ColorPickerFieldRaw.() -> Unit = {}) = init(ColorPickerFieldRaw(label), block)

fun HasComponents.docPageSelector(max: Int = 1, block: DocPageSelector.() -> Unit = {}) = init(DocPageSelector(max), block)

fun HasComponents.viewPageSelector(block: ViewPageSelector.() -> Unit = {}) = init(ViewPageSelector(), block)

fun HasComponents.modeSelector(block: ModeSelector.() -> Unit = {}) = init(ModeSelector(), block)

fun HasComponents.docImageEditor(block: DocImageEditor.() -> Unit = {}) = init(DocImageEditor(), block)

fun HasComponents.docInfoLayout(docImageEditor: DocImageEditor, block: DocInfoLayout.() -> Unit = {})
        = init(DocInfoLayout(docImageEditor), block)

fun HasComponents.docCard(docContainer: DocContainer, block: DocCard.() -> Unit = {}) = init(DocCard(docContainer), block)

fun HasComponents.docImportCard(docContainer: DocContainer, block: DocImportCard.() -> Unit = {})
        = init(DocImportCard(docContainer), block)

fun HasComponents.wordEditDialog(wordContainer: WordContainer, block: WordEditDialog.() -> Unit = {})
        = init(WordEditDialog(wordContainer), block)

fun HasComponents.attributeValueLayout(imageEditor: DocImageEditor? = null, block: AttributeValueLayout.() -> Unit = {})
        = init(AttributeValueLayout(imageEditor), block)

fun HasComponents.attributeValueSmallLayout(block: AttributeValueSmallLayout.() -> Unit = {})
        = init(AttributeValueSmallLayout(), block)

fun HasComponents.attributeCreateDialog(block: AttributeCreateDialog.() -> Unit = {}) = init(AttributeCreateDialog(), block)

fun HasComponents.attributeValueField(attributeValue: AttributeValue, imageEditor: DocImageEditor? = null, block: AttributeValueField.() -> Unit = {})
        = init(AttributeValueField(attributeValue, imageEditor), block)

fun HasComponents.attributeValueLabel(attributeValue: AttributeValue, block: AttributeValueLabel.() -> Unit = {})
        = init(AttributeValueLabel(attributeValue), block)

fun HasComponents.docDeleteDialog(docContainer: DocContainer, block: DocDeleteDialog.() -> Unit = {}) = init(DocDeleteDialog(docContainer), block)

fun HasComponents.docImportPreview(block: DocImportPreview.() -> Unit = {}) = init(DocImportPreview(), block)

fun HasComponents.card(block: Card.() -> Unit = {}) = init(Card(), block)

fun HasComponents.secondaryLabel(text: String?, block: SecondaryLabel.() -> Unit = {}) = init(SecondaryLabel(text), block)

fun HasComponents.progressBar(block: ProgressBar.() -> Unit = {}) = init(ProgressBar(), block)

fun <T> HasComponents.multiSelectComboBox(label: String, items: Collection<T>, block: MultiselectComboBox<T>.() -> Unit = {})
        = init(MultiselectComboBox<T>(label, items), block)

fun HasComponents.radioButtonGroup(block: RadioButtonGroup<String>.() -> Unit = {})= init(RadioButtonGroup(), block)

fun <T> T.tooltip(text: String?) where T : Component, T : HasStyle {
    Tooltips.getCurrent().setTooltip(this, text)
}