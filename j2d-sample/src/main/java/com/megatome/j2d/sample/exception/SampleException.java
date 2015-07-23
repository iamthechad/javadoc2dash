package com.megatome.j2d.sample.exception;

/**
 * Sample exception
 */
public class SampleException extends Exception {
    /**
     * Sample ctor
     */
    public SampleException() {
    }

    /**
     * Sample ctor
     * {@inheritDoc}
     */
    public SampleException(String message) {
        super(message);
    }

    /**
     * Sample ctor
     * {@inheritDoc}
     */
    public SampleException(String message, Throwable cause) {
        super(message, cause);
    }
}
