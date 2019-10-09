package com.dude.dms.ui.components.search;

import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.ui.components.tags.TagContainer;
import com.dude.dms.ui.converters.LocalDateConverter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DocSearchResult extends SearchResult {

    private final Doc doc;
    private final String search;

    public DocSearchResult(Doc doc, String search) {
        this.doc = doc;
        this.search = search;
    }

    @Override
    public String getHeader() {
        return "Document " + LocalDateConverter.convert(doc.getDocumentDate());
    }

    @Override
    public Component getBody() {
        TagContainer tagContainer = new TagContainer(doc.getTags());

        String raw = doc.getRawText();
        int index = raw.indexOf(search);
        int start = Math.max(0, index - 40);
        int end = Math.min(raw.length(), index + 40);
        Div div = new Div();
        div.setSizeFull();
        div.setText("..." + raw.substring(start, end) + "...");
        VerticalLayout verticalLayout = new VerticalLayout(tagContainer, div);
        verticalLayout.setSizeFull();
        return verticalLayout;
    }

    @Override
    public void onClick() {

    }
}