package com.dude.dms.backend.brain.polling;

public interface PollingService {

    void manualPoll();

    void poll();
}