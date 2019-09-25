package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Person;
import com.dude.dms.backend.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService implements CrudService<Person> {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public JpaRepository<Person, Long> getRepository() {
        return personRepository;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }
}
