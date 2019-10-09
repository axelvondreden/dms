package com.dude.dms.ui.components.search;

import com.vaadin.flow.component.Component;

public abstract class SearchResult {

    public abstract String getHeader();

    public abstract Component getBody();

    public abstract void onClick();
}