package com.dude.dms.spring.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity(name = "users")
public class User extends DataEntity {

  @Id
  @GeneratedValue
  private Long user_id;

  @NotBlank
  @Size(max = 255)
  private String name;

  public Long getUser_id() {
    return user_id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
