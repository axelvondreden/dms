package com.dude.dms.startup

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
        val defaultTree = objectMapper.readTree(File("options.default.json"))
        val json = File("options.json")
        if (!json.exists()) {
            objectMapper.writeValue(FileOutputStream("options.json"), defaultTree)
        } else {
            val userTree = objectMapper.readTree(File("options.json"))
            if (defaultTree != userTree) {
                updateOptions(defaultTree, userTree)
                objectMapper.writeValue(FileOutputStream("options.json"), userTree)
            }
        }
    }

    private fun updateOptions(default: JsonNode, user: JsonNode) {
        for (defaultField in default.fields()) {
            if (!user.has(defaultField.key)) {
                (user as ObjectNode).set<ObjectNode>(defaultField.key, defaultField.value)
            } else if (defaultField.value.isContainerNode) {
                updateOptions(defaultField.value, user[defaultField.key])
            }
        }
    }
}