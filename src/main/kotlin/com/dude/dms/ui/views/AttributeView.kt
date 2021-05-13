package com.dude.dms.ui.views

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.filter.AttributeFilter
import com.dude.dms.backend.service.AttributeFilterService
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.misc.AttributeFilterText
import com.dude.dms.ui.components.misc.FilterTestLayout
import com.dude.dms.utils.attributeFilterText
import com.dude.dms.utils.filterTestLayout
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route


@Route(value = Const.PAGE_ATTRIBUTE, layout = MainView::class)
@PageTitle("Attribute")
class AttributeView(
        private val attributeService: AttributeService,
        private val attributeFilterService: AttributeFilterService
) : VerticalLayout(), HasUrlParameter<String> {

    private var attribute: Attribute? = null
    private var attributeFilter: AttributeFilter? = null

    private lateinit var nameTextField: TextField

    private lateinit var typeComboBox: ComboBox<Attribute.Type>

    private lateinit var requiredToggle: Checkbox

    private lateinit var saveButton: Button

    private val filter: AttributeFilterText

    private val filterTestLayout: FilterTestLayout

    init {
        setSizeFull()

        horizontalLayout {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END

            saveButton = button(t("save"), VaadinIcon.DISC.create()) {
                onLeftClick { save() }
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            }
            nameTextField = textField(t("name"))
            typeComboBox = comboBox(t("attribute.type")) {
                setItems(*Attribute.Type.values())
                isPreventInvalidInput = true
                isAllowCustomValue = false
            }
            requiredToggle = checkBox(t("attribute.required"))
        }
        filter = attributeFilterText {
            setWidthFull()
        }
        filterTestLayout = filterTestLayout()
    }



    private fun fill() {
        nameTextField.value = attribute?.name
        typeComboBox.value = attribute?.type
        requiredToggle.value = attribute?.isRequired
        filter.filter.value = attributeFilter?.filter
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
        attributeFilter!!.filter = filter.filter.value
        attributeFilterService.save(attributeFilter!!)

        attribute!!.name = nameTextField.value
        attribute!!.type = typeComboBox.value
        attribute!!.isRequired = requiredToggle.value
        attributeService.save(attribute!!)
        LOGGER.showInfo(t("saved"), UI.getCurrent())
        fill()
        filterTestLayout.refresh()
    }

    override fun setParameter(beforeEvent: BeforeEvent, t: String) {
        if (t.isNotEmpty()) {
            attribute = attributeService.load(t.toLong())
            if (attribute != null) {
                val findByAttribute = attributeFilterService.findByAttribute(attribute!!)
                attributeFilter = if (findByAttribute != null) {
                    findByAttribute
                } else {
                    val af = attributeFilterService.create(AttributeFilter(attribute!!, ""))
                    attribute!!.attributeFilter = af
                    attributeService.save(attribute!!)
                    af
                }
            }
            fill()
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(AttributeView::class.java)
    }
}