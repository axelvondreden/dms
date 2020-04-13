package com.dude.dms.ui.components.dialogs.docimport

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.docs.Line
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.LineService
import com.dude.dms.backend.service.WordService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.polling.DocImportService
import com.dude.dms.brain.t
import com.dude.dms.ui.builder.BuilderFactory
import com.mlottmann.vstepper.VStepper
import com.vaadin.flow.component.dialog.Dialog
import java.time.LocalDateTime


class DocImportDialog(
        builderFactory: BuilderFactory,
        private val docService: DocService,
        private val lineService: LineService,
        private val wordService: WordService,
        docImportService: DocImportService,
        fileManager: FileManager,
        docParser: DocParser
) : Dialog() {

    init {
        add(VStepper().apply {
            addFinishListener {
                createDocs(stepData)

                close()
            }
            addCancelListener { close() }
            addDialogCloseActionListener {
                stepData.docs.filter { it.guid != null }.forEach { fileManager.delete(it.guid!!) }
                checkTextStep.processing = false
            }
        })
    }

    private fun createDocs(stepData: StepData) {
        stepData.docs.filter { it.selected }.forEach { fc ->
            val doc = Doc(fc.guid!!, fc.date, LocalDateTime.now())
            val lines = (if (fc.useOcrTxt) fc.ocrText else fc.pdfText).map { lc -> Line(doc, lc.words.map { it.word }.toSet(), lc.y) }.toSet()
            if (lines.isNotEmpty()) {
                doc.rawText = docService.getFullTextMemory(lines)
            }
            doc.tags = fc.tags
            docService.create(doc)
            lines.forEach { line ->
                line.doc = doc
                lineService.create(line)
                line.words.forEach { word ->
                    wordService.create(word)
                }
            }
            fc.file.delete()
        }
    }
}