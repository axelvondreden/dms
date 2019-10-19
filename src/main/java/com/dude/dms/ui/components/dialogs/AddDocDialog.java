package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.brain.OptionKey;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class AddDocDialog extends Dialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddDocDialog.class);

    public AddDocDialog() {
        setWidth("40vw");
        setHeight("40vh");

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".pdf");
        upload.setMaxFileSize(1048576000);
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