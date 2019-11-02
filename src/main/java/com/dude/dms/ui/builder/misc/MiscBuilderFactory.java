package com.dude.dms.ui.builder.misc;

import com.dude.dms.backend.service.ChangelogService;
import com.dude.dms.ui.builder.BuilderFactory;
import com.dude.dms.ui.builder.Factory;
import com.dude.dms.updater.UpdateChecker;

public class MiscBuilderFactory extends Factory {

    private final ChangelogService changelogService;
    private final UpdateChecker updateChecker;

    public MiscBuilderFactory(BuilderFactory builderFactory, ChangelogService changelogService, UpdateChecker updateChecker) {
        super(builderFactory);
        this.changelogService = changelogService;
                this.updateChecker = updateChecker;
    }

    public ChangelogDialogBuilder changelog() {
        return new ChangelogDialogBuilder(changelogService, updateChecker);
    }
}