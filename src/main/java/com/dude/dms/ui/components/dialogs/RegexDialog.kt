package com.dude.dms.ui.components.dialogs

import com.dude.dms.brain.t
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.splitlayout.SplitLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class RegexDialog(
        private val callBack: TextField,
        private val matches: ArrayList<String> = ArrayList()
) : DmsDialog("Regex", 60, 60) {

    private lateinit var regexField: TextField

    private lateinit var textArea: TextArea

    private lateinit var grid: Grid<String>

    init {
        horizontalLayout {
            alignItems = FlexComponent.Alignment.END

            regexField = textField("Regex") {
                value = callBack.value
                setWidthFull()
                addValueChangeListener { refreshResults() }
                valueChangeMode = ValueChangeMode.EAGER
            }
            iconButton(VaadinIcon.CHECK.create()) {
                onLeftClick { save() }
            }
        }
        splitLayout {
            setWidthFull()
            height = "80%"
            orientation = SplitLayout.Orientation.VERTICAL

            textArea = TextArea(t("testarea")).apply {
                setWidthFull()
                height = "50%"
                addValueChangeListener { refreshResults() }
                valueChangeMode = ValueChangeMode.EAGER
            }
            grid = Grid<String>().apply {
                addColumn { it }.setHeader(t("matches"))
                setItems(matches)
                height = "30%"
            }
            addToPrimary(textArea)
            addToSecondary(grid)
        }
    }

    private fun refreshResults() {
        matches.clear()
        if (!regexField.isEmpty && !textArea.isEmpty) {
            regexField.errorMessage = ""
            try {
                val pattern = Pattern.compile(regexField.value)
                textArea.value.split("\n").toTypedArray()
                        .map { pattern.matcher(it) }
                        .forEach {
                            while (it.find()) {
                                val result = it.group()
                                if (result.isNotEmpty()) {
                                    matches.add(result)
                                }
                            }
                        }
            } catch (e: PatternSyntaxException) {
                regexField.errorMessage = t("regex.invalid")
            }
        }
        grid.dataProvider.refreshAll()
    }

    private fun save() {
        callBack.value = regexField.value
        close()
    }
}