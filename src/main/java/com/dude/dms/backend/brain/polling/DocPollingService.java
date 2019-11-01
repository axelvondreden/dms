package com.dude.dms.backend.brain.polling;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.brain.parsing.PdfToDocParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.dude.dms.backend.brain.OptionKey.DOC_POLL_PATH;
import static com.dude.dms.backend.brain.OptionKey.POLL_INTERVAL;

@Component
public class DocPollingService implements PollingService {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(DocPollingService.class);

    private final String docPath;

    private int tick;//test

    @Autowired
    private PdfToDocParser pdfToDocParser;

    public DocPollingService() {
        docPath = DOC_POLL_PATH.getString();
        tick = 1;
    }

    @Scheduled(fixedRate = 1000)
    public void poll() {
        if (tick < POLL_INTERVAL.getInt()) {
            tick++;
        } else {
            tick = 1;
            LOGGER.info("Polling {} for PDFs...", docPath);
            File [] files = new File(docPath).listFiles((file, name) -> name.endsWith(".pdf"));
            if (files != null) {
                for (File file : files) {
                    processFile(file);
                }
            }
        }
    }

    private void processFile(File file) {
        LOGGER.info("Processing file: {}", file.getName());
        pdfToDocParser.parse(file);
    }
}