package com.dude.dms.startup

import com.dude.dms.brain.options.Options
import com.dude.dms.brain.polling.DocPollingService
import com.dude.dms.updater.UpdateChecker
import org.springframework.boot.CommandLineRunner
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class StartUpRunner(
        private val docPollingService: DocPollingService,
        private val updateChecker: UpdateChecker,
        private val directoryChecker: DirectoryChecker,
        private val optionsChecker: OptionsChecker
) : CommandLineRunner {

    override fun run(vararg args: String) {
        updateChecker.check(true)
        optionsChecker.checkOptions()
        directoryChecker.checkDirectories()
        docPollingService.poll()
        LocaleContextHolder.setLocale(Locale.forLanguageTag(Options.get().view.locale))
    }
}