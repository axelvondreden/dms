package com.dude.dms.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Account extends DataEntity {

    @NotBlank
    @Size(max = 50)
    private String name;

    @OneToMany(mappedBy = "account")
    private List<AccountHistory> history;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AccountHistory> getHistory() {
        return history;
    }

    public void setHistory(List<AccountHistory> history) {
        this.history = history;
    }
}
