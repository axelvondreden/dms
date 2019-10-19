package com.dude.dms;

import com.dude.dms.backend.brain.polling.DocPollingService;
import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.tags.Tag;
import com.dude.dms.backend.data.updater.Changelog;
import com.dude.dms.backend.service.ChangelogService;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.updater.Asset;
import com.dude.dms.updater.Release;
import com.dude.dms.updater.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.dude.dms.backend.brain.OptionKey.*;

@Component
public class StartUpRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartUpRunner.class);

    @Autowired
    private TagService tagService;

    @Autowired
    private DocService docService;

    @Autowired
    private DocPollingService docPollingService;

    @Autowired
    private ChangelogService changelogService;

    @Autowired
    private ShutdownManager shutdownManager;

    private Random random;

    @Value("${build.version}")
    private String buildVersion;

    @Override
    public void run(String... args) throws IOException {
        checkForUpdate();
        createOptionsFile();
        createDirectories();
        createTags();
        docPollingService.manualPoll();
        LocaleContextHolder.setLocale(Locale.forLanguageTag(LOCALE.getString()));

        createDemoData();
    }

    private void checkForUpdate() {
        LOGGER.info("Checking for updates...");
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(GITHUB_USER.getString(), GITHUB_PASSWORD.getString()));
            Release[] releases = restTemplate.getForObject("https://api.github.com/repos/axelvondreden/dms/releases", Release[].class);
            if (releases != null) {
                Version newest = new Version(buildVersion);
                Release newestRelease = null;
                for (Release release : releases) {
                    if (!changelogService.findByVersion(release.getTag_name()).isPresent()) {
                        LOGGER.info("Saving Changelog for {}", release.getTag_name());
                        changelogService.create(new Changelog(release.getPublished_at(), release.getBody(), release.getTag_name()));
                    }
                    Version v = new Version(release.getTag_name());
                    if (v.isAfter(newest)) {
                        newest = v;
                        newestRelease = release;
                    }
                }
                if (newestRelease != null) {
                    LOGGER.info("New version found: {}", newest.get());
                    downloadRelease(newestRelease, restTemplate);
                } else {
                    LOGGER.info("Already running latest version: {}", buildVersion);
                }
            }
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void downloadRelease(Release newestRelease, RestTemplate restTemplate) {
        File old = new File("update");
        if (old.exists() && old.isDirectory()) {
            old.delete();
        }
        RequestCallback requestCallback = request -> request.getHeaders().setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        for (Asset asset : newestRelease.getAssets()) {
            ResponseExtractor<Void> responseExtractor = response -> {
                Files.copy(response.getBody(), Paths.get(asset.getName()), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Download finished: {}", asset.getName());
                unzip(asset.getName());
                return null;
            };
            LOGGER.info("Starting download: {}", asset.getName());
            restTemplate.execute(URI.create(asset.getUrl()), HttpMethod.GET, requestCallback, responseExtractor);
        }
    }

    private void unzip(String name) {
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
            update();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void update() {
        LOGGER.info("Processing update...");
        shutdownManager.initiateShutdown(1337);
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        destFile.createNewFile();
        return destFile;
    }

    private static void createDirectories() {
        File pollDir = new File(DOC_POLL_PATH.getString());
        if (!pollDir.exists()) {
            LOGGER.info("Creating input directory new docs {}", pollDir);
            pollDir.mkdir();
        }
        File saveDir = new File(DOC_SAVE_PATH.getString());
        if (!saveDir.exists()) {
            LOGGER.info("Creating directory for saved docs {}", saveDir);
            saveDir.mkdir();
            new File(saveDir, "pdf").mkdir();
            new File(saveDir, "img").mkdir();
        }
    }

    private void createTags() {
        if (AUTO_REVIEW_TAG.getBoolean()) {
            Tag reviewTag = tagService.create(new Tag("Review", "red"));
            REVIEW_TAG_ID.setFloat(reviewTag.getId());
        }
    }

    private static void createOptionsFile() throws IOException {
        File prop = new File("options.properties");
        if (!prop.exists()) {
            LOGGER.info("Creating user properties...");
            prop.createNewFile();
        }
    }

    private void createDemoData() {
        random = new SecureRandom();
        Collection<Tag> tags = new HashSet<>();
        tags.add(tagService.create(new Tag("Rechnung", randomColor())));
        tags.add(tagService.create(new Tag("Auto", randomColor())));
        tags.add(tagService.create(new Tag("Beleg", randomColor())));
        tags.add(tagService.create(new Tag("Einkauf", randomColor())));
        tags.add(tagService.create(new Tag("Steuer", randomColor())));
        tags.add(tagService.create(new Tag("Arbeit", randomColor())));
        tags.add(tagService.create(new Tag("Test", randomColor())));

        if (docService.count() == 0L && DEMO_DOCS.getInt() > 0) {
            LOGGER.info("Creating demo docs...");

            StringBuilder contentBuilder = new StringBuilder();
            try (Stream<String> stream = Files.lines( Paths.get("lipsum.txt"), StandardCharsets.UTF_8)) {
                stream.forEach(s -> contentBuilder.append(s).append(' '));
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            String txt = contentBuilder.toString();
            for (int i = 0; i < DEMO_DOCS.getInt(); i++) {
                Set<Tag> rngTags = new HashSet<>();
                for (Tag tag : tags) {
                    if (random.nextFloat() > 0.7F) {
                        rngTags.add(tag);
                    }
                }
                LocalDate date = LocalDate.of(2016 + random.nextInt(4), 1 + random.nextInt(12), 1 + random.nextInt(28));
                int r1 = random.nextInt(txt.length());
                int r2 = r1 + Math.min(random.nextInt(txt.length() - r1), 5000);
                docService.create(new Doc(date, txt.substring(r1, r2), UUID.randomUUID().toString(), rngTags));
            }
        }
    }

    private String randomColor() {
        return String.format("#%06x", random.nextInt(0xffffff + 1));
    }
}