package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.brain.OptionKey;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;

import java.io.*;

public class DocCreateDialog extends EventDialog {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(DocCreateDialog.class);

    public DocCreateDialog() {
        setWidth("40vw");
        setHeight("40vh");

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".pdf");
        upload.setMaxFileSize(OptionKey.MAX_UPLOAD_FILE_SIZE.getInt() * 1024 * 1024);
        upload.addFinishedListener(e -> {
            for (String file : buffer.getFiles()) {
                try (InputStream inputStream = buffer.getInputStream(file)) {
                    byte[] bytes = new byte[inputStream.available()];
                    inputStream.read(bytes);

                    File targetFile = new File(OptionKey.DOC_POLL_PATH.getString(), file);
                    LOGGER.info("Writing file {}...", file);
                    try (OutputStream outStream = new FileOutputStream(targetFile)) {
                        outStream.write(bytes);
                    }
                    LOGGER.info("Done");
                    close();
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        });
        upload.setHeight("80%");
        add(upload);
    }
}