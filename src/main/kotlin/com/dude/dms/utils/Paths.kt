package com.dude.dms.utils

import java.io.File

val tessdataPath by lazy {
    when {
        File("tessdata").exists() -> "tessdata"
        File("config/tessdata").exists() -> "config/tessdata"
        else -> "../config/tessdata"
    }
}

val optionsDefaultPath by lazy {
    if (File("config").exists()) {
        "config/options.default.json"
    } else {
        "../config/options.default.json"
    }
}
val optionsPath by lazy {
    if (File("config").exists()) "config/options.json" else "../config/options.json"
}