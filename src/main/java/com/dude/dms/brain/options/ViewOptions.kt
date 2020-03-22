package com.dude.dms.brain.options

data class ViewOptions(
        var dateFormat: String,
        var dateScanFormats: List<String>,
        var locale: String,
        var darkMode: Boolean,
        var notificationPosition: String,
        var docCardSize: Int
)