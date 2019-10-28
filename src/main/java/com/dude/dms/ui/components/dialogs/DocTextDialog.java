package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.docs.TextBlock;
import com.dude.dms.backend.service.TextBlockService;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;

import java.util.List;

public class DocTextDialog extends Dialog {

    public DocTextDialog(Doc doc, TextBlockService textBlockService) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        Element container = ElementFactory.createDiv();
        container.getStyle().set("width", "100%").set("height", "100%").set("position", "relative").set("maxWidth", "100%").set("maxHeight", "100%");
        verticalLayout.getElement().appendChild(container);
        add(verticalLayout);

        boolean first = true;
        List<TextBlock> textBlocks = textBlockService.findByDoc(doc);
        for (TextBlock textBlock : textBlocks) {
            if (first) {
                first = false;
                setSize(textBlock.getPageWidth(), textBlock.getPagHeight());
            }
            Element div = ElementFactory.createDiv(textBlock.getText());
            div.getStyle()
                    .set("fontSize", textBlock.getFontSize() / 6.0F + "vmin")
                    .set("textAlign", "center")
                    .set("position", "absolute")
                    .set("top", textBlock.getY() + "%")
                    .set("left", textBlock.getX() + "%")
                    .set("width", textBlock.getWidth() + "%")
                    .set("height", textBlock.getHeight() + "%");
            container.appendChild(div);
        }
    }

    private void setSize(float pageWidth, float pagHeight) {
        setWidth("60vw");
        setHeight(((pagHeight / pageWidth) * 100.0F) * 0.6F + "vh");
    }
}