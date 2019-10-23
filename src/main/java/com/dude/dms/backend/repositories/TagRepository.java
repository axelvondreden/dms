package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.data.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    Set<Tag> findByPlainTextRules(PlainTextRule rule);

    Set<Tag> findByRegexRules(RegexRule rule);

    List<Tag> findTop10ByNameContaining(String name);

    long countByNameContaining(String name);

    List<Tag> findTop10ByNameContainingIgnoreCase(String name);

    long countByNameContainingIgnoreCase(String name);
}