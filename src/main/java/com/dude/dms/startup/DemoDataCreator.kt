package com.dude.dms.startup

import com.dude.dms.backend.data.Tag
import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.TagService
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.*

@Component
class DemoDataCreator(private val tagService: TagService, private val attributeService: AttributeService) {

    private var random: Random? = null

    fun createDemoData() {
        random = SecureRandom()
        createDemoTags()
    }

    private fun createDemoTags(): Set<Tag> {
        val attributes = setOf(
                attributeService.findByName("Firma")
                        ?: (attributeService.create(Attribute("Firma", false, Attribute.Type.STRING))),
                attributeService.findByName("Betrag")
                        ?: (attributeService.create(Attribute("Betrag", false, Attribute.Type.FLOAT))),
                attributeService.findByName("Garantie")
                        ?: (attributeService.create(Attribute("Garantie", false, Attribute.Type.DATE)))
        )
        return hashSetOf(
                tagService.create(Tag("Rechnung", randomColor(), attributes = attributes)),
                tagService.create(Tag("Auto", randomColor())),
                tagService.create(Tag("Beleg", randomColor())),
                tagService.create(Tag("Einkauf", randomColor())),
                tagService.create(Tag("Steuer", randomColor())),
                tagService.create(Tag("Arbeit", randomColor())),
                tagService.create(Tag("Test", randomColor()))
        )
    }

    private fun randomColor() = String.format("#%06x", random!!.nextInt(0xffffff + 1))
}