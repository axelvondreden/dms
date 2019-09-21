package com.dude.dms.spring.backend.data.entity;

import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity(name = "history_items")
public class HistoryItem extends DataEntity {

  @Id
  @GeneratedValue
  private Long history_item_id;

  @NotBlank
  @Size(max = 255)
  private String text;

  @NotNull
  private LocalDateTime timestamp;

  @PersistenceConstructor
  public HistoryItem(String text) {
    this.text = text;
    timestamp = LocalDateTime.now();
  }

  public Long getHistory_item_id() {
    return history_item_id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
