package com.dude.dms.api

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/docs")
class DocController(private val docService: DocService) {

    @GetMapping("/")
    fun getAllDocs() = docService.findAll()

    @GetMapping("/{guid}")
    fun getDocByGuid(@PathVariable("guid") guid: String): ResponseEntity<Doc> {
        val doc = docService.findByGuid(guid)
        return if (doc.isPresent) ResponseEntity.ok().body(doc.get()) else ResponseEntity.notFound().build()
    }
}