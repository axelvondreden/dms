package com.dude.dms.ui.components.standard

import com.dude.dms.ui.Const
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.dom.ElementFactory

class DmsColorPickerSimple(label: String?) : ComboBox<String>(label) {

    init {
        setItems(*Const.SIMPLE_COLORS)
        isPreventInvalidInput = true
        isAllowCustomValue = false
        setRenderer(ComponentRenderer { item: String ->
            val span = ElementFactory.createSpan(item)
            val d = Div().apply {
                setSizeFull()
                element.style["display"] = "inline"
                element.style["border"] = "2px solid #CCF"
                element.style["backgroundColor"] = item
                element.style["borderRadius"] = "30px"
                element.style["padding"] = "2px 5px"
                element.appendChild(span)
            }
            // TODO: save colors consistently as hex, then ctx can be removed
            span.executeJs("" +
                    "var ctx = document.createElement('canvas').getContext('2d');" +
                    "ctx.fillStyle = '$item';" +
                    "var color = ctx.fillStyle;" +
                    "if (color.indexOf('#') === 0) { color = color.slice(1); }" +
                    "var r = parseInt(color.slice(0, 2), 16), g = parseInt(color.slice(2, 4), 16), b = parseInt(color.slice(4, 6), 16);" +
                    "this.style.color = (r * 0.299 + g * 0.487 + b * 0.314) > 156 ? '#000000' : '#FFFFFF';"
            )
            d
        })
    }
}