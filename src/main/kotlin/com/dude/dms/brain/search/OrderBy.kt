package com.dude.dms.brain.search

data class OrderBy(val key: OrderKey, val direction: OrderDirection) : Translatable {
    override fun translate() = " ORDER BY ${key.translate()} ${direction.translate()}"
}

abstract class OrderDirection : Translatable

object Asc : OrderDirection() {
    override fun translate() = "ASC"
}

object Desc : OrderDirection() {
    override fun translate() = "DESC"
}