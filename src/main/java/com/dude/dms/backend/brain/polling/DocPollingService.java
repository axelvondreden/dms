package com.dude.dms.backend.brain.polling;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.backend.brain.pdf.PdfToDocParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Component
public class DocPollingService implements PollingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocPollingService.class);

    private final WatchService watcher;

    private final String docPath;

    @Autowired
    private PdfToDocParser pdfToDocParser;

    public DocPollingService() throws IOException {
        try (FileSystem fileSystem = FileSystems.getDefault()) {
            watcher = fileSystem.newWatchService();
            docPath = BrainUtils.getProperty("doc_path");
            if (docPath != null) {
                Paths.get(docPath).register(watcher, ENTRY_CREATE);
            }
        }

        //TODO: check dir on startup
    }

    @Scheduled(fixedRate = 10000)
    public void poll() {
        LOGGER.info("Polling...");
        WatchKey key = watcher.poll();
        if (key != null) {
            Collection<File> files = pollForFiles(key);

            // process files
            for (File file : files) {
                LOGGER.info("Processing file: {}", file.getName());
                pdfToDocParser.parse(file);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<File> pollForFiles(WatchKey key) {
        Collection<File> files = new ArrayList<>();
        for (WatchEvent<?> event : key.pollEvents()) {
            Path filename = ((WatchEvent<Path>) event).context();
            File file = new File(docPath, filename.getFileName().toString());
            if (file.exists() && file.isFile() && file.canRead()) {
                // check for pdf
                String[] name = file.getName().split("\\.");
                if ("pdf".equals(name[name.length - 1])) {
                    files.add(file);
                }
            }
        }
        key.reset();
        return files;
    }
}