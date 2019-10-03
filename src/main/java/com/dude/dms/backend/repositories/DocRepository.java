package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.Doc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocRepository extends JpaRepository<Doc, Long> {

    Page<Doc> findByTitleLikeIgnoreCase(String title, Pageable pageable);

    long countByTitleLikeIgnoreCase(String title);

}