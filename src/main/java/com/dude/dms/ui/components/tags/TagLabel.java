package com.dude.dms.ui.components.tags;

import com.dude.dms.backend.data.tags.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;

public class TagLabel extends Div {

    public TagLabel(Tag tag) {
        getElement().getStyle()
                .set("display", "inline")
                .set("border", "2px solid #CCF")
                .set("backgroundColor", tag.getColor())
                .set("borderRadius", "30px")
                .set("padding", "2px 5px");
        Element span = ElementFactory.createSpan(tag.getName());
        getElement().appendChild(span);
        // TODO: save colors consistently as hex, then ctx can be removed
        span.executeJs("" +
                "var ctx = document.createElement('canvas').getContext('2d');" +
                "ctx.fillStyle = '" + tag.getColor() + "';" +
                "var color = ctx.fillStyle;" +
                "if (color.indexOf('#') === 0) { color = color.slice(1); }" +
                "var r = parseInt(color.slice(0, 2), 16), g = parseInt(color.slice(2, 4), 16), b = parseInt(color.slice(4, 6), 16);" +
                "this.style.color = (r * 0.299 + g * 0.487 + b * 0.314) > 156 ? '#000000' : '#FFFFFF';"
        );
    }
}