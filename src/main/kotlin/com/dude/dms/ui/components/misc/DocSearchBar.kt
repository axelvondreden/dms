package com.dude.dms.ui.components.misc

import com.dude.dms.brain.dsl.docsearch.DocSearchParser
import com.dude.dms.brain.t
import com.dude.dms.ui.components.dialogs.QuerySaveDialog
import com.dude.dms.utils.clearTooltips
import com.dude.dms.utils.hintList
import com.dude.dms.utils.tooltip
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.iconButton
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode


class DocSearchBar(private val hideSaveIcon: Boolean = false) : VerticalLayout() {

    lateinit var filter: TextField
    private lateinit var saveButton: Button
    private lateinit var hintList: HintList
    private var statusIcon = VaadinIcon.CHECK.create().apply { color = "var(--lumo-success-text-color)" }

    var onChange: ((String) -> Unit)? = null

    private val searchParser = DocSearchParser()

    init {
        isPadding = false
        setWidthFull()
        horizontalLayout(isPadding = false) {
            alignItems = FlexComponent.Alignment.CENTER
            setWidthFull()

            saveButton = iconButton(VaadinIcon.DISC.create()) {
                tooltip(t("save"))
                onLeftClick {
                    QuerySaveDialog(filter.value).open()
                }
                isEnabled = false
                if (hideSaveIcon) isVisible = false
            }
            filter = textField {
                setWidthFull()
                isClearButtonVisible = true
                placeholder = t("search")
                valueChangeMode = ValueChangeMode.EAGER
                element.setAttribute(
                    "onkeydown", """
                        if (event.key == 'ArrowDown' || event.key == 'ArrowUp') {
                            return false;
                        }
                        return true;
                        """.trimIndent()
                )
                addFocusListener {
                    hintList.isVisible = true
                }
                addBlurListener {
                    hintList.isVisible = false
                }
                addKeyDownListener(Key.ARROW_DOWN, {
                    hintList.down()
                })
                addKeyDownListener(Key.ARROW_UP, {
                    hintList.up()
                })
                addKeyDownListener(Key.ENTER, {
                    hintList.select()
                })
                prefixComponent = statusIcon
                addValueChangeListener { searchTextChange(it.value) }
            }
        }
        hintList = hintList {
            isVisible = false
            maxWidth = "30em"
            style["position"] = "absolute"
            style["top"] = "30px"
            style["backgroundColor"] = "var(--lumo-base-color)"
            style["zIndex"] = "1"
            style["border"] = "2px solid var(--lumo-contrast-20pct)"
            setHints(searchParser.getHints())
            onSelect = {
                val current = filter.value
                if (current.endsWith(" ")) {
                    filter.value = "$current${it.text} "
                } else {
                    var itCopy = it.text
                    while (!current.endsWith(itCopy, ignoreCase = true)) {
                        itCopy = itCopy.dropLast(1)
                    }
                    filter.value = current.dropLast(itCopy.length) + it.text + " "
                }
                if (it.caretBackwardsMovement > 0) {
                    val index = filter.value.length - 1 - it.caretBackwardsMovement
                    filter.element.executeJs("this.shadowRoot.children[1].children[1].children[1].children[0].setSelectionRange($index, $index)")
                }
            }
        }
    }

    private fun searchTextChange(newValue: String) {
        val result = searchParser.setInput(newValue)
        val hintResult = searchParser.getHints()
        hintList.setHints(hintResult)
        statusIcon.clearTooltips()
        if (newValue.isBlank()) {
            onChange?.invoke("")
            saveButton.isEnabled = false
            setSearchStatusSuccess("")
        } else {
            if (result.isValid) {
                onChange?.invoke(result.docSearch!!.translate())
                saveButton.isEnabled = true
                setSearchStatusSuccess(result.docSearch!!.translate())
            } else {
                saveButton.isEnabled = false
                setSearchStatusFail(result.error!!)
            }
        }
    }

    private fun setSearchStatusSuccess(msg: String) {
        statusIcon = VaadinIcon.CHECK.create().apply {
            color = "var(--lumo-success-text-color)"
            if (msg.isNotBlank()) {
                tooltip(msg)
            }
        }
        filter.prefixComponent = statusIcon
    }

    private fun setSearchStatusFail(msg: String) {
        statusIcon = VaadinIcon.BAN.create().apply {
            color = "var(--lumo-error-text-color)"
            tooltip(msg)
        }
        filter.prefixComponent = statusIcon
    }
}