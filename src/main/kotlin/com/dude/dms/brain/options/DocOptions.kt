package com.dude.dms.brain.options

data class DocOptions(
    var pollingPath: String,
    var savePath: String,
    var imageParserDpi: Double,
    var ocrLanguage: String
)
