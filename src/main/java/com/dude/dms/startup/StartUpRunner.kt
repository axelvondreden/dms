package com.dude.dms.startup

import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.OptionKey
import com.dude.dms.brain.polling.DocPollingService
import com.dude.dms.updater.UpdateChecker
import org.springframework.boot.CommandLineRunner
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import java.io.File
import java.util.*

@Component
class StartUpRunner(
        private val docPollingService: DocPollingService,
        private val updateChecker: UpdateChecker,
        private val directoryManager: DirectoryManager,
        private val demoDataManager: DemoDataManager
) : CommandLineRunner {

    override fun run(vararg args: String) {
        updateChecker.check(true)
        createOptionsFile()
        directoryManager.checkDirectories()
        demoDataManager.createDemoData()
        docPollingService.poll()
        LocaleContextHolder.setLocale(Locale.forLanguageTag(OptionKey.LOCALE.string))
    }

    private fun createOptionsFile() {
        val prop = File("options.properties")
        if (!prop.exists()) {
            LOGGER.info("Creating user properties...")
            prop.createNewFile()
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(StartUpRunner::class.java)
    }
}