package com.dude.dms.api

import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.service.PlainTextRuleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rules")
class RuleController(private val plainTextRuleService: PlainTextRuleService) {

    @GetMapping("/plain/")
    fun getAllRules() = plainTextRuleService.findAll()

    @GetMapping("/plain/{id}")
    fun getPlainTextRuleById(@PathVariable("id") id: Long): ResponseEntity<PlainTextRule> {
        val plainTextRule = plainTextRuleService.findById(id)
        return if (plainTextRule.isPresent) ResponseEntity.ok().body(plainTextRule.get()) else ResponseEntity.notFound().build()
    }
}