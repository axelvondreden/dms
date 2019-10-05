package com.dude.dms.ui.components.standard;

import com.github.juchar.colorpicker.ColorPickerFieldRaw;

public class DmsColorPicker extends ColorPickerFieldRaw {

    @Override
    public void setValue(String color) {
        super.setValue(color == null ? "" : color);
    }

    @Override
    public void setPreviousValue(String color) {
        super.setPreviousValue(color == null ? "" : color);
    }

}
