package com.byoskill.ejb2spring.invoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * Decorator that adds logging to EJB method invocations.
 * This decorator logs method entry, exit, duration, and any exceptions that
 * occur.
 *
 * <p>
 * Log entries include:
 * </p>
 * <ul>
 * <li>Method entry with request details</li>
 * <li>Method exit with duration</li>
 * <li>Exception details if the method fails</li>
 * </ul>
 *
 * @author EJB2Spring Framework
 */
public class LoggingEjbMethodInvoker implements EjbMethodInvoker {

    private static final Logger log = LoggerFactory.getLogger(LoggingEjbMethodInvoker.class);

    private final EjbMethodInvoker delegate;
    private final String methodName;

    /**
     * Constructs a new LoggingEjbMethodInvoker.
     *
     * @param delegate   the wrapped invoker to delegate to
     * @param methodName the name of the method being invoked (for logging)
     */
    public LoggingEjbMethodInvoker(EjbMethodInvoker delegate, String methodName) {
        this.delegate = delegate;
        this.methodName = methodName != null ? methodName : "unknown";
    }

    @Override
    public Object invoke(Object serviceRequest, Connection connection) throws Exception {
        long startTime = System.currentTimeMillis();

        log.info("Invoking EJB method: {}", methodName);
        log.debug("Request type: {}", serviceRequest != null ? serviceRequest.getClass().getSimpleName() : "null");

        try {
            // Delegate to wrapped invoker
            Object result = delegate.invoke(serviceRequest, connection);

            long duration = System.currentTimeMillis() - startTime;
            log.info("EJB method {} completed successfully in {} ms", methodName, duration);
            log.debug("Result type: {}", result != null ? result.getClass().getSimpleName() : "null");

            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("EJB method {} failed after {} ms: {}", methodName, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Gets the method name being logged.
     *
     * @return the method name
     */
    public String getMethodName() {
        return methodName;
    }
}
