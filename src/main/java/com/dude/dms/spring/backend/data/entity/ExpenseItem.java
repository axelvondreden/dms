package com.dude.dms.spring.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "expense_items")
public class ExpenseItem extends DataEntity {

  @Id
  @GeneratedValue
  private Long expense_item_id;

  @NotNull
  @ManyToOne
  @JoinColumn(/*name = "expense_id", */nullable = false)
  private Expense expense;

  @NotNull
  private Integer amount = 0;

  /*@ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "TRANSACTION_ITEM_TAG", joinColumns = @JoinColumn(name = "transaction_item_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags;*/

  public Long getExpense_item_id() {
    return expense_item_id;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public Expense getExpense() {
    return expense;
  }

  public void setExpense(Expense expense) {
    this.expense = expense;
  }
}
