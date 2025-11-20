package com.byoskill.ejb2spring.exception;

/**
 * Exception thrown when EJB bean creation or invocation fails.
 * This exception wraps underlying errors that occur during the EJB-to-Spring
 * integration process.
 *
 * @author EJB2Spring Framework
 */
public class BeanException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new BeanException with the specified detail message.
     *
     * @param message the detail message
     */
    public BeanException(String message) {
        super(message);
    }

    /**
     * Constructs a new BeanException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new BeanException with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public BeanException(Throwable cause) {
        super(cause);
    }
}
