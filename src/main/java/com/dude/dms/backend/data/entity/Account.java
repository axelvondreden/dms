package com.dude.dms.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity(name = "accounts")
public class Account extends DataEntity {

    //@Type(type = "org.hibernate.type.NumericBooleanType")

    @Id
    @GeneratedValue
    private Long account_id;

    @NotBlank
    @Size(max = 50)
    private String name;

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
}
