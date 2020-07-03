package com.dude.dms.ui.components.tags

import com.dude.dms.backend.data.Tag
import com.github.mvysny.karibudsl.v10.label
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.dom.ElementFactory

class TagLabel(val tag: Tag) : Div() {

    var onClick: ((ComponentEvent<*>) -> Unit)? = null

    init {
        label(tag.name) {
            addClassName("tag-label")
            style["backgroundColor"] = tag.color
            // TODO: save colors consistently as hex, then ctx can be removed
            val span = ElementFactory.createSpan(tag.name).apply { style["display"] = "none" }
            element.appendChild(span)
            span.executeJs("" +
                    "var ctx = document.createElement('canvas').getContext('2d');" +
                    "ctx.fillStyle = '${tag.color}';" +
                    "var color = ctx.fillStyle;" +
                    "if (color.indexOf('#') === 0) { color = color.slice(1); }" +
                    "var r = parseInt(color.slice(0, 2), 16), g = parseInt(color.slice(2, 4), 16), b = parseInt(color.slice(4, 6), 16);" +
                    "return (r * 0.299 + g * 0.487 + b * 0.314) > 156 ? '#000000' : '#FFFFFF';"
            ).then {
                element.setProperty("style", "color: ${it.asString()}")
            }
        }

        if (onClick != null) addClickListener(onClick)
    }
}