package com.dude.dms.api

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.utils.fileManager
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.time.LocalDate

@RestController
@RequestMapping("/api/docs")
class DocController(private val docService: DocService) {

    @GetMapping("/")
    fun getAllDocs() = docService.findAll().map { DocContainer(it) }.map { DocOverviewResponse(it.guid, it.date) }

    @GetMapping("/{guid}")
    fun getDoc(@PathVariable("guid") guid: String): ResponseEntity<Doc> {
        val doc = docService.findByGuid(guid)
        return if (doc != null) ResponseEntity.ok().body(doc) else ResponseEntity.notFound().build()
    }

    @GetMapping("/{guid}/thumb")
    fun getDocThumb(@PathVariable("guid") guid: String): ResponseEntity<InputStreamResource> {
        val file = fileManager.getThumb(guid)
        return if (file != null) ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(InputStreamResource(file.inputStream())) else ResponseEntity.notFound().build()
    }

    data class DocOverviewResponse(val guid: String, val date: LocalDate?)
}
