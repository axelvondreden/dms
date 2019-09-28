package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
public class Person extends DataEntity implements Diffable<Person>, Historical<PersonHistory>, Tagged {

    @NotBlank
    @Size(max = 255)
    private String firstName;

    @NotBlank
    @Size(max = 255)
    private String lastName;

    private LocalDate dateOfBirth;

    @OneToOne(mappedBy = "person")
    private User user;

    @ManyToMany
    private Set<User> users;

    @ManyToMany
    private Set<Tag> tags;

    @OneToMany(mappedBy = "person")
    private List<PersonHistory> history;

    public Person() {

    }

    public Person(String firstName, String lastName, LocalDate dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public List<PersonHistory> getHistory() {
        return history;
    }

    public void setHistory(List<PersonHistory> history) {
        this.history = history;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public Set<Tag> getTags() {
        return tags;
    }

    @Override
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
