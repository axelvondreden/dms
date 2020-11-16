package com.dude.dms.brain.parsing

import com.dude.dms.backend.service.AttributeService
import com.dude.dms.backend.service.TagService
import org.springframework.stereotype.Component

@Component
class SearchParser(
        private val tagService: TagService,
        private val attributeService: AttributeService) {

    fun refresh() {
        //tagIncludeFilter.setItems(tagService.findAll())
        //attributeIncludeFilter.setItems(attributeService.findAll())
    }
}