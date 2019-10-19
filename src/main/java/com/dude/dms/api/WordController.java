package com.dude.dms.api;

import com.dude.dms.backend.data.docs.Word;
import com.dude.dms.backend.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/words")
public class WordController {

    @Autowired
    private WordService wordService;

    @GetMapping("/")
    public List<Word> getAllWords() {
        return wordService.findAll();
    }
}