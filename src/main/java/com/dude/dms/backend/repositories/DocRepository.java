package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocRepository extends JpaRepository<Doc, Long> {

    Optional<Doc> findByGuid(String guid);

    List<Doc> findByTags(Tag tag);

    long countByTags(Tag tag);

    List<Doc> findTop10ByRawTextContaining(String rawText);

    long countByRawTextContaining(String rawText);

    List<Doc> findTop10ByRawTextContainingIgnoreCase(String rawText);

    long countByRawTextContainingIgnoreCase(String rawText);

    long countByAttributeValues_AttributeEquals(Attribute attribute);
}