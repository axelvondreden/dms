package com.dude.dms.updater

import com.dude.dms.brain.DmsLogger
import com.dude.dms.startup.ShutdownManager
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@Component
class UpdateInstaller(private val shutdownManager: ShutdownManager) {

    fun install(name: String) {
        LOGGER.info("Extracting {}...", name)
        val destDir = File("update")
        if (!destDir.exists()) {
            destDir.mkdir()
        }
        val buffer = ByteArray(1024)
        try {
            ZipInputStream(FileInputStream(name)).use { zis ->
                var zipEntry = zis.nextEntry
                while (zipEntry != null) {
                    val newFile = newFile(destDir, zipEntry)
                    FileOutputStream(newFile).use { fos ->
                        var len: Int
                        while (zis.read(buffer).also { len = it } > 0) {
                            fos.write(buffer, 0, len)
                        }
                    }
                    zipEntry = zis.nextEntry
                }
                zis.closeEntry()
                LOGGER.info("Processing update...")
                shutdownManager.initiateShutdown(1337)
            }
        } catch (e: IOException) {
            e.message?.let { LOGGER.error(it, e) }
        }
    }

    private fun newFile(destinationDir: File, zipEntry: ZipEntry): File {
        val destFile = File(destinationDir, zipEntry.name)
        val destDirPath = destinationDir.canonicalPath
        val destFilePath = destFile.canonicalPath
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw IOException("Entry is outside of the target dir: ${zipEntry.name}")
        }
        destFile.createNewFile()
        return destFile
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(UpdateInstaller::class.java)
    }
}