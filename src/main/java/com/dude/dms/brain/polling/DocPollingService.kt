package com.dude.dms.brain.polling

import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.ui.Const
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File

@Component
class DocPollingService(private val docParser: DocParser, private val fileManager: FileManager) : PollingService {

    private val docPath = Options.get().doc.pollingPath
    private var tick = 1
    private val processing = HashSet<String>()

    override fun poll() {
        LOGGER.info("Polling {}...", docPath)
        File(docPath).listFiles { _, name -> name.endsWith(".pdf") }?.forEach { processPdf(it) }
        File(docPath).listFiles { _, name -> Const.IMAGE_FORMATS.any { name.endsWith(it) } }?.forEach { processImage(it) }
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

    private fun processPdf(pdf: File) {
        val name = pdf.name
        if (processing.add(name)) {
            fileManager.importPdf(pdf)?.let {
                docParser.parse(it)
                processing.remove(name)
            }
        }
    }

    private fun processImage(img: File) {
        val name = img.name
        if (processing.add(name)) {
            fileManager.importImage(img)?.let {
                docParser.parse(it)
                processing.remove(name)
            }
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DocPollingService::class.java)
    }
}