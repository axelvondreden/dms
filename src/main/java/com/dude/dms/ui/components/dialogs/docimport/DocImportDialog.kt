package com.dude.dms.ui.components.dialogs.docimport

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.WordService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.polling.DocPollingService
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.mlottmann.vstepper.VStepper
import com.vaadin.flow.component.dialog.Dialog
import java.io.File
import java.time.LocalDate


class DocImportDialog(
        builderFactory: BuilderFactory,
        private val docService: DocService,
        private val lineService: LineService,
        private val wordService: WordService,
        docPollingService: DocPollingService,
        fileManager: FileManager,
        docParser: DocParser
) : Dialog() {

    data class FileContainer(
            val file: File,
            var guid: String? = null,
            var selected: Boolean = false,
            var language: String = Options.get().doc.ocrLanguage,
            var useOcrTxt: Boolean = false,
            var ocrText: Set<Line>? = null,
            var pdfText: Set<Line>? = null,
            var date: LocalDate? = null,
            var tags: MutableSet<Tag> = mutableSetOf()
    )

    data class StepData(
            var files: MutableSet<FileContainer> = mutableSetOf()
    )

    init {
        width = "60vw"
        height = "80vh"

        val stepData = StepData(docPollingService.poll().map { FileContainer(it) }.toMutableSet())
        add(VStepper().apply {
            val checkTextStep = CheckTextStep(builderFactory, stepData, fileManager, docParser)
            val checkTagStep = CheckTagStep(builderFactory, stepData, docParser)
            addStep(t("import"), FileImportStep(stepData))
            addStep(t("text.check"), checkTextStep)
            addStep(t("tags.check"), checkTagStep)
            addFinishListener {
                createDocs(stepData)

                close()
            }
            addCancelListener { close() }
            addDialogCloseActionListener {
                stepData.files.filter { it.guid != null }.forEach { fileManager.delete(it.guid!!) }
                checkTextStep.processing = false
            }
        })
    }

    private fun createDocs(stepData: StepData) {
        stepData.files.filter { it.selected }.forEach {
            val lines = (if (it.useOcrTxt) it.ocrText else it.pdfText)!!
            val doc = Doc(it.guid!!, it.date)
            if (lines.isNotEmpty()) {
                doc.rawText = docService.getFullTextMemory(lines)
            }
            doc.tags = it.tags
            docService.create(doc)
            lines.forEach { line ->
                line.doc = doc
                lineService.create(line)
                line.words.forEach { word ->
                    wordService.create(word)
                }
            }
            it.file.delete()
        }
    }
}