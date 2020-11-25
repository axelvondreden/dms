package com.dude.dms.startup

import com.dude.dms.backend.service.*
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.polling.DocImportService
import com.dude.dms.brain.t
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.boot.CommandLineRunner
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class StartUpRunner(
        private val docService: DocService,
        private val attributeValueService: AttributeValueService,
        private val pageService: PageService,
        private val lineService: LineService,
        private val wordService: WordService,
        private val fileManager: FileManager,
        private val docImportService: DocImportService,
        private val directoryChecker: DirectoryChecker,
        private val optionsChecker: OptionsChecker
) : CommandLineRunner {

    override fun run(vararg args: String) {
        optionsChecker.checkOptions()
        directoryChecker.checkDirectories()
        LOGGER.info("Cleaning up files...")
        fileManager.getAllPdfs().forEach { file -> if (docService.findByGuid(file.name.takeWhile { it != '.' }) == null) file.delete() }
        fileManager.getAllImages().forEach { file -> if (docService.findByGuid(file.name.takeWhile { it != '_' }) == null) file.delete() }

        // Migration 0.0.2 -> 0.0.3
        docService.findIncomplete().forEach {
            if (it.deleted == null) {
                it.deleted = false
                docService.save(it)
            }
        }
        pageService.findIncomplete().forEach {
            if (it.deleted == null) {
                it.deleted = false
                pageService.save(it)
            }
        }
        lineService.findIncomplete().forEach {
            if (it.deleted == null) {
                it.deleted = false
                lineService.save(it)
            }
        }
        wordService.findIncomplete().forEach {
            if (it.deleted == null) {
                it.deleted = false
                wordService.save(it)
            }
        }
        attributeValueService.findIncomplete().forEach {
            if (it.deleted == null) {
                it.deleted = false
                attributeValueService.save(it)
            }
        }

        docImportService.import()
        LocaleContextHolder.setLocale(Locale.forLanguageTag(Options.get().view.locale))
        LOGGER.info(t("startup.complete"))
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(StartUpRunner::class.java)
    }
}