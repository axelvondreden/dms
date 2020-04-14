package com.dude.dms.backend.service

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.backend.repositories.TagRepository
import com.dude.dms.brain.events.EventManager
import org.springframework.stereotype.Service

@Service
class TagService(
        private val tagRepository: TagRepository,
        private val plainTextRuleService: PlainTextRuleService,
        private val regexRuleService: RegexRuleService,
        private val mailFilterService: MailFilterService,
        eventManager: EventManager
) : EventService<Tag>(tagRepository, eventManager) {

    override fun create(entity: Tag) = tagRepository.findByName(entity.name) ?: super.create(entity)

    override fun delete(entity: Tag) {
        plainTextRuleService.findByTag(entity).forEach {
            it.tags = findByPlainTextRule(it).minus(entity)
            plainTextRuleService.save(it)
        }
        regexRuleService.findByTag(entity).forEach {
            it.tags = findByRegexRule(it).minus(entity)
            regexRuleService.save(it)
        }
        mailFilterService.findByTag(entity).forEach {
            it.tags = findByMailFilter(it).minus(entity)
            mailFilterService.save(it)
        }
        super.delete(entity)
    }

    fun findByName(name: String) = tagRepository.findByName(name)

    fun findByPlainTextRule(rule: PlainTextRule) = tagRepository.findByPlainTextRules(rule)

    fun findByRegexRule(rule: RegexRule) = tagRepository.findByRegexRules(rule)

    private fun findByMailFilter(mailFilter: MailFilter) = tagRepository.findByMailFilters(mailFilter)

    fun findByMail(mail: Mail) = tagRepository.findByMails(mail)

    fun findByAttribute(attribute: Attribute) = tagRepository.findByAttributes(attribute)

    fun countByAttribute(attribute: Attribute) = tagRepository.countByAttributes(attribute)
}