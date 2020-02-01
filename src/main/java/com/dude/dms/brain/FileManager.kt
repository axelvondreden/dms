package com.dude.dms.brain

import com.dude.dms.brain.DmsLogger.Companion.getLogger
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.brain.options.Options
import org.apache.commons.net.ftp.FTPClient
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import javax.imageio.ImageIO

@Component
class FileManager {

    fun testFtp(): Boolean {
        val ftpClient = FTPClient()
        return try {
            try {
                ftpClient.connectTimeout = 3000
                ftpClient.connect(Options.get().storage.ftp.url, Options.get().storage.ftp.port)
                ftpClient.login(Options.get().storage.ftp.url, Options.get().storage.ftp.password)
                ftpClient.listNames()
                ftpClient.logout()
                true
            } finally {
                ftpClient.disconnect()
            }
        } catch (e: IOException) {
            LOGGER.error(e.message!!)
            false
        }
    }

    fun getDocImage(doc: Doc) = File("${Options.get().doc.savePath}/img/${doc.guid}_00.png")

    fun getDocPdf(doc: Doc) = Paths.get(Options.get().doc.savePath, "pdf", "${doc.guid}.pdf").toAbsolutePath().toFile()

    fun createDirectories() {
        val saveDir = File(Options.get().doc.savePath)
        if (!saveDir.exists()) {
            LOGGER.info("Creating directory for saved docs {}", saveDir)
            saveDir.mkdir()
            File(saveDir, "pdf").mkdir()
            File(saveDir, "img").mkdir()
        }
    }

    fun importFile(file: File): File? {
        val guid = UUID.randomUUID().toString()
        val targetPath = Paths.get(Options.get().doc.savePath, "pdf", "$guid.pdf")
        LOGGER.info("Importing file {}...", targetPath)
        return try {
            Files.move(file.toPath(), targetPath, StandardCopyOption.ATOMIC_MOVE).toFile()
        } catch (e: IOException) {
            LOGGER.error(e.message!!)
            null
        }
    }

    fun saveImage(pdDoc: PDDocument, guid: String) {
        val pr = PDFRenderer(pdDoc)
        for (i in 0 until pdDoc.numberOfPages) {
            try {
                val bi = pr.renderImageWithDPI(i, Options.get().doc.imageParserDpi.toFloat())
                val out = File(Options.get().doc.savePath, String.format("img/%s_%02d.png", guid, i))
                LOGGER.info("Saving Image {}...", out.absolutePath)
                ImageIO.write(bi, "PNG", out)
            } catch (e: IOException) {
                LOGGER.error("Error when saving image: {}", e.message)
            }
        }
    }

    companion object {
        private val LOGGER = getLogger(FileManager::class.java)
    }
}