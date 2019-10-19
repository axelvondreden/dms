package com.dude.dms.startup;

import com.dude.dms.backend.brain.polling.DocPollingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static com.dude.dms.backend.brain.OptionKey.LOCALE;

@Component
public class StartUpRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartUpRunner.class);

    @Autowired
    private DocPollingService docPollingService;

    @Autowired
    private UpdateChecker updateChecker;

    @Autowired
    private DirectoryManager directoryManager;

    @Autowired
    private DemoDataManager demoDataManager;

    @Override
    public void run(String... args) throws IOException {
        updateChecker.check();
        createOptionsFile();
        directoryManager.checkDirectories();
        demoDataManager.createDemoData();
        docPollingService.manualPoll();
        LocaleContextHolder.setLocale(Locale.forLanguageTag(LOCALE.getString()));
    }

    private static void createOptionsFile() throws IOException {
        File prop = new File("options.properties");
        if (!prop.exists()) {
            LOGGER.info("Creating user properties...");
            prop.createNewFile();
        }
    }
}