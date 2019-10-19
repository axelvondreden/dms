package com.dude.dms.backend.service;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.docs.TextBlock;
import com.dude.dms.backend.repositories.TextBlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TextBlockService extends CrudService<TextBlock> {

    private final TextBlockRepository textBlockRepository;

    @Autowired
    public TextBlockService(TextBlockRepository textBlockRepository) {
        this.textBlockRepository = textBlockRepository;
    }

    @Override
    public TextBlockRepository getRepository() {
        return textBlockRepository;
    }

    public List<TextBlock> findByDoc(Doc doc) {
        return textBlockRepository.findByDoc(doc);
    }

    public long countByTextAndDoc(String text, Doc doc) {
        return textBlockRepository.countByTextAndDoc(text, doc);
    }

    public long countByext(String text) {
        return textBlockRepository.countByText(text);
    }

    public List<TextBlock> findByTextAndDoc(String text, Doc doc) {
        return textBlockRepository.findByTextAndDoc(text, doc);
    }

    public List<TextBlock> findByText(String text) {
        return textBlockRepository.findByText(text);
    }
}