package com.dude.dms.changelog

import java.time.LocalDateTime

data class Changelog(val published: LocalDateTime, val body: String, val tag: String)
