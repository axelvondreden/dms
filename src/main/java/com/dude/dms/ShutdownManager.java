package com.dude.dms;

import org.springframework.stereotype.Component;

@Component
class ShutdownManager {

    public void initiateShutdown(int returnCode) {
        System.exit(returnCode);
    }
}