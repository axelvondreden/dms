package com.dude.dms.ui.components.standard

import com.github.juchar.colorpicker.ColorPickerFieldRaw

class DmsColorPicker(label: String?) : ColorPickerFieldRaw(label) {

    fun setErrorMessage(msg: String) {
        textField.errorMessage = msg
    }
}