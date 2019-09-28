package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.data.entity.DocHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocHistoryRepository extends JpaRepository<DocHistory, Long> {

    List<DocHistory> findByDoc(Doc doc);

}
