package com.dude.dms.startup

import com.dude.dms.backend.service.*
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.polling.DocImportService
import com.dude.dms.brain.t
import org.springframework.boot.CommandLineRunner
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import java.io.File
import java.util.*

@Component
class StartUpRunner(
        private val docService: DocService,
        private val fileManager: FileManager,
        private val docImportService: DocImportService,
        private val directoryChecker: DirectoryChecker,
        private val optionsChecker: OptionsChecker
) : CommandLineRunner {

    override fun run(vararg args: String) {
        LOGGER.info(System.getProperty("user.dir"))
        optionsChecker.checkOptions()
        directoryChecker.checkDirectories()
        LOGGER.info("Cleaning up files...")
        fileManager.getAllPdfs().cleanup()
        fileManager.getAllImages().cleanup()

        val count = docService.count()
        docService.findAll().forEachIndexed { i, doc ->
            if (i % 10 == 0) LOGGER.info("Updating document $i / $count")
            docService.save(doc)
        }

        docImportService.import()
        LocaleContextHolder.setLocale(Locale.forLanguageTag(Options.get().view.locale))
        LOGGER.info(t("startup.complete"))
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(StartUpRunner::class.java)
    }

    private fun List<File>.cleanup() {
        forEach { file ->
            val guid = file.name.takeWhile { it != '.' }
            val docs = docService.findByGuidAsSet(guid)
            if (docs == null) {
                file.delete()
            } else if (docs.size > 1) {
                LOGGER.warn("Found duplicate documents with GUID $guid. Keeping the older document...")
                docs.sortedBy { it.insertTime }.stream().skip(1).forEach { docService.delete(it) }
            }
        }
    }
}
