package com.dude.dms.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Account extends DataEntity {

    @Id
    @GeneratedValue
    private Long accountId;

    @NotBlank
    @Size(max = 50)
    private String name;

    @OneToMany(mappedBy = "account")
    private List<AccountHistory> history;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return accountId;
    }

    public List<AccountHistory> getHistory() {
        return history;
    }

    public void setHistory(List<AccountHistory> history) {
        this.history = history;
    }
}
