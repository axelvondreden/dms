package com.dude.dms.api

import com.dude.dms.backend.service.DocService
import com.dude.dms.utils.fileManager
import com.dude.dms.utils.tagService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneOffset

@RestController
@RequestMapping("/api")
class Controller(private val docService: DocService) {

    @GetMapping("/system/test")
    fun test() = true

    @GetMapping("/sync/state")
    fun getState(): ResponseEntity<DmsState> {
        val tags = tagService.findAll().map { it.name }
        val docs = docService.findAll().associate { doc ->
            doc.guid to DocResponse(
                doc.guid,
                doc.pages.size,
                doc.docText?.text,
                doc.documentDate?.atStartOfDay()?.toEpochSecond(ZoneOffset.UTC),
                doc.insertTime?.toEpochSecond(ZoneOffset.UTC),
                doc.tags.map { it.name },
                doc.attributeValues.associate { it.attribute.name to it.convertedValue }
            ).hashCode()
        }
        return ResponseEntity.ok().body(DmsState(tags, docs))
    }

    @GetMapping("/doc/{guid}")
    fun getDoc(@PathVariable("guid") guid: String): ResponseEntity<DocResponseWithHash> {
        val doc = docService.findByGuid(guid)
        return if (doc != null) ResponseEntity.ok().body(
            DocResponse(
                doc.guid,
                doc.pages.size,
                doc.docText?.text,
                doc.documentDate?.atStartOfDay()?.toEpochSecond(ZoneOffset.UTC),
                doc.insertTime?.toEpochSecond(ZoneOffset.UTC),
                doc.tags.map { it.name },
                doc.attributeValues.associate { it.attribute.name to it.convertedValue }
            ).withHash()
        ) else ResponseEntity.notFound().build()
    }

    @GetMapping("/doc/{guid}/thumb")
    fun getDocThumb(@PathVariable("guid") guid: String): ResponseEntity<InputStreamResource> {
        val file = fileManager.getThumb(guid)
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(InputStreamResource(file.inputStream()))
    }

    @GetMapping("/doc/{guid}/img/{page}")
    fun getDocImg(@PathVariable("guid") guid: String, @PathVariable("page") page: Int): ResponseEntity<InputStreamResource> {
        val file = fileManager.getImage(guid, page)
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(InputStreamResource(file.inputStream()))
    }

    data class DocResponse(val guid: String, val pages: Int, val text: String?, val documentDate: Long?, val insertTime: Long?, val tags: List<String>, val attributes: Map<String, String>) {
        fun withHash() = DocResponseWithHash(guid, hashCode(), pages, text, documentDate, insertTime, tags, attributes)
    }

    data class DocResponseWithHash(val guid: String, val hash: Int, val pages: Int, val text: String?, val documentDate: Long?, val insertTime: Long?, val tags: List<String>, val attributes: Map<String, String>)

    data class DmsState(val tags: List<String>, val docs: Map<String, Int>)
}
