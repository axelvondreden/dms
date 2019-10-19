package com.dude.dms.api;

import com.dude.dms.backend.data.docs.TextBlock;
import com.dude.dms.backend.service.TextBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/textblocks")
public class TextBlockController {

    @Autowired
    private TextBlockService textBlockService;

    @GetMapping("/")
    public List<TextBlock> getAllTextBlocks() {
        return textBlockService.findAll();
    }
}