package com.dude.dms.changelog

import java.time.LocalDateTime

data class Release(var tag_name: String, var published_at: LocalDateTime, var body: String, var assets: List<Asset>)