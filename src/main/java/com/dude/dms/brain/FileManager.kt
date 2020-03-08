package com.dude.dms.brain

import com.dude.dms.brain.DmsLogger.Companion.getLogger
import com.dude.dms.brain.options.Options
import org.apache.commons.net.ftp.FTPClient
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.awt.image.RescaleOp
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

    fun getPdf(guid: String) = File("${Options.get().doc.savePath}/pdf/$guid.pdf")

    fun getFirstImage(guid: String) = File("${Options.get().doc.savePath}/img/${guid}_00.png")

    fun getImages(guid: String) = File("${Options.get().doc.savePath}/img/").listFiles { _, name -> name.startsWith(guid) }

    fun createDirectories() {
        val saveDir = File(Options.get().doc.savePath)
        if (!saveDir.exists()) {
            LOGGER.info("Creating directory for saved docs {}", saveDir)
            saveDir.mkdir()
            File(saveDir, "pdf").mkdir()
            File(saveDir, "img").mkdir()
        }
    }

    fun importPdf(pdf: File): String? {
        val guid = UUID.randomUUID().toString()
        val targetPath = Paths.get(Options.get().doc.savePath, "pdf", "$guid.pdf")
        LOGGER.info("Importing PDF {}...", pdf.name)
        try {
            Files.move(pdf.toPath(), targetPath, StandardCopyOption.ATOMIC_MOVE).toFile()
        } catch (e: IOException) {
            LOGGER.error(e.message!!, e)
            return null
        }

        createImageFromPdf(guid)
        return guid
    }

    fun importImage(img: File): String? {
        val guid = UUID.randomUUID().toString()
        LOGGER.info("Importing Image {}...", img.name)
        try {
            ImageIO.write(processImg(ImageIO.read(img)), "png", Paths.get(Options.get().doc.savePath, "img", "${guid}_00.png").toFile())
            img.delete()
        } catch (e: IOException) {
            LOGGER.error(e.message!!, e)
            return null
        }

        createPdfFromImage(guid)
        return guid
    }

    private fun createImageFromPdf(guid: String) {
        val pdDoc = PDDocument.load(getPdf(guid))
        val pr = PDFRenderer(pdDoc)
        for (i in 0 until pdDoc.numberOfPages) {
            try {
                val bi = processImg(pr.renderImageWithDPI(i, Options.get().doc.imageParserDpi.toFloat()))
                val out = File(Options.get().doc.savePath, String.format("img/%s_%02d.png", guid, i))
                LOGGER.info("Saving Image {}...", out.name)
                ImageIO.write(bi, "PNG", out)
            } catch (e: IOException) {
                LOGGER.error("Error saving Image: {}", e, e.message)
            }
        }
    }

    private fun createPdfFromImage(guid: String) {
        try {
            LOGGER.info("Saving PDF {}.pdf...", guid)
            val pdf = PDDocument()

            val page = PDPage()
            pdf.addPage(page)

            val pdImage = PDImageXObject.createFromFile(getFirstImage(guid).absolutePath, pdf)
            val contents = PDPageContentStream(pdf, page)
            val mediaBox = page.mediaBox
            val startX = (mediaBox.width - pdImage.width) / 2
            val startY = (mediaBox.height - pdImage.height) / 2
            contents.drawImage(pdImage, startX, startY)

            contents.close()

            pdf.save("${Options.get().doc.savePath}/pdf/$guid.pdf")
            pdf.close()
        } catch (e: IOException) {
            LOGGER.error("Error saving PDF: {}", e, e.message)
        }
    }

    private fun processImg(img: BufferedImage): BufferedImage {
        val rgb = img.getRGB(img.tileWidth / 2, img.tileHeight / 2).toDouble()

        val config = when {
            rgb >= -1.4211511E7 && rgb < -7254228 -> 3f to -10f
            rgb >= -7254228 && rgb < -2171170 -> 1.455f to -47f
            rgb >= -2171170 && rgb < -1907998 -> 1.35f to -10f
            rgb >= -1907998 && rgb < -257 -> 1.19f to 0.5f
            rgb >= -257 && rgb < -1 -> 1f to 0.5f
            rgb >= -1 && rgb < 2 -> 1f to 0.35f
            else -> 1f to 1f
        }

        val processedImg = BufferedImage(img.width, img.height, img.type)
        val graphic = processedImg.createGraphics()
        graphic.drawImage(img, 0, 0, img.width, img.height, null)
        graphic.dispose()

        // rescale OP object for gray scaling images
        val rescale = RescaleOp(config.first, config.second, null)
        return rescale.filter(processedImg, null)
    }

    companion object {
        private val LOGGER = getLogger(FileManager::class.java)
    }
}