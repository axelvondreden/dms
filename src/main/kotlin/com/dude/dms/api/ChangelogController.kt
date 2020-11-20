package com.dude.dms.api

import com.dude.dms.backend.data.Changelog
import com.dude.dms.backend.service.ChangelogService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/changelogs")
class ChangelogController(private val changelogService: ChangelogService) {

    @GetMapping("/")
    fun getAllChangelogs(): List<Changelog> = changelogService.findAll()
}