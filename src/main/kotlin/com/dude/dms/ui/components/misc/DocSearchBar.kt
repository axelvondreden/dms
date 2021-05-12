package com.dude.dms.ui.components.misc

import com.dude.dms.brain.dsl.docsearch.DocSearchParser
import com.dude.dms.brain.t
import com.dude.dms.ui.components.dialogs.QuerySaveDialog
import com.dude.dms.utils.clearTooltips
import com.dude.dms.utils.hintList
import com.dude.dms.utils.tooltip
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode


class DocSearchBar(private val hideSaveIcon: Boolean = false) : VerticalLayout() {

    lateinit var textFilter: TextField
    private lateinit var saveIcon: Icon
    private lateinit var hintList: HintList
    private var searchStatusIcon = VaadinIcon.CHECK.create().apply { color = "var(--lumo-success-text-color)" }

    var onChange: ((String) -> Unit)? = null

    private val searchParser = DocSearchParser()

    init {
        isPadding = false
        isMargin = false
        setWidthFull()
        horizontalLayout(isPadding = false, isSpacing = false) {
            alignItems = FlexComponent.Alignment.CENTER
            setWidthFull()
            textFilter = textField {
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
                suffixComponent = searchStatusIcon
                addValueChangeListener { searchTextChange(it.value) }
            }
            saveIcon = icon(VaadinIcon.DISC) {
                alignSelf = FlexComponent.Alignment.CENTER
                style["paddingLeft"] = "8px"
                tooltip(t("save"))
                onLeftClick {
                    QuerySaveDialog(textFilter.value).open()
                }
                if (hideSaveIcon) isVisible = false
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
                val current = textFilter.value
                if (current.endsWith(" ")) {
                    textFilter.value = "$current${it.text} "
                } else {
                    var itCopy = it.text
                    while (!current.endsWith(itCopy, ignoreCase = true)) {
                        itCopy = itCopy.dropLast(1)
                    }
                    textFilter.value = current.dropLast(itCopy.length) + it.text + " "
                }
                if (it.caretBackwardsMovement > 0) {
                    val index = textFilter.value.length - 1 - it.caretBackwardsMovement
                    textFilter.element.executeJs("this.shadowRoot.children[1].children[1].children[1].children[0].setSelectionRange($index, $index)")
                }
            }
        }
    }

    private fun searchTextChange(newValue: String) {
        val result = searchParser.setInput(newValue)
        val hintResult = searchParser.getHints()
        hintList.setHints(hintResult)
        searchStatusIcon.clearTooltips()
        if (newValue.isBlank()) {
            onChange?.invoke("")
            saveIcon.isVisible = false
            setSearchStatusSuccess("")
        } else {
            if (result.isValid) {
                onChange?.invoke(result.docSearch!!.translate())
                saveIcon.isVisible = !hideSaveIcon
                setSearchStatusSuccess(result.docSearch!!.translate())
            } else {
                saveIcon.isVisible = false
                setSearchStatusFail(result.error!!)
            }
        }
    }

    private fun setSearchStatusSuccess(msg: String) {
        searchStatusIcon = VaadinIcon.CHECK.create().apply {
            color = "var(--lumo-success-text-color)"
            if (msg.isNotBlank()) {
                tooltip(msg)
            }
        }
    }

    private fun setSearchStatusFail(msg: String) {
        searchStatusIcon = VaadinIcon.BAN.create().apply {
            color = "var(--lumo-error-text-color)"
            tooltip(msg)
        }
    }
}