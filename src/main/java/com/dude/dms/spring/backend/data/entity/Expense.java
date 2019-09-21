package com.dude.dms.spring.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity(name = "expenses")
public class Expense extends DataEntity {

  @Id
  @GeneratedValue
  private Long expense_id;

  @NotNull
  private LocalDateTime timestamp;

  @NotNull
  @OneToOne
  //@JoinColumn(name = "account_id")
  private Account account;

  @NotNull
  @OneToOne
  //@JoinColumn(name = "user_id")
  private User user;

  @NotNull
  private Integer amount = 0;

  public Long getExpense_id() {
    return expense_id;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }
}
