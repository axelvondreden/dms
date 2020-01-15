package com.dude.dms.backend.brain

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object BrainUtils {

    private val LOGGER = DmsLogger.getLogger(BrainUtils::class.java)

    @JvmStatic
    fun getProperty(key: OptionKey) = getUserProperty(key) ?: getDefaultProperty(key)

    @JvmStatic
    fun setProperty(key: OptionKey, value: String?) {
        val prop = getProperties("options.properties")
        prop.setProperty(key.key, value)
        try {
            FileOutputStream(File("options.properties")).use { output -> prop.store(output, null) }
        } catch (io: IOException) {
            io.message?.let { LOGGER.error(it) }
        }
    }

    private fun getDefaultProperty(key: OptionKey) = getProperties("options.default.properties").getProperty(key.key)

    private fun getUserProperty(key: OptionKey) = getProperties("options.properties").getProperty(key.key)

    private fun getProperties(file: String): Properties {
        val prop = Properties()
        try {
            FileInputStream(File(file)).use { input -> prop.load(input) }
        } catch (ex: IOException) {
            ex.message?.let { LOGGER.error(it) }
        }
        return prop
    }
}