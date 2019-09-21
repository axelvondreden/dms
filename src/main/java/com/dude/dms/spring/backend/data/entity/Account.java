package com.dude.dms.spring.backend.data.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity(name = "accounts")
public class Account extends DataEntity {

  @Id
  @GeneratedValue
  private Long account_id;

  @NotBlank
  @Size(max = 50)
  private String name;

  @Column(nullable = false)
  @Type(type = "org.hibernate.type.NumericBooleanType")
  private boolean main;

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

  public boolean isMain() {
    return main;
  }

  public void setMain(boolean main) {
    this.main = main;
  }
}
