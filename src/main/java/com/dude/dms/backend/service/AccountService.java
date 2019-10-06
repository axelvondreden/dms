package com.dude.dms.backend.service;

import com.dude.dms.backend.data.base.Account;
import com.dude.dms.backend.data.history.AccountHistory;
import com.dude.dms.backend.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public AccountHistory createHistory(Account entity, String text, boolean created, boolean edited, boolean deleted) {
        return new AccountHistory(entity, text, created, edited, deleted);
    }

    public Optional<Account> findByName(String name) {
        return accountRepository.findByName(name);
    }
}