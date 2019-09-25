package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

}
