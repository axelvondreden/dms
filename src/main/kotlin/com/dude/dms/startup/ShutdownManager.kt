package com.dude.dms.startup

import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class ShutdownManager {

    fun initiateShutdown(returnCode: Int) {
        exitProcess(returnCode)
    }
}