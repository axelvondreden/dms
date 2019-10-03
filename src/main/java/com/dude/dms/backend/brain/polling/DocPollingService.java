package com.dude.dms.backend.brain.polling;

import com.dude.dms.backend.brain.BrainUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Component
public class DocPollingService {

    private WatchService watcher;

    private final String docPath;

    public DocPollingService() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        docPath = BrainUtils.getProperty("doc_path");
        Paths.get(docPath).register(watcher, ENTRY_CREATE);

        //TODO: check dir on startup
    }

    @Scheduled(fixedRate = 10000)
    public void poll() {
        System.out.println("Polling...");
        WatchKey key = watcher.poll();
        if (key != null) {
            List<File> files = new ArrayList<>();
            for (WatchEvent<?> event : key.pollEvents()) {
                Path filename = ((WatchEvent<Path>) event).context();
                File file = new File(docPath, filename.getFileName().toString());
                if (file.exists() && file.isFile() && file.canRead()) {
                    // check for pdf
                    String[] name = file.getName().split("\\.");
                    if (name[name.length - 1].equals("pdf")) {
                        files.add(file);
                    }
                }
            }
            key.reset();

            // process files
            for (File file : files) {
                System.out.println("Processing file: " + file.getName());
            }
        }
    }
}