package com.dude.dms.ui.components.misc

import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.utils.tooltip
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select

class ViewPageSelector : HorizontalLayout() {

    private var prev: Button

    private var next: Button

    var pageSize: Select<Int>

    private var div: Div

    private var silent = false

    var items = 0
        set(value) {
            if (value != field) {
                field = value
                silent = true
                page = 0
                silent = false
            }
        }

    var page = 0
        set(value) {
            field = value
            update()
        }

    val max: Int
        get() = items / pageSize.value!!

    private var onChange: ((Int) -> Unit)? = null

    init {
        isPadding = false
        isSpacing = false
        alignItems = FlexComponent.Alignment.CENTER

        label("${t("page")}:")
        prev = iconButton(VaadinIcon.ARROW_CIRCLE_LEFT.create()) {
            tooltip(t("page.prev"))
            onLeftClick { prev() }
        }
        div = div { width = "max-content" }
        next = iconButton(VaadinIcon.ARROW_CIRCLE_RIGHT.create()) {
            tooltip(t("page.next"))
            onLeftClick { next() }
        }
        div { width = "2em" }
        label("${t("items.page")}:")
        pageSize = select {
            tooltip(t("page.docs"))
            setItems(10, 20, 30, 50, 100, 200)
            value = Options.get().view.itemsPerPage
            width = "100px"
            isEmptySelectionAllowed = false
            style["padding"] = "0px 5px"
            addValueChangeListener {
                Options.get().apply {
                    view.itemsPerPage = it.value
                    save()
                }
                if (it.isFromClient) {
                    page = 0
                }
            }
        }
    }

    fun setChangeListener(onChange: ((Int) -> Unit)?) {
        this.onChange = onChange
    }

    private fun prev() {
        if (page > 0) page--
    }

    private fun next() {
        if (page < max) page++
    }

    private fun update() {
        prev.isEnabled = page > 0
        next.isEnabled = page < max
        div.text = "${page + 1} / ${max + 1}"
        if (!silent) {
            onChange?.invoke(page)
        }
    }
}