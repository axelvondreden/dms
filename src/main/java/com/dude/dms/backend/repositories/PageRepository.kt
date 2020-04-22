package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.docs.Page
import org.springframework.data.jpa.repository.JpaRepository

interface PageRepository : JpaRepository<Page, Long>