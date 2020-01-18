package com.dude.dms.brain

typealias CreateEvent<T> = (T) -> Unit

typealias EditEvent<T> = (T) -> Unit

typealias DeleteEvent<T> = (T) -> Unit

typealias ParseEvent = (Boolean) -> Unit