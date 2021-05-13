package com.dude.dms.ui.components.misc

import com.dude.dms.brain.dsl.attributefilter.AttributeFilterParser
import com.dude.dms.brain.t
import com.dude.dms.utils.clearTooltips
import com.dude.dms.utils.hintList
import com.dude.dms.utils.tooltip
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode


class AttributeFilterText : VerticalLayout() {

    val filter: TextField
    private lateinit var hintList: HintList
    private var statusIcon = VaadinIcon.CHECK.create().apply { color = "var(--lumo-success-text-color)" }

    var onChange: ((AttributeFilterParser.ParseResult) -> Unit)? = null

    val isValid get() = _isValid

    private var _isValid = false

    private val parser = AttributeFilterParser()

    init {
        isPadding = false
        isMargin = false
        style["position"] = "relative"
        filter = textField(t("attribute.filter.info")) {
            setWidthFull()
            isClearButtonVisible = true
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
            addValueChangeListener { textChange(it.value) }
        }
        hintList = hintList {
            isVisible = false
            maxWidth = "30em"
            style["position"] = "absolute"
            style["top"] = "55px"
            style["backgroundColor"] = "var(--lumo-base-color)"
            style["zIndex"] = "1"
            style["border"] = "2px solid var(--lumo-contrast-20pct)"
            setHints(parser.getHints())
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

    private fun textChange(newValue: String) {
        val result = parser.setInput(newValue)
        val hintResult = parser.getHints()
        hintList.setHints(hintResult)
        statusIcon.clearTooltips()
        if (newValue.isBlank()) {
            _isValid = true
            setStatusSuccess()
        } else {
            if (result.isValid) {
                _isValid = true
                setStatusSuccess()
            } else {
                _isValid = false
                setStatusFail(result.error!!)
            }
        }
        onChange?.invoke(result)
    }

    private fun setStatusSuccess() {
        statusIcon = VaadinIcon.CHECK.create().apply { color = "var(--lumo-success-text-color)" }
        filter.prefixComponent = statusIcon
    }

    private fun setStatusFail(msg: String) {
        statusIcon = VaadinIcon.BAN.create().apply {
            color = "var(--lumo-error-text-color)"
            tooltip(msg)
        }
        filter.prefixComponent = statusIcon
    }
}