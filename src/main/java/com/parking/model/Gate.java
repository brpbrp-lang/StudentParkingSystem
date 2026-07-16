package com.parking.model;

/**
 * Simulates the physical parking gate/barrier. In a real deployment, openGate()/closeGate()
 * would talk to a relay/microcontroller (e.g. over serial or GPIO); here they simply track state
 * and print to the console/log so the rest of the workflow can be demonstrated end-to-end.
 */
public class Gate {

    private boolean open = false;

    public boolean openGate() {
        open = true;
        System.out.println("[GATE] Gate opened.");
        return true;
    }

    public boolean closeGate() {
        open = false;
        System.out.println("[GATE] Gate closed.");
        return true;
    }

    public boolean isOpen() {
        return open;
    }
}
