package com.dude.dms.brain.options

import com.dude.dms.brain.DmsLogger
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

data class Options (
        var view: ViewOptions,
        var doc: DocOptions,
        var mail: MailOptions,
        var storage: StorageOptions,
        var tag: TagOptions,
        var update: UpdateOptions) {

    fun save() {
        options = null
        jacksonObjectMapper().writeValue(FileOutputStream("options.json"), this)
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(Options::class.java)
        private var options: Options? = null

        fun get(): Options {
            if (options == null) {
                try {
                    options = jacksonObjectMapper().readValue(File("options.json").readText(), Options::class.java)
                } catch (e: FileNotFoundException) {
                    LOGGER.warn("Options not found: Using default options...")
                    return jacksonObjectMapper().readValue(File("options.default.json").readText(), Options::class.java)
                }
            }
            return options!!
        }
    }
}