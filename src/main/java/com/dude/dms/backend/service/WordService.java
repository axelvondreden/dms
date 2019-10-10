package com.dude.dms.backend.service;

import com.dude.dms.backend.data.base.Word;
import com.dude.dms.backend.repositories.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordService extends CrudService<Word> {

    private final WordRepository wordRepository;

    @Autowired
    public WordService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public WordRepository getRepository() {
        return wordRepository;
    }
}