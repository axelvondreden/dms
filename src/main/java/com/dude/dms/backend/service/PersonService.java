package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Person;
import com.dude.dms.backend.data.entity.PersonHistory;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService extends HistoricalCrudService<Person, PersonHistory> {

    private final PersonRepository personRepository;

    private final PersonHistoryService personHistoryService;

    @Autowired
    public PersonService(PersonRepository personRepository, PersonHistoryService personHistoryService) {
        this.personRepository = personRepository;
        this.personHistoryService = personHistoryService;
    }

    @Override
    public JpaRepository<Person, Long> getRepository() {
        return personRepository;
    }

    @Override
    public PersonHistory createHistory(Person entity, User currentUser, String text, boolean created, boolean edited, boolean deleted) {
        return new PersonHistory(entity, currentUser, text, created, edited, deleted);
    }

    /**
     * Special method for creating a Person from the register-view, without being logged in
     */
    public Person create(Person entity, User createdBy) {
        Person person = personRepository.saveAndFlush(entity);
        personHistoryService.create(new PersonHistory(person, createdBy, "Created", true, false, false));
        return person;
    }
}
