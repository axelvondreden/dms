package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.Account;
import com.dude.dms.backend.data.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {

    List<AccountHistory> findByAccount(Account account);

}
