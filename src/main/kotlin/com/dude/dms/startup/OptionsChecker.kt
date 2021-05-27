package com.dude.dms.startup

import com.dude.dms.utils.optionsDefaultPath
import com.dude.dms.utils.optionsPath
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileOutputStream

@Component
class OptionsChecker {

    fun checkOptions() {
        val objectMapper = jacksonObjectMapper()
        val defaultTree = objectMapper.readTree(File(optionsDefaultPath))
        val json = File(optionsPath)
        if (!json.exists()) {
            json.createNewFile()
            objectMapper.writeValue(FileOutputStream(optionsPath), defaultTree)
        } else {
            val userTree = objectMapper.readTree(File(optionsPath))
            if (defaultTree != userTree) {
                updateOptions(defaultTree, userTree)
                objectMapper.writeValue(FileOutputStream(optionsPath), userTree)
            }
        }
    }

    private fun updateOptions(default: JsonNode, user: JsonNode) {
        default.fields().forEach { field ->
            if (!user.has(field.key)) {
                (user as ObjectNode).set<ObjectNode>(field.key, field.value)
            } else if (field.value.isContainerNode) {
                updateOptions(field.value, user[field.key])
            }
        }
    }
}
