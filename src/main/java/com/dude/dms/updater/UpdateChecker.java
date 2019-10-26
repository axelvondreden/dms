package com.dude.dms.updater;

import com.dude.dms.backend.data.updater.Changelog;
import com.dude.dms.backend.service.ChangelogService;
import com.dude.dms.startup.ShutdownManager;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.dude.dms.backend.brain.OptionKey.*;

@Component
public class UpdateChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateChecker.class);

    private int tick;

    @Value("${api.releases}")
    private String releaseApi;

    @Value("${build.version}")
    private String buildVersion;

    private final ChangelogService changelogService;

    private final UpdateDownloader updateDownloader;

    private final ShutdownManager shutdownManager;

    @Autowired
    public UpdateChecker(ChangelogService changelogService, UpdateDownloader updateDownloader, ShutdownManager shutdownManager) {
        this.changelogService = changelogService;
        this.updateDownloader = updateDownloader;
        this.shutdownManager = shutdownManager;
        tick = 1;
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void scheduledCheck() {
        if (tick < UPDATE_CHECK_INTERVAL.getInt()) {
            tick++;
        } else {
            tick = 1;
            check(false);
        }
    }

    public void check(boolean force) {
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
                    if (force) {
                        updateDownloader.download(newestRelease, restTemplate);
                    } else {
                        UI current = UI.getCurrent();
                        if (current != null) {
                            Version finalNewest = newest;
                            current.access(() -> {
                                Button b = new Button("Restart now", e -> shutdownManager.initiateShutdown(1337));
                                b.addThemeVariants(ButtonVariant.LUMO_ERROR);
                                HorizontalLayout h = new HorizontalLayout();
                                h.setAlignItems(FlexComponent.Alignment.CENTER);
                                h.setWidthFull();
                                Notification n = new Notification(h);
                                n.setDuration(0);
                                n.setPosition(Notification.Position.BOTTOM_STRETCH);
                                h.add(new Label("New Version available: " + finalNewest.get()), b, new Button(VaadinIcon.CLOSE.create(), e -> n.close()));
                                n.open();
                            });
                        }
                    }
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