package com.dude.dms.ui.components.cards

import com.dude.dms.backend.data.rules.Condition
import com.dude.dms.backend.data.rules.ConditionType
import com.dude.dms.brain.t
import com.github.appreciated.card.Card
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField

class ConditionCard(private val condition: Condition, private val parent: ConditionCard? = null) : Card() {

    private val label = ComboBox<ConditionType>().apply {
        setItems(*ConditionType.values())
        setWidthFull()
        isAllowCustomValue = false
        isPreventInvalidInput = true
        value = condition.type
        setItemLabelGenerator { t(it.translationKey) }
        addValueChangeListener {
            condition.type = it.value
            fill()
        }
    }

    private val header = HorizontalLayout().apply { setWidthFull() }

    private val body = HorizontalLayout().apply { setWidthFull() }

    init {
        setWidthFull()
        fill()
    }

    fun fill() {
        content.removeAll()
        header.removeAll()
        body.removeAll()

        header.add(label)

        when (condition.type) {
            ConditionType.AND, ConditionType.OR -> {
                if (condition.children.isNullOrEmpty()) {
                    condition.children = mutableListOf(
                            Condition(parent = condition, type = ConditionType.EQUALS),
                            Condition(parent = condition, type = ConditionType.EQUALS)
                    )
                }
                header.add(Button(VaadinIcon.PLUS.create()) { plus() })
                body.add(*condition.children.map { ConditionCard(it, this) }.toTypedArray())
            }
            else -> body.add(TextField("", condition.text ?: "", "Text").apply {
                addValueChangeListener { condition.text = it.value ?: "" }
                setWidthFull()
            })
        }

        if (condition.parent?.type in arrayOf(ConditionType.AND, ConditionType.OR)) {
            header.add(Button(VaadinIcon.TRASH.create()) { delete() })
        }

        add(header, body)
    }

    private fun plus() {
        if (condition.type in arrayOf(ConditionType.AND, ConditionType.OR)) {
            Condition(parent = condition, type = ConditionType.EQUALS).apply {
                condition.children.add(this)
                body.add(ConditionCard(this, this@ConditionCard))
            }
        }
    }

    private fun delete() {
        if (condition.type !in arrayOf(ConditionType.AND, ConditionType.OR)) {
            if (condition.parent?.children?.size ?: 0 > 2) {
                condition.parent?.children?.remove(condition)
                parent?.fill()
            }
        }
    }
}