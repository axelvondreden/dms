package com.dude.dms.utils

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.WordContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.AttributeValue
import com.dude.dms.brain.dsl.hint.Hint
import com.dude.dms.ui.components.cards.DocCard
import com.dude.dms.ui.components.cards.DocImportCard
import com.dude.dms.ui.components.dialogs.AttributeCreateDialog
import com.dude.dms.ui.components.dialogs.DocDeleteDialog
import com.dude.dms.ui.components.dialogs.WordEditDialog
import com.dude.dms.ui.components.misc.*
import com.dude.dms.ui.components.tags.*
import com.github.appreciated.card.Card
import com.github.appreciated.card.label.SecondaryLabel
import com.github.juchar.colorpicker.ColorPickerFieldRaw
import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.init
import com.hilerio.ace.AceEditor
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.HasStyle
import com.vaadin.flow.component.progressbar.ProgressBar
import dev.mett.vaadin.tooltip.Tooltips

@VaadinDsl
fun HasComponents.tagLayout(
        tags: MutableSet<Tag> = mutableSetOf(),
        edit: Boolean = false,
        compact: Boolean = false,
        block: TagLayout.() -> Unit = {}
) = init(TagLayout(tags, edit, compact), block)

@VaadinDsl
fun HasComponents.tagLabel(tag: Tag, block: TagLabel.() -> Unit = {}) = init(TagLabel(tag), block)

@VaadinDsl
fun HasComponents.tagSelector(block: TagSelector.() -> Unit = {}) = init(TagSelector(), block)

@VaadinDsl
fun HasComponents.attributeSelector(block: AttributeSelector.() -> Unit = {}) = init(AttributeSelector(), block)

@VaadinDsl
fun HasComponents.colorPicker(label: String = "", block: ColorPickerFieldRaw.() -> Unit = {}) = init(ColorPickerFieldRaw(label), block)

@VaadinDsl
fun HasComponents.docPageSelector(max: Int = 1, block: DocPageSelector.() -> Unit = {}) = init(DocPageSelector(max), block)

@VaadinDsl
fun HasComponents.viewPageSelector(block: ViewPageSelector.() -> Unit = {}) = init(ViewPageSelector(), block)

@VaadinDsl
fun HasComponents.modeSelector(block: ModeSelector.() -> Unit = {}) = init(ModeSelector(), block)

@VaadinDsl
fun HasComponents.docImageEditor(block: DocImageEditor.() -> Unit = {}) = init(DocImageEditor(), block)

@VaadinDsl
fun HasComponents.docCard(docContainer: DocContainer, block: DocCard.() -> Unit = {}) = init(DocCard(docContainer), block)

@VaadinDsl
fun HasComponents.docImportCard(docContainer: DocContainer, block: DocImportCard.() -> Unit = {})
        = init(DocImportCard(docContainer), block)

@VaadinDsl
fun HasComponents.wordEditDialog(wordContainer: WordContainer, block: WordEditDialog.() -> Unit = {})
        = init(WordEditDialog(wordContainer), block)

@VaadinDsl
fun HasComponents.filterTestLayout(block: FilterTestLayout.() -> Unit = {}) = init(FilterTestLayout(), block)

@VaadinDsl
fun HasComponents.attributeValueLayout(imageEditor: DocImageEditor? = null, block: AttributeValueLayout.() -> Unit = {})
        = init(AttributeValueLayout(imageEditor), block)

@VaadinDsl
fun HasComponents.attributeValueSmallLayout(block: AttributeValueSmallLayout.() -> Unit = {})
        = init(AttributeValueSmallLayout(), block)

@VaadinDsl
fun HasComponents.attributeCreateDialog(block: AttributeCreateDialog.() -> Unit = {}) = init(AttributeCreateDialog(), block)

@VaadinDsl
fun HasComponents.attributeValueField(attributeValue: AttributeValue, imageEditor: DocImageEditor? = null, block: AttributeValueField.() -> Unit = {})
        = init(AttributeValueField(attributeValue, imageEditor), block)

@VaadinDsl
fun HasComponents.attributeValueLabel(attributeValue: AttributeValue, block: AttributeValueLabel.() -> Unit = {})
        = init(AttributeValueLabel(attributeValue), block)

@VaadinDsl
fun HasComponents.docDeleteDialog(docContainer: DocContainer, block: DocDeleteDialog.() -> Unit = {}) = init(DocDeleteDialog(docContainer), block)

@VaadinDsl
fun HasComponents.card(block: Card.() -> Unit = {}) = init(Card(), block)

@VaadinDsl
fun HasComponents.secondaryLabel(text: String?, block: SecondaryLabel.() -> Unit = {}) = init(SecondaryLabel(text), block)

@VaadinDsl
fun HasComponents.progressBar(block: ProgressBar.() -> Unit = {}) = init(ProgressBar(), block)

@VaadinDsl
fun <T> T.tooltip(text: String?) where T : Component, T : HasStyle {
    Tooltips.getCurrent().setTooltip(this, text)
}

@VaadinDsl
fun <T> T.showTooltip() where T : Component, T : HasStyle {
    Tooltips.getCurrent().showTooltip(this)
}

@VaadinDsl
fun <T> T.clearTooltips() where T : Component, T : HasStyle {
    Tooltips.getCurrent().removeTooltip(this)
}

@VaadinDsl
fun HasComponents.searchBar(hideSaveIcon: Boolean = false, block: DocSearchBar.() -> Unit = {}) = init(DocSearchBar(hideSaveIcon), block)

@VaadinDsl
fun HasComponents.attributeFilterText(block: AttributeFilterText.() -> Unit = {}) = init(AttributeFilterText(), block)

@VaadinDsl
fun HasComponents.tagFilterText(block: TagFilterText.() -> Unit = {}) = init(TagFilterText(), block)

@VaadinDsl
fun HasComponents.hintList(block: HintList.() -> Unit = {}) = init(HintList(), block)

@VaadinDsl
fun HasComponents.hintItem(hint: Hint, block: HintItem.() -> Unit = {}) = init(HintItem(hint), block)

@VaadinDsl
fun HasComponents.aceEditor(block: AceEditor.() -> Unit = {}) = init(AceEditor(), block)
