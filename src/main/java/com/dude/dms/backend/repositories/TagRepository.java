package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.data.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    Set<Tag> findByDocs(Doc doc);
}
