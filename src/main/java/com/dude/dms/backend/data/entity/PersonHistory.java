package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class PersonHistory extends History {

    @NotNull
    @ManyToOne
    private Person person;

    public PersonHistory() {

    }

    public PersonHistory(Person person, String text, boolean created, boolean edited, boolean deleted) {
        super(text, created, edited, deleted);
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
