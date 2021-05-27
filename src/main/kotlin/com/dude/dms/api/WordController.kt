package com.dude.dms.api

import com.dude.dms.backend.service.WordService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/words")
class WordController(private val wordService: WordService) {

    @GetMapping("/")
    fun getAllWords() = wordService.findAll()
}
