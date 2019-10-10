package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.base.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {

}