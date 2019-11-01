package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.Changelog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChangelogRepository extends JpaRepository<Changelog, Long> {

    Optional<Changelog> findByVersion(String version);
}