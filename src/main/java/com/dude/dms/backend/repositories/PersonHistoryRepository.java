package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.PersonHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonHistoryRepository extends JpaRepository<PersonHistory, Long> {

}
