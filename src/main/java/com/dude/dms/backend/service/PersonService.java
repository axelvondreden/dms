package com.dude.dms.backend.service;

import com.dude.dms.app.security.SecurityUtils;
import com.dude.dms.backend.data.entity.Person;
import com.dude.dms.backend.data.entity.PersonHistory;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService implements CrudService<Person> {

    private final PersonRepository personRepository;

    private final PersonHistoryService personHistoryService;

    private final UserService userService;

    @Autowired
    public PersonService(PersonRepository personRepository, PersonHistoryService personHistoryService, UserService userService) {
        this.personRepository = personRepository;
        this.personHistoryService = personHistoryService;
        this.userService = userService;
    }

    @Override
    public JpaRepository<Person, Long> getRepository() {
        return personRepository;
    }

    @Override
    public Person create(Person entity) {
        Person person = CrudService.super.create(entity);
        User currentUser = userService.findByLogin(SecurityUtils.getUsername()).orElseThrow(() -> new RuntimeException("No User!"));
        personHistoryService.create(new PersonHistory(person, currentUser, "Created", true, false, false));
        return person;
    }

    /**
     * Special method for creating a Person from the register-view, without being logged in
     */
    public Person create(Person entity, User createdBy) {
        Person person = CrudService.super.create(entity);
        personHistoryService.create(new PersonHistory(person, createdBy, "Created", true, false, false));
        return person;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }
}
