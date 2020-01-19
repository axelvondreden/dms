package com.dude.dms.brain.options

data class DocOptions(
        var pollingInterval: Int,
        var pollingPath: String,
        var savePath: String,
        var imageParserDpi: Double,
        var demoDocs: Int
)
