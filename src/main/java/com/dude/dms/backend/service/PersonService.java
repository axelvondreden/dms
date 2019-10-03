package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Person;
import com.dude.dms.backend.data.entity.PersonHistory;
import com.dude.dms.backend.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService extends HistoricalCrudService<Person, PersonHistory> {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository, PersonHistoryService personHistoryService) {
        this.personRepository = personRepository;
    }

    @Override
    public JpaRepository<Person, Long> getRepository() {
        return personRepository;
    }

    @Override
    public PersonHistory createHistory(Person entity, String text, boolean created, boolean edited, boolean deleted) {
        return new PersonHistory(entity, text, created, edited, deleted);
    }
}