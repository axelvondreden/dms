package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.mails.MailFilter
import org.springframework.data.jpa.repository.JpaRepository

interface MailFilterRepository : JpaRepository<MailFilter, Long>