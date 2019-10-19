package com.dude.dms.updater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

@Component
public class UpdateDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateDownloader.class);

    @Autowired
    private UpdateInstaller updateInstaller;

    public void download(Release newestRelease, RestTemplate restTemplate) {
        File old = new File("update");
        if (old.exists() && old.isDirectory()) {
            old.delete();
        }
        RequestCallback requestCallback = request -> request.getHeaders().setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        for (Asset asset : newestRelease.getAssets()) {
            ResponseExtractor<Void> responseExtractor = response -> {
                Files.copy(response.getBody(), Paths.get(asset.getName()), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Download finished: {}", asset.getName());
                updateInstaller.install(asset.getName());
                return null;
            };
            LOGGER.info("Starting download: {}", asset.getName());
            restTemplate.execute(URI.create(asset.getUrl()), HttpMethod.GET, requestCallback, responseExtractor);
        }
    }
}