package com.dude.dms.backend.service;

import com.dude.dms.backend.data.rules.Rule;

import java.util.List;

public interface RuleService<T extends Rule> {

    List<T> getActiveRules();
}