package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocRepository extends JpaRepository<Doc, Long> {

    Optional<Doc> findByGuid(String guid);

    List<Doc> findByTags(Tag tag);

    long countByTags(Tag tag);

    long countByRawTextLike(String rawText);

    List<Doc> findTop10ByRawTextLike(String rawText);
}