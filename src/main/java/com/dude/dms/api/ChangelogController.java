package com.dude.dms.api;

import com.dude.dms.backend.data.Changelog;
import com.dude.dms.backend.service.ChangelogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/changelogs")
public class ChangelogController {

    @Autowired
    private ChangelogService changelogService;

    @GetMapping("/")
    public List<Changelog> getAllChangelogs() {
        return changelogService.findAll();
    }
}
