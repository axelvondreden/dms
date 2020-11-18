package com.dude.dms.startup

import com.dude.dms.brain.FileManager
import com.dude.dms.brain.options.Options
import org.springframework.stereotype.Component
import java.io.File

@Component
class DirectoryChecker(private val fileManager: FileManager) {

    fun checkDirectories() {
        val pollDir = File(Options.get().doc.pollingPath)
        if (!pollDir.exists()) {
            pollDir.mkdir()
        }
        fileManager.createDirectories()
    }
}