package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.docs.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {

}