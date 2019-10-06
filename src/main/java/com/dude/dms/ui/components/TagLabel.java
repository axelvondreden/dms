package com.dude.dms.ui.components;

import com.dude.dms.backend.data.entity.Tag;
import com.vaadin.flow.component.html.Div;

public class TagLabel extends Div {

    public TagLabel(Tag tag) {
        getElement().getStyle().set("display", "inlineBlock").set("border", "1px solid black").set("backgroundColor", tag.getColor());
        setText(tag.getName());
    }
}