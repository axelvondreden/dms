package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.data.rules.RegexRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    Set<Tag> findByPlainTextRules(PlainTextRule rule);

    Set<Tag> findByRegexRules(RegexRule rule);
}