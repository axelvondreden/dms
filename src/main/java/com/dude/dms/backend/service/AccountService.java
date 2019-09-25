package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Account;
import com.dude.dms.backend.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService implements CrudService<Account> {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public JpaRepository<Account, Long> getRepository() {
        return accountRepository;
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }
}
