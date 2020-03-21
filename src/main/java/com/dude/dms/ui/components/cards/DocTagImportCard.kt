package com.dude.dms.ui.components.cards

import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.options.Options
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.dialogs.docimport.DocImportDialog
import com.github.appreciated.card.Card
import com.github.appreciated.card.label.TitleLabel
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import java.util.*

class DocTagImportCard(
        builderFactory: BuilderFactory,
        private val tagService: TagService,
        private val fileContainer: DocImportDialog.FileContainer
) : Card() {

    private val label = TitleLabel(fileContainer.file.name).apply {
        setAlignSelf(FlexComponent.Alignment.CENTER)
    }

    private val date = DatePicker().apply {
        fileContainer.date?.let { value = it }
        addValueChangeListener { it.value?.let { date -> fileContainer.date = date } }
        locale = Locale.forLanguageTag(Options.get().view.locale)
        element.style["padding"] = "10px"
    }

    private val tagContainer = builderFactory.tags().container(fileContainer.tags, true) { event ->
        event.source.children.findFirst().ifPresent { elem -> fileContainer.tags.remove(tagService.findByName(elem.element.text!!)) }
        fill()
    }.apply { setWidthFull() }

    init {
        setWidthFull()
        fill()
    }

    private fun fill() {
        tagContainer.setTags(fileContainer.tags)
        val wrapper = VerticalLayout(
                HorizontalLayout(label, date).apply { setWidthFull(); alignItems = FlexComponent.Alignment.CENTER },
                tagContainer
        ).apply {
            setWidthFull()
            alignItems = FlexComponent.Alignment.CENTER
            isPadding = false
            isSpacing = false
        }
        add(wrapper)
    }
}