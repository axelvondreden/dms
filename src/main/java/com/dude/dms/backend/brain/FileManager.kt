package com.dude.dms.backend.brain

import com.dude.dms.backend.brain.DmsLogger.Companion.getLogger
import com.dude.dms.backend.data.docs.Doc
import org.apache.commons.net.ftp.FTPClient
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import javax.imageio.ImageIO

object FileManager {

    private val LOGGER = getLogger(FileManager::class.java)

    fun testFtp(): Boolean {
        val ftpClient = FTPClient()
        return try {
            try {
                ftpClient.connectTimeout = 3000
                ftpClient.connect(OptionKey.FTP_URL.string, OptionKey.FTP_PORT.int)
                ftpClient.login(OptionKey.FTP_USER.string, OptionKey.FTP_PASSWORD.string)
                ftpClient.listNames()
                ftpClient.logout()
                true
            } finally {
                ftpClient.disconnect()
            }
        } catch (e: IOException) {
            LOGGER.showError(e.message!!)
            false
        }
    }

    fun getDocImage(doc: Doc) = File("${OptionKey.DOC_SAVE_PATH.string}/img/${doc.guid}_00.png")

    fun getDocPdf(doc: Doc) = Paths.get(OptionKey.DOC_SAVE_PATH.string, "pdf", "${doc.guid}.pdf").toAbsolutePath().toFile()

    fun createDirectories() {
        val saveDir = File(OptionKey.DOC_SAVE_PATH.string)
        if (!saveDir.exists()) {
            LOGGER.info("Creating directory for saved docs {}", saveDir)
            saveDir.mkdir()
            File(saveDir, "pdf").mkdir()
            File(saveDir, "img").mkdir()
        }
    }

    fun importFile(file: File): File? {
        val guid = UUID.randomUUID().toString()
        val targetPath = Paths.get(OptionKey.DOC_SAVE_PATH.string, "pdf", "$guid.pdf")
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
                val bi = pr.renderImageWithDPI(i, OptionKey.IMAGE_PARSER_DPI.float)
                val out = File(OptionKey.DOC_SAVE_PATH.string, String.format("img/%s_%02d.png", guid, i))
                LOGGER.info("Saving Image {}...", out.absolutePath)
                ImageIO.write(bi, "PNG", out)
            } catch (e: IOException) {
                LOGGER.error("Error when saving image: {}", e.message)
            }
        }
    }
}