package com.dude.dms.updater;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.startup.ShutdownManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class UpdateInstaller {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(UpdateInstaller.class);

    @Autowired
    private ShutdownManager shutdownManager;

    public void install(String name) {
        LOGGER.info("Extracting {}...", name);
        File destDir = new File("update");
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(name))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            LOGGER.info("Processing update...");
            shutdownManager.initiateShutdown(1337);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        destFile.createNewFile();
        return destFile;
    }
}