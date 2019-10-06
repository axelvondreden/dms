package com.dude.dms.ui.components.rules;

import com.dude.dms.backend.data.base.Tag;
import com.dude.dms.backend.data.rules.Rule;
import com.dude.dms.backend.service.TagService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RuleTagger<T extends Rule> extends HorizontalLayout {

    private final Set<Tag> ruleTags;
    private final List<Tag> availableTags;
    private final Grid<Tag> ruleGrid;
    private final Grid<Tag> availableGrid;

    public RuleTagger(TagService tagService) {
        ruleTags = new HashSet<>();
        availableTags = tagService.findAll();

        ruleGrid = new Grid<>();
        ruleGrid.setItems(ruleTags);
        ruleGrid.addColumn(Tag::getName).setHeader("Rule Tags");
        ruleGrid.setHeightFull();
        ruleGrid.setWidth("50%");

        availableGrid = new Grid<>();
        availableGrid.setItems(availableTags);
        availableGrid.addColumn(Tag::getName).setHeader("Available Tags");
        availableGrid.setHeightFull();
        availableGrid.setWidth("50%");

        ruleGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        ruleGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(tag -> {
            ruleTags.remove(tag);
            availableTags.add(tag);
            refreshGrids();
        }));

        availableGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        availableGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(tag -> {
            availableTags.remove(tag);
            ruleTags.add(tag);
            refreshGrids();
        }));

        add(ruleGrid, availableGrid);
    }

    public RuleTagger(Collection<Tag> tags, TagService tagService) {
        this(tagService);
        ruleTags.addAll(tags);
        availableTags.removeAll(ruleTags);
        refreshGrids();
    }

    private void refreshGrids() {
        availableGrid.getDataProvider().refreshAll();
        ruleGrid.getDataProvider().refreshAll();
    }

    public boolean validate() {
        return !ruleTags.isEmpty();
    }

    public Set<Tag> getRuleTags() {
        return ruleTags;
    }
}