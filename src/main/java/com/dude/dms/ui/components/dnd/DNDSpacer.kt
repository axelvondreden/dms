package com.dude.dms.ui.components.dnd

class DNDSpacer(index: Int, onDrop: (Pair<Int, Int>) -> Unit) : DNDItem(index, onDrop) {

    init {
        width = "20px"
        height = "50px"
        style["padding"] = "4px"
    }
}