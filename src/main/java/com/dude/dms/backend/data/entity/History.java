package com.dude.dms.backend.data.entity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class History extends DataEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "historyUserId")
    protected User historyUser;

    @NotBlank
    @Size(max = 255)
    protected String text;

    @NotNull
    protected LocalDateTime timestamp;

    protected Boolean created;

    protected Boolean edited;

    protected Boolean deleted;

    public History() {
    }

    public History(User historyUser, String text, Boolean created, Boolean edited, Boolean deleted) {
        this.historyUser = historyUser;
        this.text = text;
        this.created = created;
        this.edited = edited;
        this.deleted = deleted;
        timestamp = LocalDateTime.now();
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

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }

    public Boolean getEdited() {
        return edited;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

  public User getHistoryUser() {
    return historyUser;
  }

  public void setHistoryUser(User historyUser) {
    this.historyUser = historyUser;
  }
}
