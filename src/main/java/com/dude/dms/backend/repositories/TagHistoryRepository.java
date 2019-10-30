package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.Tag;
import com.dude.dms.backend.data.history.TagHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagHistoryRepository extends JpaRepository<TagHistory, Long> {

    List<TagHistory> findByTag(Tag tag);
}