package com.byoskill.ejb2spring.invoker;

import java.sql.Connection;

/**
 * Functional interface for invoking EJB methods.
 * This interface provides a standard signature for method invocation that can
 * be
 * decorated with cross-cutting concerns (logging, transactions, exception
 * handling).
 *
 * <p>
 * Implementations can be composed using the decorator pattern to add
 * functionality without modifying the core invocation logic.
 * </p>
 *
 * @author EJB2Spring Framework
 */
@FunctionalInterface
public interface EjbMethodInvoker {

    /**
     * Invokes an EJB method with the provided parameters.
     *
     * @param serviceRequest the service request object (typically an EJB request
     *                       DTO)
     * @param connection     the database connection to use for this invocation
     * @return the result of the method invocation
     * @throws Exception if the method invocation fails
     */
    Object invoke(Object serviceRequest, Connection connection) throws Exception;
}
