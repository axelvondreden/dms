package com.dude.dms.ui.components.tags;

import com.dude.dms.backend.data.tags.Tag;
import com.vaadin.flow.component.html.Div;

public class TagLabel extends Div {

    public TagLabel(Tag tag) {
        getElement().getStyle()
                .set("display", "inline")
                .set("border", "2px solid #CCF")
                .set("backgroundColor", tag.getColor())
                .set("borderRadius", "30px")
                .set("padding", "2px 5px");
        setText(tag.getName());
    }
}