package com.dude.dms.backend.brain

typealias CreateEvent<T> = (T) -> Unit

typealias EditEvent<T> = (T) -> Unit

typealias DeleteEvent<T> = (T) -> Unit

typealias ParseEvent = (Boolean) -> Unit