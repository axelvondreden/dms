package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class PersonHistory extends History {

    @Id
    @GeneratedValue
    private Long personHistoryId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "personId")
    private Person person;

    public PersonHistory() {

    }

    public PersonHistory(Person person, User historyUser, String text, boolean created, boolean edited, boolean deleted) {
        super(historyUser, text, created, edited, deleted);
        this.person = person;
    }

    public Long getPersonHistoryId() {
        return personHistoryId;
    }

    @Override
    public Long getId() {
        return personHistoryId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
