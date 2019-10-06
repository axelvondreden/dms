package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.Doc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocRepository extends JpaRepository<Doc, Long> {

    Optional<Doc> findByGuid(String guid);
}