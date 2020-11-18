package com.dude.dms.brain.parsing.search

import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.TagService

class SearchParser(
        private val tagService: TagService,
        private val attributeService: AttributeService) {

    private var blocks = emptyList<Block>()

    val filter
        get() = ""

    fun setInput(text: String): String? {
        try {
            //blocks = parseBlocks(text)
        } catch (e: ParseException) {
            return e.message
        }
        return null
    }

    /*fun parseBlocks(text: String): List<Block> {
        var index = 0
        val max = text.length
        var currentText = ""
        var inString = false
        var braceCounter = 0
        val parts = mutableListOf<Part>()
        while (index < max) {
            val c = text[index]
            when {
                inString -> {
                    when (c) {
                        '"' -> {
                            inString = false
                            parts.add(StringPart(currentText))
                            currentText = ""
                        }
                        else -> currentText += c
                    }
                }
                braceCounter > 0 -> {
                    when (c) {
                        '(' -> braceCounter++
                        ')' -> braceCounter--
                    }
                    if (braceCounter > 0) {
                        currentText += c
                    } else {
                        parts.add(Block(currentText))
                        currentText = ""
                    }
                }
                c == ' ' -> {
                    if (currentText.isNotBlank()) {
                        parts.add(KeyPart(currentText))
                        currentText = ""
                    }
                }
                c == '"' -> inString = true
                c == '(' -> braceCounter++
                c == ')' -> throw ParseException("unexpected ')'")
                else -> currentText += c
            }
            index++
        }
        if (currentText.isNotBlank()) {
            parts.add(KeyPart(currentText))
        }
        println(parts.joinToString(" ") { it.debug() })
        if (inString) throw ParseException("unclosed String")
        if (braceCounter > 0) throw ParseException("unclosed braces")
        return parts
    }*/

    /*fun old(text: String): List<Block> {
        var index = 0
        val max = text.length
        var currentText = ""
        var inString = false
        var braceCounter = 0
        val parts = mutableListOf<Part>()
        while (index < max) {
            val c = text[index]
            when {
                inString -> {
                    when (c) {
                        '"' -> {
                            inString = false
                            parts.add(StringPart(currentText))
                            currentText = ""
                        }
                        else -> currentText += c
                    }
                }
                braceCounter > 0 -> {
                    when (c) {
                        '(' -> braceCounter++
                        ')' -> braceCounter--
                    }
                    if (braceCounter > 0) {
                        currentText += c
                    } else {
                        parts.add(Block(currentText))
                        currentText = ""
                    }
                }
                c == ' ' -> {
                    if (currentText.isNotBlank()) {
                        parts.add(KeyPart(currentText))
                        currentText = ""
                    }
                }
                c == '"' -> inString = true
                c == '(' -> braceCounter++
                c == ')' -> throw ParseException("unexpected ')'")
                else -> currentText += c
            }
            index++
        }
        if (currentText.isNotBlank()) {
            parts.add(KeyPart(currentText))
        }
        println(parts.joinToString(" ") { it.debug() })
        if (inString) throw ParseException("unclosed String")
        if (braceCounter > 0) throw ParseException("unclosed braces")
        return parts
    }*/

    fun refresh() {
        //tagIncludeFilter.setItems(tagService.findAll())
        //attributeIncludeFilter.setItems(attributeService.findAll())
    }

    fun getTips(): List<String> {
        if (blocks.isEmpty()) {
            return searchKeys
        }
        return emptyList()
    }

    companion object {
        private val searchKeys = listOf(
                "text", "date", "tag", "attribute"
        )
    }
}