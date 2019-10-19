package com.dude.dms.startup;

import org.springframework.stereotype.Component;

@Component
class ShutdownManager {

    public void initiateShutdown(int returnCode) {
        System.exit(returnCode);
    }
}