package com.dude.dms.startup

import com.dude.dms.backend.brain.DmsLogger
import com.dude.dms.backend.brain.OptionKey
import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.security.SecureRandom
import java.time.LocalDate
import java.util.*
import kotlin.math.min

@Component
class DemoDataManager(private val tagService: TagService, private val docService: DocService, private val attributeService: AttributeService) {

    private var random: Random? = null

    fun createDemoData() {
        random = SecureRandom()
        createReviewTag()
        val tags = createDemoTags()
        if (docService.count() == 0L && OptionKey.DEMO_DOCS.int > 0) {
            LOGGER.info("Creating demo docs...")
            createDemoDocs(tags)
        }
    }

    private fun createDemoDocs(tags: Iterable<Tag>) {
        val contentBuilder = StringBuilder()
        try {
            Files.lines(Paths.get("lipsum.txt"), StandardCharsets.UTF_8).use { stream -> stream.forEach { contentBuilder.append(it).append(' ') } }
        } catch (e: IOException) {
            LOGGER.error(e.message, e)
        }
        val txt = contentBuilder.toString()
        for (i in 0 until OptionKey.DEMO_DOCS.int) {
            val rngTags: MutableSet<Tag> = HashSet()
            for (tag in tags) {
                if (random!!.nextFloat() > 0.7f) {
                    rngTags.add(tag)
                }
            }
            val date = LocalDate.of(2016 + random!!.nextInt(4), 1 + random!!.nextInt(12), 1 + random!!.nextInt(28))
            val r1 = random!!.nextInt(txt.length)
            val r2 = r1 + min(random!!.nextInt(txt.length - r1), 5000)
            docService.create(Doc(date, txt.substring(r1, r2), UUID.randomUUID().toString(), rngTags))
        }
    }

    private fun createDemoTags(): Set<Tag> {
        val tags: MutableSet<Tag> = HashSet()
        val attributes: MutableSet<Attribute> = HashSet()
        attributes.add(attributeService.findByName("Firma").orElse(attributeService.create(Attribute("Firma", false, Attribute.Type.STRING))))
        attributes.add(attributeService.findByName("Betrag").orElse(attributeService.create(Attribute("Betrag", false, Attribute.Type.FLOAT))))
        attributes.add(attributeService.findByName("Garantie").orElse(attributeService.create(Attribute("Garantie", false, Attribute.Type.DATE))))
        tags.add(tagService.create(Tag("Rechnung", randomColor(), attributes)))
        tags.add(tagService.create(Tag("Auto", randomColor())))
        tags.add(tagService.create(Tag("Beleg", randomColor())))
        tags.add(tagService.create(Tag("Einkauf", randomColor())))
        tags.add(tagService.create(Tag("Steuer", randomColor())))
        tags.add(tagService.create(Tag("Arbeit", randomColor())))
        tags.add(tagService.create(Tag("Test", randomColor())))
        return tags
    }

    private fun randomColor() = String.format("#%06x", random!!.nextInt(0xffffff + 1))

    private fun createReviewTag() {
        if (OptionKey.AUTO_TAG.boolean) {
            val reviewTag = tagService.create(Tag("Review", "red"))
            OptionKey.AUTO_TAG_ID.float = reviewTag.id.toFloat()
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DemoDataManager::class.java)
    }
}