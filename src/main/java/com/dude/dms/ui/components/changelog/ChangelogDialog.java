package com.dude.dms.ui.components.changelog;

import com.dude.dms.backend.data.updater.Changelog;
import com.dude.dms.backend.service.ChangelogService;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.List;

public class ChangelogDialog extends Dialog {

    public ChangelogDialog(ChangelogService changelogService) {
        setWidth("80vw");
        setHeight("80vh");
        setSizeFull();
        List<Changelog> changelogs = changelogService.findAll();
        for (Changelog changelog : changelogs) {
            TextArea area = new TextArea(changelog.getVersion(), changelog.getBody(), "");
            area.setReadOnly(true);
            area.setSizeFull();
            add(area);
        }
    }
}