package com.dude.dms.backend.service;

import com.dude.dms.backend.brain.DmsLogger;
import com.dude.dms.backend.data.Changelog;
import com.dude.dms.backend.repositories.ChangelogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChangelogService extends CrudService<Changelog> {

    private static final DmsLogger LOGGER = DmsLogger.getLogger(ChangelogService.class);

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

    @Override
    public Changelog create(Changelog entity) {
        if (findByVersion(entity.getVersion()).isPresent()) {
            LOGGER.warn("Tried to create changelog with same Version");
            return null;
        }
        return super.create(entity);
    }
}