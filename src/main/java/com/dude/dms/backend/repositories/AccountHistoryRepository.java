package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {

}
