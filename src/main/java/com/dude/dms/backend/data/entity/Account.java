package com.dude.dms.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Account extends DataEntity implements Diffable<Account>, Historical<AccountHistory> {

    @NotBlank
    @Size(max = 50)
    private String name;

    @OneToMany(mappedBy = "account")
    @OrderBy("timestamp")
    private List<AccountHistory> history;

    @ManyToMany
    private List<User> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<AccountHistory> getHistory() {
        return history;
    }

    public void setHistory(List<AccountHistory> history) {
        this.history = history;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
