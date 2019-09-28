package com.dude.dms.backend.service;

import com.dude.dms.app.security.SecurityUtils;
import com.dude.dms.backend.data.entity.Account;
import com.dude.dms.backend.data.entity.AccountHistory;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements CrudService<Account> {

    private final AccountRepository accountRepository;

    private final AccountHistoryService accountHistoryService;

    private final UserService userService;

    @Autowired
    public AccountService(AccountRepository accountRepository, AccountHistoryService accountHistoryService, UserService userService) {
        this.accountRepository = accountRepository;
        this.accountHistoryService = accountHistoryService;
        this.userService = userService;
    }

    @Override
    public JpaRepository<Account, Long> getRepository() {
        return accountRepository;
    }

    @Override
    public Account create(Account entity) {
        User currentUser = userService.findByLogin(SecurityUtils.getUsername()).orElseThrow(() -> new RuntimeException("No User!"));
        accountHistoryService.create(new AccountHistory(entity, currentUser, "Created", true, false, false));
        return CrudService.super.create(entity);
    }
}
