package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.Person;
import com.dude.dms.backend.data.entity.PersonHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonHistoryRepository extends JpaRepository<PersonHistory, Long> {

    List<PersonHistory> findByPerson(Person person);

}
