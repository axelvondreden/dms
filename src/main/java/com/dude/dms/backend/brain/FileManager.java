package com.dude.dms.backend.brain;

import com.dude.dms.backend.data.docs.Doc;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import static com.dude.dms.backend.brain.OptionKey.*;

public final class FileManager {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(FileManager.class);

    private FileManager() {
    }

    public static boolean testFtp() {
        FTPClient ftpClient = new FTPClient();
        try {
            try {
                ftpClient.setConnectTimeout(3000);
                ftpClient.connect(FTP_URL.getString(), FTP_PORT.getInt());
                ftpClient.login(FTP_USER.getString(), FTP_PASSWORD.getString());
                ftpClient.listNames();
                ftpClient.logout();
                return true;
            } finally {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            LOGGER.showError(e.getMessage());
            return false;
        }
    }

    public static File getDocImage(Doc doc) {
        return new File(DOC_SAVE_PATH.getString() + "/img/" + doc.getGuid() + "_00.png");
    }

    public static File getDocPdf(Doc doc) {
        return Paths.get(DOC_SAVE_PATH.getString(), "pdf", doc.getGuid() + ".pdf").toAbsolutePath().toFile();
    }

    public static void createDirectories() {
        File saveDir = new File(DOC_SAVE_PATH.getString());
        if (!saveDir.exists()) {
            LOGGER.info("Creating directory for saved docs {}", saveDir);
            saveDir.mkdir();
            new File(saveDir, "pdf").mkdir();
            new File(saveDir, "img").mkdir();
        }
    }

    public static Optional<File> savePdf(File file, String guid) {
        Path targetPath = Paths.get(DOC_SAVE_PATH.getString(), "pdf", guid + ".pdf");
        LOGGER.info("Saving PDF {}...", targetPath);
        try {
            return Optional.of(Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING).toFile());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return Optional.empty();
        }
    }

    public static void saveImage(PDDocument pdDoc, String guid) throws IOException {
        PDFRenderer pr = new PDFRenderer(pdDoc);
        for (int i = 0; i < pdDoc.getNumberOfPages(); i++) {
            try {
                BufferedImage bi = pr.renderImageWithDPI(i, IMAGE_PARSER_DPI.getFloat());
                File out = new File(DOC_SAVE_PATH.getString(), String.format("img/%s_%02d.png", guid, i));
                LOGGER.info("Saving Image {}...", out.getAbsolutePath());
                ImageIO.write(bi, "PNG", out);
            } catch (EOFException e) {
                LOGGER.error("Error when saving image: EOFException {}", e.getMessage());
            }
        }
    }
}