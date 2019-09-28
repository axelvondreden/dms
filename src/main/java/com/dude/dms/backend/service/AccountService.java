package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Account;
import com.dude.dms.backend.data.entity.AccountHistory;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService extends HistoricalCrudService<Account, AccountHistory> {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public JpaRepository<Account, Long> getRepository() {
        return accountRepository;
    }

    @Override
    public AccountHistory createHistory(Account entity, User currentUser, String text, boolean created, boolean edited, boolean deleted) {
        return new AccountHistory(entity, currentUser, text, created, edited, deleted);
    }
}
