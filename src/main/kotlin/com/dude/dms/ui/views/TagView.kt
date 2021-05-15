package com.dude.dms.ui.views

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.filter.TagFilter
import com.dude.dms.backend.service.TagFilterService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.dialogs.DocMultiSelectDialog
import com.dude.dms.ui.components.misc.FilterTestLayout
import com.dude.dms.ui.components.misc.TagFilterText
import com.dude.dms.ui.components.tags.AttributeSelector
import com.dude.dms.utils.*
import com.github.juchar.colorpicker.ColorPickerFieldRaw
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route


@Route(value = Const.PAGE_TAG, layout = MainView::class)
@PageTitle("Tag")
class TagView(
    private val tagService: TagService,
    private val tagFilterService: TagFilterService
) : VerticalLayout(), HasUrlParameter<String> {

    private var tag: Tag? = null
    private var tagFilter: TagFilter? = null

    private lateinit var saveButton: Button

    private lateinit var runButton: Button

    private lateinit var nameTextField: TextField

    private lateinit var colorPicker: ColorPickerFieldRaw

    private lateinit var attributeSelector: AttributeSelector

    private val filter: TagFilterText

    private lateinit var filterTestLayout: FilterTestLayout

    init {
        setSizeFull()

        horizontalLayout {
            setWidthFull()
            verticalLayout(isPadding = false) {
                width = "20em"

                saveButton = button(t("save"), VaadinIcon.DISC.create()) {
                    setWidthFull()
                    onLeftClick { save() }
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                }
                runButton = button(t("run"), VaadinIcon.REFRESH.create()) {
                    setWidthFull()
                    onLeftClick { runForMissing() }
                    addThemeVariants(ButtonVariant.LUMO_SUCCESS)
                    tooltip(t("tag.run"))
                }
                nameTextField = textField(t("name")) { setWidthFull() }
                colorPicker = colorPicker(t("color")) { setWidthFull() }
            }
            attributeSelector = attributeSelector { setHeightFull() }
        }
        filter = tagFilterText {
            setWidthFull()
            onChange = {
                if (it.isValid) filterTestLayout.fill(overrideTagFilter = TagFilter(tag!!, filter.value))
            }
        }
        filterTestLayout = filterTestLayout()
    }

    private fun fill() {
        nameTextField.value = tag!!.name
        colorPicker.value = tag!!.color
        attributeSelector.selectedAttributes = tag!!.attributes
        filter.filter.value = tagFilter?.filter
    }

    private fun save() {
        if (nameTextField.isEmpty) {
            LOGGER.showError(t("name.missing"), UI.getCurrent())
            return
        }
        if (!filter.filter.isEmpty && !filter.isValid) {
            LOGGER.showError(t("condition.invalid"), UI.getCurrent())
            return
        }
        if (colorPicker.isEmpty) {
            LOGGER.showError(t("color.missing"), UI.getCurrent())
            return
        }
        tagFilter!!.filter = filter.filter.value
        tagFilterService.save(tagFilter!!)

        tag!!.name = nameTextField.value
        tag!!.color = colorPicker.value
        tag!!.attributes = attributeSelector.selectedAttributes
        tagService.save(tag!!)
        docService.findByTag(tag!!).forEach { docService.save(it) }
        LOGGER.showInfo(t("saved"), UI.getCurrent())
        fill()
    }

    private fun runForMissing() {
        if (tag!!.tagFilter != null) {
            val docs = docService.findByTagNot(tag!!).map { DocContainer(it) }
            val docs2 = docs.filter { docParser.filterTags(it, setOf(tag!!.tagFilter!!)).isNotEmpty() }
            DocMultiSelectDialog(docs2) { items ->
                items.mapNotNull { it.doc }.forEach {
                    it.tags = it.tags.plus(tag!!)
                    docService.save(it)
                }
            }.open()
        }
    }

    override fun setParameter(beforeEvent: BeforeEvent, t: String) {
        if (t.isNotEmpty()) {
            tag = tagService.load(t.toLong())
            if (tag != null) {
                val findByAttribute = tagFilterService.findByTag(tag!!)
                tagFilter = if (findByAttribute != null) {
                    findByAttribute
                } else {
                    val af = tagFilterService.create(TagFilter(tag!!, ""))
                    tag!!.tagFilter = af
                    tagService.save(tag!!)
                    af
                }
            }
            fill()
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(TagView::class.java)
    }
}