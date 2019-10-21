package com.dude.dms.ui.components.dialogs;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.docs.TextBlock;
import com.dude.dms.backend.service.TextBlockService;
import com.dude.dms.ui.components.dialogs.crud.TextBlockEditDialog;
import com.helger.commons.io.file.FileHelper;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.util.List;

import static com.dude.dms.backend.brain.OptionKey.DOC_SAVE_PATH;

public class DocImageDialog extends Dialog {

    private final TextBlockService textBlockService;

    private final Element container;

    public DocImageDialog(TextBlockService textBlockService) {
        this.textBlockService = textBlockService;

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMaxWidth("80vw");
        verticalLayout.setMaxHeight("80vh");

        container = ElementFactory.createDiv();
        container.getStyle().set("width", "100%").set("height", "100%").set("position", "relative").set("maxWidth", "100%").set("maxHeight", "100%");
        verticalLayout.getElement().appendChild(container);
        add(verticalLayout);
    }

    public void open(Doc doc) {
        container.removeAllChildren();

        File img = new File(DOC_SAVE_PATH.getString() + "/img/" + doc.getGuid() + "_00.png");
        if (img.exists()) {
            Element image = new Element("object");
            image.setAttribute("type", "image/png");
            image.getStyle().set("maxWidth", "100%").set("maxHeight", "100%");

            StreamResource resource = new StreamResource("image.png", () -> FileHelper.getInputStream(img));
            image.setAttribute("data", resource);
            container.appendChild(image);

            List<TextBlock> textBlocks = textBlockService.findByDoc(doc);
            for (TextBlock textBlock : textBlocks) {
                Element div = ElementFactory.createDiv();
                div.getStyle()
                        .set("border", "2px solid gray")
                        .set("position", "absolute")
                        .set("top", textBlock.getY() + "%")
                        .set("left", textBlock.getX() + "%")
                        .set("width", textBlock.getWidth() + "%")
                        .set("height", textBlock.getHeight() + "%");
                div.setAttribute("title", textBlock.toString());
                div.setAttribute("id", String.valueOf(textBlock.getId()));
                div.addEventListener("mouseenter", event -> event.getSource().getStyle().set("border", "3px solid black"));
                div.addEventListener("mouseleave", event -> event.getSource().getStyle().set("border", "2px solid gray"));
                div.addEventListener("click", event -> new TextBlockEditDialog(textBlockService).open(textBlockService.load(Long.parseLong(event.getSource().getAttribute("id")))));
                container.appendChild(div);
            }
        } else {
            add(new Text("No image found!"));
        }
        open();
    }
}