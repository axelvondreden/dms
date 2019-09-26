package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.DocHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocHistoryRepository extends JpaRepository<DocHistory, Long> {

}
