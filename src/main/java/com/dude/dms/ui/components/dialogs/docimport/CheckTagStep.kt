package com.dude.dms.ui.components.dialogs.docimport

import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.mlottmann.vstepper.StepContent
import com.mlottmann.vstepper.stepEvent.AbortEvent
import com.mlottmann.vstepper.stepEvent.CompleteEvent
import com.mlottmann.vstepper.stepEvent.EnterEvent
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.progressbar.ProgressBar

class CheckTagStep(
        private val builderFactory: BuilderFactory,
        private val stepData: DocImportDialog.StepData,
        private val docParser: DocParser
) : StepContent() {

    private val progressBar = ProgressBar().apply { setWidthFull() }

    private val progressText = Text("")

    private val cardWrapper = VerticalLayout().apply {
        setWidthFull()
        height = "55vh"
        maxHeight = "55vh"
        element.style["overflowY"] = "auto"
    }

    private val wrapper = Div(cardWrapper, progressText, progressBar).apply { setSizeFull() }

    var processing = false

    init {
        add(wrapper)
        setSizeFull()
    }

    override fun onAbort(event: AbortEvent?) {
        processing = false
    }

    override fun onComplete(event: CompleteEvent?) { }

    override fun isValid() = !processing && cardWrapper.children.count() > 0

    override fun onEnter(event: EnterEvent?) {
        cardWrapper.removeAll()
        val ui = UI.getCurrent()
        processing = true
        stepChanged()
        Thread { processFiles(ui) }.start()
    }

    private fun processFiles(ui: UI) {
        val files = stepData.docs.filter { it.selected }
        ui.access {
            progressBar.max = files.size.toDouble()
            progressBar.value = 0.0
        }

        files.withIndex().forEach { (index, it) ->
            if (!processing) return

            ui.access { progressText.text = t("doc.parse.date.detect") }
            it.date = docParser.getMostFrequentDate((if (it.useOcrTxt) it.ocrText else it.pdfText)!!)

            ui.access { progressText.text = t("tags.add") }
            it.tags = docParser.discoverTags((if (it.useOcrTxt) it.ocrText else it.pdfText)!!).toMutableSet()

            if (!processing) return
            ui.access {
                progressBar.value = (index + 1).toDouble()
                cardWrapper.add(builderFactory.docs().tagImportCard(it))
            }
        }
        processing = false
        ui.access {
            stepChanged()
            progressText.text = t("done")
            progressBar.value = progressBar.max
        }
    }
}