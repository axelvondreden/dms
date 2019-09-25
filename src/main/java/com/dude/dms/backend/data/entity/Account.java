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
    private Long account_id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @OneToMany(mappedBy = "account")
    private List<AccountHistory> history;

    public Long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Long account_id) {
        this.account_id = account_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return account_id;
    }

    public List<AccountHistory> getHistory() {
        return history;
    }

    public void setHistory(List<AccountHistory> history) {
        this.history = history;
    }
}
