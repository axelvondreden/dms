package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.TagHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagHistoryRepository extends JpaRepository<TagHistory, Long> {

}
