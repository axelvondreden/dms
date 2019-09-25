package com.dude.dms.backend.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Person extends DataEntity {

    @Id
    @GeneratedValue
    private Long person_id;

    @NotBlank
    @Size(max = 255)
    private String first_name;

    @NotBlank
    @Size(max = 255)
    private String last_name;

    private LocalDateTime dateOfBirth;

    @OneToMany(mappedBy = "person")
    private List<PersonHistory> history;

    public Long getPerson_id() {
        return person_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public Long getId() {
        return person_id;
    }

    public List<PersonHistory> getHistory() {
        return history;
    }

    public void setHistory(List<PersonHistory> history) {
        this.history = history;
    }
}
