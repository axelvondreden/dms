package com.dude.dms.api;

import com.dude.dms.backend.data.entity.Account;
import com.dude.dms.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public List<Account> getAllAccounts() {
        return accountService.findAll();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Account> getAccountByName(@PathVariable("name") String name) {
        Optional<Account> account = accountService.findByName(name);
        return account.isPresent() ? ResponseEntity.ok().body(account.get()) : ResponseEntity.notFound().build();
    }

}
