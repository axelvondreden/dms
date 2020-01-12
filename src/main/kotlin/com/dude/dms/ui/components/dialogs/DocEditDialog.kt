package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.standard.DmsDatePicker
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField

class DocEditDialog(builderFactory: BuilderFactory, private val doc: Doc, private val docService: DocService) : EventDialog<Doc>() {

    private val datePicker = DmsDatePicker("Date").apply {
        setWidthFull()
        value = doc.documentDate
    }

    private val tagSelector = builderFactory.tags().selector().forDoc(doc).build().apply {
        height = "25vw"
    }

    private val attributeValueContainer = builderFactory.attributes().valueContainer(doc).build().apply {
        setSizeFull()
        maxHeight = "40vh"
    }

    init {
        width = "40vw"
        val guidTextField = TextField("GUID").apply {
            setWidthFull()
            isReadOnly = true
            value = doc.guid
        }
        val saveButton = Button("Save", VaadinIcon.DISC.create()) { save() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        }
        val cancelButton = Button("Close", VaadinIcon.CLOSE.create()) { close() }.apply {
            setWidthFull()
            addThemeVariants(ButtonVariant.LUMO_ERROR)
        }
        val tagDetails = Details("Tags", tagSelector).apply {
            element.style["width"] = "100%"
        }
        val attributeDetails = Details("Attributes", attributeValueContainer).apply {
            element.style["width"] = "100%"
        }
        val fieldWrapper = HorizontalLayout(guidTextField, datePicker).apply {
            setWidthFull()
        }
        val buttonWrapper = HorizontalLayout(saveButton, cancelButton).apply {
            setWidthFull()
        }
        val wrapper = VerticalLayout(fieldWrapper, tagDetails, attributeDetails, buttonWrapper).apply {
            setSizeFull()
            isPadding = false
            isSpacing = false
        }
        add(wrapper)
    }

    private fun save() {
        if (attributeValueContainer.validate()) {
            doc.documentDate = datePicker.value
            doc.tags = tagSelector.selectedTags
            docService.save(doc)
            triggerEditEvent(doc)
            close()
        }
    }
}