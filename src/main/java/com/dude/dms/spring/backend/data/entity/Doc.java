package com.dude.dms.spring.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity(name = "docs")
public class Doc extends DataEntity {

  @Id
  @GeneratedValue
  private Long doc_id;

  @NotBlank
  @Size(max = 255)
  private String title;

  @NotNull
  private LocalDateTime uploadDate;

  public Long getDoc_id() {
    return doc_id;
  }

  public void setDoc_id(Long doc_id) {
    this.doc_id = doc_id;
  }
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public LocalDateTime getUploadDate() {
    return uploadDate;
  }

  public void setUploadDate(LocalDateTime uploadDate) {
    this.uploadDate = uploadDate;
  }
}
