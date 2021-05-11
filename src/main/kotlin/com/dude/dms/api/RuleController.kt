package com.dude.dms.api

import com.dude.dms.backend.data.filter.PlainTextRule
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
    fun getAllRules(): List<PlainTextRule> = plainTextRuleService.findAll()

    @GetMapping("/plain/{id}")
    fun getPlainTextRuleById(@PathVariable("id") id: Long): ResponseEntity<PlainTextRule> {
        val plainTextRule = plainTextRuleService.load(id)
        return if (plainTextRule != null) ResponseEntity.ok().body(plainTextRule) else ResponseEntity.notFound().build()
    }
}