package com.dude.dms.backend.service

import com.dude.dms.backend.data.rules.Rule

interface RuleService<T : Rule> {
    val activeRules: List<T>
}