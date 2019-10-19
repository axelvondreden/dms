package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.docs.TextBlock;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;

public class DocTextDialog extends Dialog {

    private final Element container;

    public DocTextDialog() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        container = ElementFactory.createDiv();
        container.getStyle().set("width", "100%").set("height", "100%").set("position", "relative").set("maxWidth", "100%").set("maxHeight", "100%");
        verticalLayout.getElement().appendChild(container);
        add(verticalLayout);
    }

    public void open(Iterable<TextBlock> textBlocks) {
        container.removeAllChildren();

        boolean first = true;
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
        open();
    }

    private void setSize(float pageWidth, float pagHeight) {
        setWidth("60vw");
        setHeight(((pagHeight / pageWidth) * 100.0F) * 0.6F + "vh");
    }
}