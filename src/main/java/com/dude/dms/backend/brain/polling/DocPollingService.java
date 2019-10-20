package com.dude.dms.backend.brain.polling;

import com.dude.dms.backend.brain.parsing.PdfToDocParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.dude.dms.backend.brain.OptionKey.DOC_POLL_PATH;

@Component
public class DocPollingService implements PollingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocPollingService.class);

    private final String docPath;

    @Autowired
    private PdfToDocParser pdfToDocParser;

    public DocPollingService() {
        docPath = DOC_POLL_PATH.getString();
    }

    @Scheduled(fixedRate = 10000)
    public void poll() {
        LOGGER.info("Polling {} for PDFs...", docPath);
        File [] files = new File(docPath).listFiles((file, name) -> name.endsWith(".pdf"));
        if (files != null) {
            for (File file : files) {
                processFile(file);
            }
        }
    }

    private void processFile(File file) {
        LOGGER.info("Processing file: {}", file.getName());
        pdfToDocParser.parse(file);
    }
}