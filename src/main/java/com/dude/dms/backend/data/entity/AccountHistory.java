package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class AccountHistory extends History {

    @NotNull
    @ManyToOne
    private Account account;

    public AccountHistory() {

    }

    public AccountHistory(Account account, String text, boolean created, boolean edited, boolean deleted) {
        super(text, created, edited, deleted);
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
