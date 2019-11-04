package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.docs.Attribute;
import com.dude.dms.backend.data.docs.AttributeValue;
import com.dude.dms.backend.data.docs.Doc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {

    Optional<AttributeValue> findByDocAndAttribute(Doc doc, Attribute attribute);
}