package com.dude.dms.brain.polling

import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.backend.service.MailService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.brain.options.Options
import com.dude.dms.brain.t
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MailPollingService(
        private val mailManager: MailManager,
        private val mailFilterService: MailFilterService,
        private val mailService: MailService
) {

    private var tick = 1

    private val options = Options.get()

    fun poll() {
        for (filter in mailFilterService.findAll()) {
            LOGGER.info(t("mail.poll", filter.folder))
            val allMails = mailService.findAll()
            val newMails = mailManager.getMails(filter.folder).filter { it !in allMails }
            if (newMails.isNotEmpty()) {
                LOGGER.info(t("mail.process", newMails.size))
            }
            newMails.forEach { mailService.save(it) }
        }
    }

    @Scheduled(fixedRate = 1000)
    fun scheduledPoll() {
        if (tick < options.mail.pollingInterval * 60) {
            tick++
        } else {
            tick = 1
            poll()
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(MailPollingService::class.java)
    }
}