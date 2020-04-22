package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.docs.Word
import org.springframework.data.jpa.repository.JpaRepository

interface WordRepository : JpaRepository<Word, Long>