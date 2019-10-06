package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.rules.PlainTextRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlainTextRuleRepository extends JpaRepository<PlainTextRule, Long> {

    List<PlainTextRule> findByActive(boolean active);
}