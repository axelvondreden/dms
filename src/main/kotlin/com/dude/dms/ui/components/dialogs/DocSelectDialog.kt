package com.dude.dms.ui.components.dialogs

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import com.dude.dms.ui.components.cards.DocCard
import com.dude.dms.ui.components.misc.DocSearchBar
import com.dude.dms.ui.components.misc.ViewPageSelector
import com.dude.dms.utils.docCard
import com.dude.dms.utils.docService
import com.dude.dms.utils.searchBar
import com.dude.dms.utils.viewPageSelector
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import org.springframework.data.domain.PageRequest

class DocSelectDialog(private val onSelect: (DocContainer) -> Unit) : DmsDialog(t("doc.select")) {

    private val itemContainer: Div

    private val searchBar: DocSearchBar

    private lateinit var pageSelector: ViewPageSelector

    private lateinit var itemCount: Text

    private var filter = ""

    init {
        setHeightFull()

        searchBar = searchBar(true) {
            style["position"] = "sticky"
            style["top"] = "0"
            style["zIndex"] = "1"
            style["borderBottom"] = "1px solid var(--lumo-contrast-10pct)"
            style["backgroundColor"] = "var(--lumo-base-color)"
            onChange = {
                this@DocSelectDialog.filter = it
                fill()
            }
        }
        itemContainer = div {
            setWidthFull()
            style["display"] = "flex"
            style["flexWrap"] = "wrap"
            style["paddingBottom"] = "40px"
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
            iconButton(VaadinIcon.MINUS_CIRCLE.create()) {
                onLeftClick { shrink() }
            }
            iconButton(VaadinIcon.PLUS_CIRCLE.create()) {
                onLeftClick { grow() }
            }
            div { width = "2em" }
            pageSelector = viewPageSelector()
            div { width = "2em" }
            itemCount = text(t("items") + ":")
        }

        pageSelector.setChangeListener { fill() }
        fill()
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

    private fun fill() {
        val docs = docService.findByFilter(filter, PageRequest.of(pageSelector.page, pageSelector.pageSize.value))
        val count = docService.countByFilter(filter)
        itemContainer.removeAll()
        pageSelector.items = count.toInt()
        itemCount.text = "${t("items")}: ${docs.size}"
        docs.forEach {
            itemContainer.docCard(DocContainer(it)) {
                addClickListener {
                    onSelect.invoke(docContainer)
                    close()
                }
            }
        }
    }
}