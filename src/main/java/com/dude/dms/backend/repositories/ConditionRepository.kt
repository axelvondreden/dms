package com.dude.dms.backend.repositories

import com.dude.dms.backend.data.rules.Condition
import org.springframework.data.jpa.repository.JpaRepository

interface ConditionRepository : JpaRepository<Condition, Long>