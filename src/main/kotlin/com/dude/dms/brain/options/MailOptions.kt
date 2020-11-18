package com.dude.dms.brain.options

data class MailOptions(
        var host: String,
        var port: Int,
        var login: String,
        var password: String,
        var pollingInterval: Int
)
