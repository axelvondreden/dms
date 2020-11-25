package com.dude.dms.ui.components.misc

import com.dude.dms.brain.search.SearchParser
import com.dude.dms.brain.t
import com.dude.dms.utils.clearTooltips
import com.dude.dms.utils.searchHintList
import com.dude.dms.utils.showTooltip
import com.dude.dms.utils.tooltip
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.util.*
import kotlin.concurrent.schedule


class SearchBar : VerticalLayout() {

    var textFilter: TextField
    private lateinit var hintList: SearchHintList

    var onChange: ((String) -> Unit)? = null

    private val searchParser = SearchParser()

    private val viewUI = UI.getCurrent()

    init {
        isPadding = false
        isMargin = false
        setWidthFull()
        textFilter = textField {
            setWidthFull()
            isClearButtonVisible = true
            placeholder = t("search")
            valueChangeMode = ValueChangeMode.EAGER
            element.setAttribute("onkeydown", """
                        if (event.key == 'ArrowDown' || event.key == 'ArrowUp') {
                            return false;
                        }
                        return true;
                        """.trimIndent())
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
            suffixComponent = VaadinIcon.CHECK.create().apply { color = "var(--lumo-success-text-color)" }
            addValueChangeListener { searchTextChange(it.value) }
        }
        hintList = searchHintList {
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
        (textFilter.suffixComponent as Icon).clearTooltips()
        if (newValue.isBlank()) {
            onChange?.invoke("")
            setSearchStatusSuccess("")
        } else {
            if (result.isValid) {
                onChange?.invoke(result.search!!.translate())
                setSearchStatusSuccess(result.search!!.translate())
            } else {
                setSearchStatusFail(result.error!!)
            }
        }
    }

    private fun setSearchStatusSuccess(msg: String) {
        textFilter.suffixComponent = VaadinIcon.CHECK.create().apply { color = "var(--lumo-success-text-color)" }
        if (msg.isNotBlank()) {
            (textFilter.suffixComponent as Icon).tooltip(msg)
        }
    }

    private fun setSearchStatusFail(msg: String) {
        textFilter.suffixComponent = VaadinIcon.BAN.create().apply { color = "var(--lumo-error-text-color)" }
        (textFilter.suffixComponent as Icon).tooltip(msg)
        Timer().schedule(200) { viewUI.access { (textFilter.suffixComponent as Icon).showTooltip() } }
    }
}