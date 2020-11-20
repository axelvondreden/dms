package com.dude.dms.ui.views

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.rules.Condition
import com.dude.dms.backend.data.rules.ConditionType
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.ConditionService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.cards.ConditionCard
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route


@Route(value = Const.PAGE_ATTRIBUTE, layout = MainView::class)
@PageTitle("Attributes")
class AttributeView(
        private val attributeService: AttributeService,
        private val conditionService: ConditionService
) : VerticalLayout(), HasUrlParameter<String> {

    private var attribute: Attribute? = null

    private lateinit var nameTextField: TextField

    private lateinit var typeComboBox: ComboBox<Attribute.Type>

    private lateinit var requiredToggle: Checkbox

    private lateinit var saveButton: Button

    private var conditionLayout: VerticalLayout

    init {
        horizontalLayout {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END

            nameTextField = textField(t("name"))
            typeComboBox = comboBox(t("attribute.type")) {
                setItems(*Attribute.Type.values())
                isPreventInvalidInput = true
                isAllowCustomValue = false
            }
            requiredToggle = checkBox(t("attribute.required"))
            saveButton = button(t("save"), VaadinIcon.DISC.create()) {
                onLeftClick { save() }
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            }
        }
        conditionLayout = verticalLayout { setSizeFull() }
    }

    private fun fill() {
        nameTextField.value = attribute?.name
        typeComboBox.value = attribute?.type
        requiredToggle.value = attribute?.isRequired
        conditionLayout.removeAll()
        if (attribute?.condition != null) {
            conditionLayout.add(Label(t("attribute.rule.description")))
            conditionLayout.add(ConditionCard(attribute!!.condition!!))
        } else {
            conditionLayout.add(Button(t("condition.new"), VaadinIcon.PLUS.create()) {
                attribute!!.condition = Condition(attribute!!, type = ConditionType.EQUALS)
                fill()
            })
        }
    }

    private fun save() {
        if (nameTextField.isEmpty) {
            LOGGER.showError(t("name.missing"), UI.getCurrent())
            return
        }
        if (attribute?.condition != null && !attribute!!.condition!!.isValid()) {
            LOGGER.showError(t("condition.invalid"), UI.getCurrent())
            return
        }
        attribute!!.name = nameTextField.value
        attribute!!.type = typeComboBox.value
        attribute!!.isRequired = requiredToggle.value
        attribute!!.condition?.let(conditionService::save)
        attributeService.save(attribute!!)
        LOGGER.showInfo(t("saved"), UI.getCurrent())
        fill()
    }

    override fun setParameter(beforeEvent: BeforeEvent, t: String) {
        if (t.isNotEmpty()) {
            attribute = attributeService.findByName(t)
            fill()
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(AttributeView::class.java)
    }
}