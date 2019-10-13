package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.backend.data.base.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findByDoc(Doc doc);

    long countByTextAndDoc(String text, Doc doc);

    long countByText(String text);

    List<Word> findByTextAndDoc(String text, Doc doc);

    List<Word> findByText(String text);
}