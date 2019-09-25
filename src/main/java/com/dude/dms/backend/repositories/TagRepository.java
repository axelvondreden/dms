package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
