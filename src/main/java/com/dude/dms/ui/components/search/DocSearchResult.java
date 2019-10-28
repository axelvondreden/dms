package com.dude.dms.ui.components.search;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.ui.components.dialogs.DocImageDialog;
import com.dude.dms.ui.components.dialogs.DocTextDialog;
import com.dude.dms.ui.components.tags.TagContainer;
import com.dude.dms.ui.converters.LocalDateConverter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DocSearchResult extends SearchResult {

    private final Doc doc;

    private final String search;

    private final DocImageDialog docImageDialog;
    private final DocTextDialog docTextDialog;

    public DocSearchResult(Doc doc, String search, DocImageDialog docImageDialog, DocTextDialog docTextDialog) {
        this.doc = doc;
        this.search = search;
        this.docImageDialog = docImageDialog;
        this.docTextDialog = docTextDialog;
    }

    @Override
    public String getHeader() {
        return "Document " + LocalDateConverter.convert(doc.getDocumentDate());
    }

    @Override
    public Component getBody() {
        TagContainer tagContainer = new TagContainer(doc.getTags());
        Button pdfButton = new Button(VaadinIcon.FILE_TEXT.create(), event -> docImageDialog.open());
        Button textButton = new Button(VaadinIcon.TEXT_LABEL.create(), e -> docTextDialog.open());
        HorizontalLayout buttonWrapper = new HorizontalLayout(pdfButton, textButton);
        buttonWrapper.setWidthFull();
        buttonWrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        VerticalLayout verticalLayout = new VerticalLayout(tagContainer, getTextSnippet(), buttonWrapper);
        verticalLayout.setSizeFull();
        return verticalLayout;
    }

    private Html getTextSnippet() {
        String raw = doc.getRawText();
        int index = raw.indexOf(search);
        if (index < 0) {
            index = raw.toLowerCase().indexOf(search.toLowerCase());
        }
        int length = search.length();
        int start = Math.max(0, index - 40);
        int end = Math.min(raw.length(), index + length + 40);
        String before = raw.substring(start, index);
        String after = raw.substring(index + length, end);
        return new Html("<div style=\"width: 100%;\">..." + before + "<mark>" + search + "</mark>" + after + "...</div>");
    }

    @Override
    public void onClick() {
    }
}