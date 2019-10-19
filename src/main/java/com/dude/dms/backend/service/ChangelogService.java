package com.dude.dms.backend.service;

import com.dude.dms.backend.data.updater.Changelog;
import com.dude.dms.backend.repositories.ChangelogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChangelogService extends CrudService<Changelog> {

    private final ChangelogRepository changelogRepository;

    @Autowired
    public ChangelogService(ChangelogRepository changelogRepository) {
        this.changelogRepository = changelogRepository;
    }

    @Override
    public JpaRepository<Changelog, Long> getRepository() {
        return changelogRepository;
    }

    public Optional<Changelog> findByVersion(String version) {
        return changelogRepository.findByVersion(version);
    }
}