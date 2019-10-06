package com.dude.dms.api;

import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/")
    public List<Tag> getAllTags() {
        return tagService.findAll();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Tag> getTagByName(@PathVariable("name") String name) {
        Optional<Tag> tag = tagService.findByName(name);
        return tag.isPresent() ? ResponseEntity.ok().body(tag.get()) : ResponseEntity.notFound().build();
    }

}
