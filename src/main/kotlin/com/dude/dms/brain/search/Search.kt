package com.dude.dms.brain.search

data class Search(val query: Query?, val order: OrderBy?) : Translatable {
    override fun translate() = "${query?.translate() ?: ""} ${order?.translate() ?: ""}"
}