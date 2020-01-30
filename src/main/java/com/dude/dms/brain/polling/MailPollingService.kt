package com.dude.dms.brain.polling

import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.service.MailFilterService
import com.dude.dms.backend.service.MailService
import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.MailReceiveEvent
import com.dude.dms.brain.mail.MailManager
import com.dude.dms.brain.options.Options
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.HashMap

@Component
class MailPollingService(
        private val mailManager: MailManager,
        private val mailFilterService: MailFilterService,
        private val mailService: MailService
) : PollingService {

    private var tick = 1

    private val eventListeners = HashMap<String, MailReceiveEvent>()

    fun addEventListener(key: String, mailReceiveEvent: MailReceiveEvent) {
        eventListeners[key] = mailReceiveEvent
    }

    override fun poll() {
        for (filter in mailFilterService.findAll()) {
            LOGGER.info("Polling {}...", filter.folder)
            val allMails = mailService.findAll()
            val newMails = mailManager.getMails(filter.folder).filter { it !in allMails }
            if (newMails.isNotEmpty()) {
                LOGGER.info("Processing {} new mails...", newMails.size)
            }
            processMails(newMails)
        }
    }

    @Scheduled(fixedRate = 1000)
    fun scheduledPoll() {
        if (tick < Options.get().mail.pollingInterval * 60) {
            tick++
        } else {
            tick = 1
            poll()
        }
    }

    private fun processMails(mails: List<Mail>) {
        for (mail in mails) {
            LOGGER.info("Saving Mail: {}", mail)
            mailService.save(mail)
        }
        eventListeners.values.forEach { it.invoke(mails.size) }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(MailPollingService::class.java)
    }
}