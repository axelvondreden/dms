package com.dude.dms.api;

import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/docs")
public class DocController {

    @Autowired
    private DocService docService;

    @GetMapping("/")
    public List<Doc> getAllDocs() {
        return docService.findAll();
    }

    @GetMapping("/{guid}")
    public ResponseEntity<Doc> getDocByGuid(@PathVariable("guid") String guid) {
        Optional<Doc> doc = docService.findByGuid(guid);
        return doc.isPresent() ? ResponseEntity.ok().body(doc.get()) : ResponseEntity.notFound().build();
    }

}
