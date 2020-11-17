package com.dude.dms.brain.parsing.search

import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.TagService

class SearchParser(
        private val tagService: TagService,
        private val attributeService: AttributeService) {

    private var input = emptyArray<String>()

    val filter
        get() = ""

    fun setInput(input: String): Boolean {
        this.input = input.trim().split(Regex("\\s+")).toTypedArray()
        return parse()
    }

    fun refresh() {
        //tagIncludeFilter.setItems(tagService.findAll())
        //attributeIncludeFilter.setItems(attributeService.findAll())
    }

    fun getTips(): List<String> {
        if (input.isEmpty()) {
            return searchKeys
        }
        return emptyList()
    }

    private fun parse(): Boolean {
        return parseBlocks()
    }

    private fun parseBlocks(): Boolean {
        return true
    }

    companion object {
        private val searchKeys = listOf(
                "text", "date", "tag", "attribute"
        )
    }
}