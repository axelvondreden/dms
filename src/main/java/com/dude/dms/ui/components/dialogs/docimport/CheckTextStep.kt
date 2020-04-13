package com.dude.dms.ui.components.dialogs.docimport

import com.dude.dms.brain.FileManager
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

class CheckTextStep(
        private val builderFactory: BuilderFactory,
        private val stepData: DocImportDialog.StepData,
        private val fileManager: FileManager,
        private val docParser: DocParser
) : StepContent() {

    private fun processFiles(ui: UI) {
        val files = stepData.docs.filter { it.selected }
        files.withIndex().forEach { (index, it) ->

        }
        processing = false
        ui.access {
            stepChanged()
            progressText.text = t("done")
            progressBar.value = progressBar.max
        }
    }
}