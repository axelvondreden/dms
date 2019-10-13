package com.dude.dms.backend.service;

import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.backend.data.base.Word;
import com.dude.dms.backend.repositories.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Word> findByDoc(Doc doc) {
        return wordRepository.findByDoc(doc);
    }

    public long countByTextAndDoc(String text, Doc doc) {
        return wordRepository.countByTextAndDoc(text, doc);
    }

    public long countByext(String text) {
        return wordRepository.countByText(text);
    }

    public List<Word> findByTextAndDoc(String text, Doc doc) {
        return wordRepository.findByTextAndDoc(text, doc);
    }

    public List<Word> findByText(String text) {
        return wordRepository.findByText(text);
    }
}