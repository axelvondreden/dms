package com.dude.dms.backend.service;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.repositories.PlainTextRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlainTextRuleService extends CrudService<PlainTextRule> implements RuleService<PlainTextRule> {

    private final PlainTextRuleRepository plainTextRuleRepository;

    @Autowired
    public PlainTextRuleService(PlainTextRuleRepository plainTextRuleRepository) {
        this.plainTextRuleRepository = plainTextRuleRepository;
    }

    @Override
    public PlainTextRuleRepository getRepository() {
        return plainTextRuleRepository;
    }

    @Override
    public List<PlainTextRule> getActiveRules() {
        return plainTextRuleRepository.findByActive(true);
    }

    public Optional<PlainTextRule> findById(Long id) {
        return plainTextRuleRepository.findById(id);
    }
}