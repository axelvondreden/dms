package com.dude.dms.api

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.service.TagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tags")
class TagController(private val tagService: TagService) {

    @GetMapping("/")
    fun getAllTags(): List<Tag> = tagService.findAll()

    @GetMapping("/{name}")
    fun getTagByName(@PathVariable("name") name: String): ResponseEntity<Tag> {
        val tag = tagService.findByName(name)
        return if (tag != null) ResponseEntity.ok().body(tag) else ResponseEntity.notFound().build()
    }
}