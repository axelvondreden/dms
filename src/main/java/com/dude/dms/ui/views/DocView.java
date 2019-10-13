package com.dude.dms.ui.views;

import com.dude.dms.backend.data.base.Doc;
import com.dude.dms.backend.data.base.Word;
import com.dude.dms.backend.service.DocService;
import com.dude.dms.backend.service.WordService;
import com.dude.dms.ui.Const;
import com.dude.dms.ui.MainView;
import com.dude.dms.ui.components.crud.WordEditDialog;
import com.helger.commons.io.file.FileHelper;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

import static com.dude.dms.backend.brain.OptionKey.DOC_SAVE_PATH;

@Route(value = Const.PAGE_DOC, layout = MainView.class)
@PageTitle("Doc")
public class DocView extends VerticalLayout implements HasUrlParameter<String> {

    private final DocService docService;

    private final WordService wordService;

    private final Element container;

    @Autowired
    public DocView(DocService docService, WordService wordService) {
        this.docService = docService;
        this.wordService = wordService;

        container = ElementFactory.createDiv();
        container.getStyle().set("width", "100%").set("height", "100%").set("position", "relative");
        getElement().appendChild(container);
    }

    private void setImage(Doc doc) {
        container.removeAllChildren();

        File img = new File(DOC_SAVE_PATH.getString() + "/img/" + doc.getGuid() + "_00.png");
        if (img.exists()) {
            Element image = new Element("object");
            image.setAttribute("type", "image/png");
            image.getStyle().set("maxWidth", "100%").set("maxHeight", "100%");

            StreamResource resource = new StreamResource("image.png", () -> FileHelper.getInputStream(img));
            image.setAttribute("data", resource);
            container.appendChild(image);

            List<Word> words = wordService.findByDoc(doc);
            for (Word word : words) {
                Element div = ElementFactory.createDiv();
                div.getStyle()
                        .set("border", "2px solid gray")
                        .set("position", "absolute")
                        .set("top", word.getY() + "%")
                        .set("left", word.getX() + "%")
                        .set("width", word.getWidth() + "%")
                        .set("height", word.getHeight() + "%");
                div.setAttribute("title", word.toString());
                div.setAttribute("id", String.valueOf(word.getId()));
                div.addEventListener("mouseenter", event -> event.getSource().getStyle().set("border", "3px solid black"));
                div.addEventListener("mouseleave", event -> event.getSource().getStyle().set("border", "2px solid gray"));
                div.addEventListener("click", event -> new WordEditDialog(wordService).open(wordService.load(Long.parseLong(event.getSource().getAttribute("id")))));
                container.appendChild(div);
            }
        } else {
            add(new Text("No image found!"));
        }
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        setImage(docService.load(Long.parseLong(parameter)));
    }
}
