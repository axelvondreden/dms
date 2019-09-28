package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.Tag;
import com.dude.dms.backend.data.entity.TagHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagHistoryRepository extends JpaRepository<TagHistory, Long> {

    List<TagHistory> findByTag(Tag tag);

}
