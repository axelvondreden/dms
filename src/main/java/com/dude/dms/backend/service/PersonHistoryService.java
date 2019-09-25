package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.PersonHistory;
import com.dude.dms.backend.data.entity.UserHistory;
import com.dude.dms.backend.repositories.PersonHistoryRepository;
import com.dude.dms.backend.repositories.UserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonHistoryService implements CrudService<PersonHistory> {

    private final PersonHistoryRepository personHistoryRepository;

    @Autowired
    public PersonHistoryService(PersonHistoryRepository personHistoryRepository) {
        this.personHistoryRepository = personHistoryRepository;
    }

    @Override
    public PersonHistoryRepository getRepository() {
        return personHistoryRepository;
    }

    @Override
    public PersonHistory save(PersonHistory entity) {
        return personHistoryRepository.saveAndFlush(entity);
    }
}
