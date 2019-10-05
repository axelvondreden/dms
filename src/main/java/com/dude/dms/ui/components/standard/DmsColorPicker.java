package com.dude.dms.ui.components.standard;

import com.github.juchar.colorpicker.ColorPickerFieldRaw;

public class DmsColorPicker extends ColorPickerFieldRaw {

    @Override
    public void setValue(String color) {
        if (color == null) {
            color = "";
        }
        super.setValue(color);
    }

    @Override
    public void setPreviousValue(String color) {
        if (color == null) {
            color = "";
        }
        super.setPreviousValue(color);
    }

}
