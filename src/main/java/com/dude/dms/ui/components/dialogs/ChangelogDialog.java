package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.updater.Changelog;
import com.dude.dms.backend.service.ChangelogService;
import com.dude.dms.updater.UpdateChecker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.List;
import java.util.stream.Collectors;

public class ChangelogDialog extends Dialog {

    public ChangelogDialog(ChangelogService changelogService, UpdateChecker updateChecker) {
        setWidth("70vw");
        setHeight("70vh");
        Button b = new Button("Check for updates", e -> updateChecker.check(false));
        b.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(b);
        List<Changelog> changelogs = changelogService.findAll().stream().sorted((o1, o2) -> o2.getPublished().compareTo(o1.getPublished())).collect(Collectors.toList());
        for (Changelog changelog : changelogs) {
            TextArea area = new TextArea(changelog.getVersion(), changelog.getBody(), "");
            area.setReadOnly(true);
            area.setWidthFull();
            add(area);
        }
    }
}