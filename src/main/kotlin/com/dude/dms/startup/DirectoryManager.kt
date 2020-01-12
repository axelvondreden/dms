package com.dude.dms.startup

import com.dude.dms.backend.brain.DmsLogger
import com.dude.dms.backend.brain.FileManager
import com.dude.dms.backend.brain.OptionKey
import org.springframework.stereotype.Component
import java.io.File

@Component
class DirectoryManager {

    fun checkDirectories() {
        val pollDir = File(OptionKey.DOC_POLL_PATH.string)
        if (!pollDir.exists()) {
            LOGGER.info("Creating input directory new docs {}", pollDir)
            pollDir.mkdir()
        }
        FileManager.createDirectories()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DirectoryManager::class.java)
    }
}