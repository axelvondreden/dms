package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Entity
public class User extends DataEntity implements Diffable<User>, Historical<UserHistory> {

    @NotBlank
    @Size(max = 255)
    protected String login;

    @NotNull
    @Size(min = 4, max = 255)
    protected String passwordHash;

    @NotBlank
    @Size(max = 255)
    protected String role;

    @OneToOne
    protected Person person;

    @ManyToMany(mappedBy = "users")
    private List<Account> accounts;

    @OneToMany(mappedBy = "user")
    private List<Doc> docs;

    @ManyToMany(mappedBy = "users")
    private Set<Person> persons;

    @OneToMany(mappedBy = "user")
    @OrderBy("timestamp")
    private List<UserHistory> history;

    public User() {

    }

    public User(String login, String passwordHash, String role) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public List<UserHistory> getHistory() {
        return history;
    }

    public void setHistory(List<UserHistory> history) {
        this.history = history;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Doc> getDocs() {
        return docs;
    }

    public void setDocs(List<Doc> docs) {
        this.docs = docs;
    }

    public Set<Person> getPersons() {
        return persons;
    }

    public void setPersons(Set<Person> persons) {
        this.persons = persons;
    }
}
