package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class AccountHistory extends History {

    @Id
    @GeneratedValue
    private Long account_history_id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public AccountHistory() {

    }

    public AccountHistory(Account account, User historyUser, String text, boolean created, boolean edited, boolean deleted) {
        super(historyUser, text, created, edited, deleted);
        this.account = account;
    }

    public Long getAccount_history_id() {
        return account_history_id;
    }

    @Override
    public Long getId() {
        return account_history_id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
