package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.rules.RegexRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegexRuleRepository extends JpaRepository<RegexRule, Long> {

    List<RegexRule> findByActive(boolean active);
}