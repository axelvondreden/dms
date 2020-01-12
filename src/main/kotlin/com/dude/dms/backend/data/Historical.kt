package com.dude.dms.backend.data

import com.dude.dms.backend.data.history.History

@FunctionalInterface
interface Historical<T : History> {
    val history: List<T>
}