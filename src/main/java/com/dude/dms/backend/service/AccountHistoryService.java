package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.AccountHistory;
import com.dude.dms.backend.repositories.AccountHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountHistoryService implements CrudService<AccountHistory> {

    private final AccountHistoryRepository accountHistoryRepository;

    @Autowired
    public AccountHistoryService(AccountHistoryRepository accountHistoryRepository) {
        this.accountHistoryRepository = accountHistoryRepository;
    }

    @Override
    public AccountHistoryRepository getRepository() {
        return accountHistoryRepository;
    }
}
