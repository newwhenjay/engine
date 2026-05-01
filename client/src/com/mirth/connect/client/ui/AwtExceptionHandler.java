package com.mirth.connect.client.ui;

/**
 * Handler for AWT exceptions (EDT) via sun.awt.exception.handler.
 */
public class AwtExceptionHandler {

    public void handle(Throwable t) {
        DebugLog.error("AWT exception", t);
    }
}

