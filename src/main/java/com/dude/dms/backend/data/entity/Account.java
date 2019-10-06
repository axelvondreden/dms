package com.dude.dms.backend.data.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Account extends DataEntity implements Diffable<Account>, Historical<AccountHistory> {

    @NotBlank
    @Size(max = 50)
    protected String name;

    @OneToMany(mappedBy = "account")
    @OrderBy("timestamp")
    private List<AccountHistory> history;

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
}