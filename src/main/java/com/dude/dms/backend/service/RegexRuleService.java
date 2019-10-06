package com.dude.dms.backend.service;

import com.dude.dms.backend.data.rules.RegexRule;
import com.dude.dms.backend.repositories.RegexRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegexRuleService extends CrudService<RegexRule> implements RuleService<RegexRule> {

    private final RegexRuleRepository regexRuleRepository;

    @Autowired
    public RegexRuleService(RegexRuleRepository regexRuleRepository) {
        this.regexRuleRepository = regexRuleRepository;
    }

    @Override
    public RegexRuleRepository getRepository() {
        return regexRuleRepository;
    }

    @Override
    public List<RegexRule> getActiveRules() {
        return regexRuleRepository.findByActive(true);
    }

    public Optional<RegexRule> findById(Long id) {
        return regexRuleRepository.findById(id);
    }
}