package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.components.cards.DocCard
import com.dude.dms.utils.docCard
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent

class DocMultiSelectDialog(items: List<DocContainer>, private val onSelect: (Set<DocContainer>) -> Unit) : DmsDialog(t("doc.select"), 70) {

    private val itemContainer: Div

    private lateinit var selectButton: Button

    private val map = items.associateWith { true }.toMutableMap()

    init {
        setHeightFull()

        itemContainer = div {
            setWidthFull()
            style["display"] = "flex"
            style["flexWrap"] = "wrap"
            style["paddingBottom"] = "40px"

            items.forEach { dc ->
                docCard(dc, false) {
                    header.style["backgroundColor"] = if (map[dc]!!) "green" else ""
                    addClickListener {
                        map[dc] = !map[dc]!!
                        header.style["backgroundColor"] = if (map[dc]!!) "green" else ""
                        refresh()
                    }
                }
            }
        }
        horizontalLayout {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            alignItems = FlexComponent.Alignment.CENTER
            style["position"] = "sticky"
            style["bottom"] = "0"
            style["marginTop"] = "auto"
            style["borderTop"] = "1px solid var(--lumo-contrast-10pct)"
            style["backgroundColor"] = "var(--lumo-base-color)"

            label("${t("zoom")}:")
            iconButton(VaadinIcon.MINUS_CIRCLE.create()) { onLeftClick { shrink() } }
            iconButton(VaadinIcon.PLUS_CIRCLE.create()) { onLeftClick { grow() } }
            div { width = "2em" }
            selectButton = button("${t("select")} ${map.count { it.value }}") {
                onLeftClick {
                    onSelect.invoke(map.filter { it.value }.map { it.key }.toSet())
                    close()
                }
            }
        }
    }

    private fun grow() {
        val options = Options.get()
        if (options.view.docCardSize < 400) {
            options.view.docCardSize += 10
            options.save()
            itemContainer.children.filter { it is DocCard }.forEach { (it as DocCard).resize() }
        }
    }

    private fun shrink() {
        val options = Options.get()
        if (options.view.docCardSize > 100) {
            options.view.docCardSize -= 10
            options.save()
            itemContainer.children.filter { it is DocCard }.forEach { (it as DocCard).resize() }
        }
    }

    private fun refresh() {
        selectButton.text = "${t("select")} ${map.count { it.value }}"
    }
}
