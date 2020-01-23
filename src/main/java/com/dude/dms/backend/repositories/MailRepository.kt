package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.mails.Mail
import org.springframework.data.jpa.repository.JpaRepository

interface MailRepository : JpaRepository<Mail, Long>