package com.dude.dms.updater

import com.dude.dms.backend.brain.DmsLogger
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Component
class UpdateDownloader(private val updateInstaller: UpdateInstaller) {

    fun download(newestRelease: Release, restTemplate: RestTemplate) {
        val old = File("update")
        if (old.exists() && old.isDirectory) {
            old.delete()
        }
        val requestCallback = RequestCallback { request -> request.headers.accept = listOf(MediaType.APPLICATION_OCTET_STREAM) }
        for ((name, url) in newestRelease.assets) {
            val responseExtractor = ResponseExtractor<Void> { response ->
                Files.copy(response.body, Paths.get(name), StandardCopyOption.REPLACE_EXISTING)
                LOGGER.info("Download finished: {}", name)
                updateInstaller.install(name)
                null
            }
            LOGGER.info("Starting download: {}", name)
            restTemplate.execute(URI.create(url), HttpMethod.GET, requestCallback, responseExtractor)
        }
    }

    companion object {
        private val LOGGER = DmsLogger.getLogger(UpdateDownloader::class.java)
    }
}