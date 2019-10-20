package com.dude.dms.ui.components.standard;

import com.dude.dms.ui.Const;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class DmsColorPickerSimple extends ComboBox<String> {

    public DmsColorPickerSimple(String label) {
        super(label);
        setItems(Const.SIMPLE_COLORS);
        setPreventInvalidInput(true);
        setAllowCustomValue(false);
        setRenderer(new ComponentRenderer<>(item -> {
            Div d = new Div(new Label(item));
            d.setSizeFull();
            d.getElement().getStyle().set("backgroundColor", item);
            return d;
        }));
    }
}