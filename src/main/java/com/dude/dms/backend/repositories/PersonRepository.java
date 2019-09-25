package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
