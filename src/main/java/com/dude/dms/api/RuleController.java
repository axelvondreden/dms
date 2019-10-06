package com.dude.dms.api;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rules")
public class RuleController {

    @Autowired
    private PlainTextRuleService plainTextRuleService;

    @GetMapping("/plain/")
    public List<PlainTextRule> getAllPlainTextRules() {
        return plainTextRuleService.findAll();
    }

    @GetMapping("/plain/{id}")
    public ResponseEntity<PlainTextRule> getPlainTextRuleById(@PathVariable("id") Long id) {
        Optional<PlainTextRule> plainTextRule = plainTextRuleService.findById(id);
        return plainTextRule.isPresent() ? ResponseEntity.ok().body(plainTextRule.get()) : ResponseEntity.notFound().build();
    }
}