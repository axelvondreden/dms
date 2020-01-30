package com.dude.dms.backend.service

import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.repositories.MailFilterRepository
import org.springframework.stereotype.Service

@Service
class MailFilterService(private val mailFilterRepository: MailFilterRepository) : CrudService<MailFilter>(mailFilterRepository)