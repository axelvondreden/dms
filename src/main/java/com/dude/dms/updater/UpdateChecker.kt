package com.dude.dms.updater

import com.dude.dms.brain.DmsLogger
import com.dude.dms.brain.OptionKey
import com.dude.dms.backend.data.Changelog
import com.dude.dms.backend.service.ChangelogService
import com.dude.dms.startup.ShutdownManager
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpClientErrorException.Unauthorized
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Component
class UpdateChecker(private val changelogService: ChangelogService, private val updateDownloader: UpdateDownloader, private val shutdownManager: ShutdownManager) {

    private var tick = 1

    @Value("\${api.releases}")
    private lateinit var releaseApi: String

    @Value("\${build.version}")
    private lateinit var buildVersion: String

    @Scheduled(fixedRate = 1000 * 60)
    fun scheduledCheck() {
        if (tick < OptionKey.UPDATE_CHECK_INTERVAL.int) {
            tick++
        } else {
            tick = 1
            check(false)
        }
    }

    fun check(force: Boolean) {
        LOGGER.info("Checking for updates...")
        try {
            val restTemplate = RestTemplate()
            val releases = restTemplate.getForObject(releaseApi, Array<Release>::class.java)
            if (releases != null) {
                var newest = Version(buildVersion)
                var newestRelease: Release? = null
                for (release in releases) {
                    if (changelogService.findByVersion(release.tag_name) == null) {
                        LOGGER.info("Saving Changelog for {}", release.tag_name)
                        changelogService.create(Changelog(release.published_at, release.body, release.tag_name))
                    }
                    val v = Version(release.tag_name)
                    if (v.isAfter(newest)) {
                        newest = v
                        newestRelease = release
                    }
                }
                if (newestRelease != null) {
                    LOGGER.info("New version found: {}", newest.version)
                    if (force) {
                        updateDownloader.download(newestRelease, restTemplate)
                    } else {
                        val current = UI.getCurrent()
                        if (current != null) {
                            val finalNewest = newest
                            current.access {
                                val b = Button("Restart now", VaadinIcon.POWER_OFF.create()) { shutdownManager.initiateShutdown(1337) }.apply {
                                    addThemeVariants(ButtonVariant.LUMO_ERROR)
                                }
                                val h = HorizontalLayout().apply {
                                    alignItems = FlexComponent.Alignment.CENTER
                                    setWidthFull()
                                }
                                val n = Notification(h).apply {
                                    duration = 0
                                    position = Notification.Position.BOTTOM_STRETCH
                                }
                                h.add(Label("New Version available: ${finalNewest.version}"), b, Button(VaadinIcon.CLOSE.create()) { n.close() })
                                n.open()
                            }
                        }
                    }
                } else {
                    LOGGER.info("Already running latest version: {}", buildVersion)
                }
            }
        } catch (e: Unauthorized) {
            LOGGER.warn(e.statusText)
        } catch (e: HttpClientErrorException.NotFound) {
            LOGGER.warn(e.statusText)
        } catch (e: RestClientException) {
            e.message?.let { LOGGER.error(it, e) }
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(UpdateChecker::class.java)
    }
}