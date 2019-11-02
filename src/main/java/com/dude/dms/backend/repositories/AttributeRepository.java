package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.docs.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    Set<Attribute> findByTag(Tag tag);

    Set<Attribute> findByTagNot(Tag tag);

    Optional<Attribute> findByName(String name);
}