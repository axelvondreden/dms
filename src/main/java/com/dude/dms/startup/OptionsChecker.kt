package com.dude.dms.startup

import com.dude.dms.brain.DmsLogger
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
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
            LOGGER.info("Creating options.json...")
            objectMapper.writeValue(FileOutputStream("options.json"), defaultTree)
        } else {
            val userTree = objectMapper.readTree(File("options.json"))
            if (defaultTree != userTree) {
                LOGGER.info("Updating options.json...")
                updateOptions(defaultTree, userTree)
                objectMapper.writeValue(FileOutputStream("options.json"), userTree)
            }
        }
    }

    private fun updateOptions(default: JsonNode, user: JsonNode) {
        for (defaultField in default.fields()) {
            if (!user.has(defaultField.key)) {
                LOGGER.info("Creating ${defaultField.key}...")
                (user as ObjectNode).set(defaultField.key, defaultField.value)
            } else if (defaultField.value.isContainerNode) {
                updateOptions(defaultField.value, user[defaultField.key])
            }
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(OptionsChecker::class.java)
    }
}