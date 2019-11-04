package com.dude.dms.ui.builder.misc;

import com.dude.dms.backend.service.ChangelogService;
import com.dude.dms.ui.components.dialogs.ChangelogDialog;
import com.dude.dms.updater.UpdateChecker;

public final class ChangelogDialogBuilder {

    private final ChangelogService changelogService;
    private final UpdateChecker updateChecker;

    ChangelogDialogBuilder(ChangelogService changelogService, UpdateChecker updateChecker) {
        this.changelogService = changelogService;
        this.updateChecker = updateChecker;
    }

    public ChangelogDialog build() {
        return new ChangelogDialog(changelogService, updateChecker);
    }
}
