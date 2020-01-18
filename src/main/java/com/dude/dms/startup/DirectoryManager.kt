package com.dude.dms.startup

import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.OptionKey
import org.springframework.stereotype.Component
import java.io.File

@Component
class DirectoryManager(private val fileManager: FileManager) {

    fun checkDirectories() {
        val pollDir = File(OptionKey.DOC_POLL_PATH.string)
        if (!pollDir.exists()) {
            LOGGER.info("Creating input directory new docs {}", pollDir)
            pollDir.mkdir()
        }
        fileManager.createDirectories()
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(DirectoryManager::class.java)
    }
}