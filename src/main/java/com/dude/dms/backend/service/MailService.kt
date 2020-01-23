package com.dude.dms.backend.service

import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.repositories.MailRepository
import org.springframework.stereotype.Service

@Service
class MailService(private val mailRepository: MailRepository) : CrudService<Mail>(mailRepository)