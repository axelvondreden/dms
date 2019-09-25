package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class PersonHistory extends History {

    @Id
    @GeneratedValue
    private Long person_history_id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public PersonHistory() {

    }

    public PersonHistory(Person person, User historyUser, String text, boolean created, boolean edited, boolean deleted) {
        super(historyUser, text, created, edited, deleted);
        this.person = person;
    }

    public Long getPerson_history_id() {
        return person_history_id;
    }

    @Override
    public Long getId() {
        return person_history_id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
