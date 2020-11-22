package com.dude.dms.utils

import java.io.File

val tessdataPath by lazy { if (File("tessdata").exists()) "tessdata" else "../config/tessdata" }

val optionsDefaultPath by lazy {
    if (File("config/options.default.json").exists()) {
        "config/options.default.json"
    } else {
        "../config/options.default.json"
    }
}
val optionsPath by lazy {
    if (File("config/options.json").exists()) "config/options.json" else "../config/options.json"
}