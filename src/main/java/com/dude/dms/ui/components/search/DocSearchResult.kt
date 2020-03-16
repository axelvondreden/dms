package com.dude.dms.ui.components.search

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.t
import com.dude.dms.ui.components.dialogs.DocImageDialog
import com.dude.dms.ui.components.tags.TagContainer
import com.dude.dms.ui.extensions.convert
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import kotlin.math.max
import kotlin.math.min

class DocSearchResult(
        private val doc: Doc,
        private val search: String,
        private val docImageDialog: DocImageDialog,
        private val tagService: TagService
) : SearchResult() {

    override val header = "${t("doc")} ${doc.documentDate?.convert()}"

    override val body: Component
        get() {
            val pdfButton = Button(VaadinIcon.FILE_TEXT.create(), ComponentEventListener { docImageDialog.open() }).apply {
                setWidthFull()
            }
            return VerticalLayout(TagContainer(tagService.findByDoc(doc)), textSnippet, pdfButton).apply {
                setSizeFull()
            }
        }

    private val textSnippet: Html
        get() {
            val raw = doc.rawText ?: return Html("")
            var index = raw.indexOf(search)
            if (index < 0) {
                index = raw.toLowerCase().indexOf(search.toLowerCase())
            }
            val length = search.length
            val start = max(0, index - 40)
            val end = min(raw.length, index + length + 40)
            val before = raw.substring(start, index)
            val after = raw.substring(index + length, end)
            return Html("<div style=\"width: 100%;\">...$before<mark>$search</mark>$after...</div>")
        }

    override fun onClick() {}
}