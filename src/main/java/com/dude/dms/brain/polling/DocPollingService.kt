package com.dude.dms.brain.polling

import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.parsing.PdfToDocParser
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File

@Component
class DocPollingService(private val pdfToDocParser: PdfToDocParser, private val fileManager: FileManager) : PollingService {

    private val docPath = Options.get().doc.pollingPath
    private var tick = 1
    private val processing = HashSet<String>()

    override fun poll() {
        LOGGER.info("Polling {} for PDFs...", docPath)
        File(docPath).listFiles { _, name -> name.endsWith(".pdf") }?.forEach { processFile(it) }
    }

    @Scheduled(fixedRate = 1000)
    fun scheduledPoll() {
        if (tick < Options.get().doc.pollingInterval) {
            tick++
        } else {
            tick = 1
            poll()
        }
    }

    private fun processFile(file: File) {
        val name = file.name
        if (processing.add(name)) {
            fileManager.importFile(file)?.let {
                pdfToDocParser.parse(it)
                processing.remove(name)
            }
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DocPollingService::class.java)
    }
}