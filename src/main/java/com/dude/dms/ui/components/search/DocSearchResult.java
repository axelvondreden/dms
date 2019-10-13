package com.dude.dms.ui.components.search;

import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.ui.components.tags.TagContainer;
import com.dude.dms.ui.converters.LocalDateConverter;
import com.dude.dms.ui.views.DocView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
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

        VerticalLayout verticalLayout = new VerticalLayout(tagContainer, getTextSnippet());
        verticalLayout.setSizeFull();
        return verticalLayout;
    }

    private Html getTextSnippet() {
        String raw = doc.getRawText();
        int index = raw.indexOf(search);
        int length = search.length();
        int start = Math.max(0, index - 40);
        int end = Math.min(raw.length(), index + length + 40);
        String before = raw.substring(start, index);
        String after = raw.substring(index + length, end);
        return new Html("<div style=\"width: 100%;\">..." + before + "<mark>" + search + "</mark>" + after + "...</div>");
    }

    @Override
    public void onClick() {
        UI.getCurrent().navigate(DocView.class, String.valueOf(doc.getId()));
    }
}