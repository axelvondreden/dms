package com.dude.dms.ui.views

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.containers.WordContainer
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.filter.AttributeFilter
import com.dude.dms.backend.service.AttributeFilterService
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.dsl.attributefilter.AttributeFilterParser
import com.dude.dms.brain.t
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.dialogs.DocSelectDialog
import com.dude.dms.ui.components.misc.AttributeFilterText
import com.dude.dms.utils.*
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import de.f0rce.ace.AceEditor
import de.f0rce.ace.enums.AceMode
import de.f0rce.ace.enums.AceTheme


@Route(value = Const.PAGE_ATTRIBUTE, layout = MainView::class)
@PageTitle("Attributes")
class AttributeView(
        private val attributeService: AttributeService,
        private val attributeFilterService: AttributeFilterService
) : VerticalLayout(), HasUrlParameter<String> {

    private var attribute: Attribute? = null
    private var attributeFilter: AttributeFilter? = null
    private var testDoc: DocContainer? = null

    private lateinit var nameTextField: TextField

    private lateinit var typeComboBox: ComboBox<Attribute.Type>

    private lateinit var requiredToggle: Checkbox

    private lateinit var saveButton: Button

    private lateinit var testText: AceEditor
    private lateinit var testGrid: Grid<WordContainer>
    private lateinit var testFinalValue: TextField
    private lateinit var testDocLabel: Label

    private lateinit var filter: AttributeFilterText

    init {
        setSizeFull()

        horizontalLayout {
            setWidthFull()
            alignItems = FlexComponent.Alignment.END

            nameTextField = textField(t("name"))
            typeComboBox = comboBox(t("attribute.type")) {
                setItems(*Attribute.Type.values())
                isPreventInvalidInput = true
                isAllowCustomValue = false
                addValueChangeListener { filter.refresh() }
            }
            requiredToggle = checkBox(t("attribute.required"))
            saveButton = button(t("save"), VaadinIcon.DISC.create()) {
                onLeftClick { save() }
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            }
        }
        filter = attributeFilterText {
            setWidthFull()
            onChange = {
                filterChange(it)
            }
        }
        card {
            setWidthFull()

            details(t("filter") + " Test") {
                isOpened = true
                style["width"] = "99%"
                style["height"] = "100%"
                style["padding"] = "5px"

                content {
                    setWidthFull()

                    horizontalLayout {
                        button(t("doc.select")) {
                            onLeftClick {
                                DocSelectDialog {
                                    testDoc = it
                                    testText.value = testDoc!!.getFullText()
                                    testDocLabel.text = testDoc!!.guid
                                    filter.refresh()
                                }.open()
                            }
                        }
                        testDocLabel = label("")
                    }
                    horizontalLayout {
                        alignItems = FlexComponent.Alignment.STRETCH
                        setSizeFull()
                        verticalLayout {
                            width = "70%"
                            label(t("doc.text"))
                            testText = aceEditor {
                                setSizeFull()
                                theme = AceTheme.dracula
                                mode = AceMode.text
                                isReadOnly = true
                            }
                        }
                        verticalLayout {
                            setSizeFull()
                            width = "30%"
                            label(t("words.matched"))
                            testGrid = grid {
                                setWidthFull()
                                addColumn { it.text }.setHeader(t("word"))
                                addItemClickListener {
                                    //testText.setSelection()
                                }
                            }
                            testFinalValue = textField(t("word.final")) {
                                setWidthFull()
                                isReadOnly = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun filterChange(result: AttributeFilterParser.ParseResult) {
        if (testDoc != null && result.attributeFilter != null && result.isValid) {
            val words = testDoc!!.getWordsForAttributeFilter(result.attributeFilter)
            testGrid.setItems(words)

            val filtered = words.filter {
                when (attribute!!.type) {
                    Attribute.Type.INT -> it.text.findInt() != null
                    Attribute.Type.FLOAT -> it.text.findDecimal() != null
                    Attribute.Type.DATE -> it.text.findDate() != null
                    else -> true
                }
            }
            val value = filtered.groupBy { it.text }.maxByOrNull { it.value.size }?.key
            if (value != null) {
                testFinalValue.value = when (typeComboBox.value) {
                    Attribute.Type.INT -> value.findInt().toString()
                    Attribute.Type.FLOAT -> value.findDecimal()!!.toFloat().toString()
                    Attribute.Type.DATE -> value.findDate().toString()
                    else -> value
                }
            }
        } else {
            testGrid.setItems(emptyList())
        }
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
        filter.refresh()
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