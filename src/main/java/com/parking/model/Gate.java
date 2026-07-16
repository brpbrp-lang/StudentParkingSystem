package com.parking.model;


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
