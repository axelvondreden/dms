package com.dude.dms.api

import com.dude.dms.backend.service.TextBlockService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/textblocks")
class TextBlockController(private val textBlockService: TextBlockService) {

    @GetMapping("/")
    fun getAllTextBlocks() = textBlockService.findAll()
}