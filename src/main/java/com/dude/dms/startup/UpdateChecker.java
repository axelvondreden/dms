package com.dude.dms.startup;

import com.dude.dms.backend.data.updater.Changelog;
import com.dude.dms.backend.service.ChangelogService;
import com.dude.dms.updater.Release;
import com.dude.dms.updater.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.dude.dms.backend.brain.OptionKey.GITHUB_PASSWORD;
import static com.dude.dms.backend.brain.OptionKey.GITHUB_USER;

@Component
class UpdateChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateChecker.class);

    @Value("${api.releases}")
    private String releaseApi;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private ChangelogService changelogService;

    @Autowired
    private UpdateDownloader updateDownloader;

    public void check() {
        LOGGER.info("Checking for updates...");
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(GITHUB_USER.getString(), GITHUB_PASSWORD.getString()));
            Release[] releases = restTemplate.getForObject(releaseApi, Release[].class);
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
                    updateDownloader.download(newestRelease, restTemplate);
                } else {
                    LOGGER.info("Already running latest version: {}", buildVersion);
                }
            }
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.NotFound e) {
            LOGGER.warn(e.getStatusText());
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}