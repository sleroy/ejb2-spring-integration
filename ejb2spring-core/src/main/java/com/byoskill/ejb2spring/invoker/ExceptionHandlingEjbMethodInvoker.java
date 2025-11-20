package com.byoskill.ejb2spring.invoker;

import com.byoskill.ejb2spring.exception.BeanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Decorator that adds exception handling and retry logic to EJB method
 * invocations.
 * This decorator can retry transient failures and transform exceptions into
 * appropriate types.
 *
 * <p>
 * Features:
 * </p>
 * <ul>
 * <li>Automatic retry for transient exceptions (e.g., temporary database
 * failures)</li>
 * <li>Exponential backoff between retries</li>
 * <li>Exception transformation for better error handling</li>
 * <li>Configurable retry attempts</li>
 * </ul>
 *
 * @author EJB2Spring Framework
 */
public class ExceptionHandlingEjbMethodInvoker implements EjbMethodInvoker {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlingEjbMethodInvoker.class);

    private final EjbMethodInvoker delegate;
    private final int maxRetries;
    private final Function<Exception, Exception> exceptionTransformer;

    /**
     * Constructs a new ExceptionHandlingEjbMethodInvoker with all options.
     *
     * @param delegate             the wrapped invoker to delegate to
     * @param maxRetries           maximum number of retry attempts (0 means no
     *                             retries)
     * @param exceptionTransformer function to transform exceptions (null means no
     *                             transformation)
     */
    public ExceptionHandlingEjbMethodInvoker(EjbMethodInvoker delegate,
            int maxRetries,
            Function<Exception, Exception> exceptionTransformer) {
        this.delegate = delegate;
        this.maxRetries = Math.max(0, maxRetries);
        this.exceptionTransformer = exceptionTransformer;
    }

    /**
     * Constructs a new ExceptionHandlingEjbMethodInvoker with retry support.
     *
     * @param delegate   the wrapped invoker to delegate to
     * @param maxRetries maximum number of retry attempts
     */
    public ExceptionHandlingEjbMethodInvoker(EjbMethodInvoker delegate, int maxRetries) {
        this(delegate, maxRetries, null);
    }

    /**
     * Constructs a new ExceptionHandlingEjbMethodInvoker with no retries.
     *
     * @param delegate the wrapped invoker to delegate to
     */
    public ExceptionHandlingEjbMethodInvoker(EjbMethodInvoker delegate) {
        this(delegate, 0, null);
    }

    @Override
    public Object invoke(Object serviceRequest, Connection connection) throws Exception {
        int attempt = 0;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                // Delegate to wrapped invoker
                return delegate.invoke(serviceRequest, connection);

            } catch (Exception e) {
                lastException = e;
                attempt++;

                // Check if we should retry
                if (attempt <= maxRetries && isRetryable(e)) {
                    long backoffMs = calculateBackoff(attempt);
                    log.warn("EJB invocation failed (attempt {}/{}), retrying after {} ms: {}",
                            attempt, maxRetries + 1, backoffMs, e.getMessage());

                    try {
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new BeanException("Retry interrupted", ie);
                    }
                } else {
                    // No more retries or exception is not retryable
                    break;
                }
            }
        }

        // All retries exhausted or exception not retryable
        if (lastException != null) {
            // Transform exception if transformer is configured
            if (exceptionTransformer != null) {
                lastException = exceptionTransformer.apply(lastException);
            }

            // Wrap in BeanException if not already
            if (!(lastException instanceof BeanException)) {
                throw new BeanException("EJB invocation failed after " + attempt + " attempt(s)", lastException);
            }

            throw lastException;
        }

        // Should never reach here
        throw new BeanException("Unexpected error in exception handling");
    }

    /**
     * Determines if an exception is retryable (transient failure).
     *
     * @param e the exception to check
     * @return true if the exception indicates a transient failure that might
     *         succeed on retry
     */
    private boolean isRetryable(Exception e) {
        // Database connection issues are often transient
        if (e instanceof SQLException) {
            SQLException sqlEx = (SQLException) e;
            String sqlState = sqlEx.getSQLState();

            // Common transient SQL states
            return sqlState != null && (sqlState.startsWith("08") || // Connection exceptions
                    sqlState.startsWith("40") || // Transaction rollback
                    sqlState.equals("HY000") // General error (might be transient)
            );
        }

        // Timeout exceptions are usually retryable
        String message = e.getMessage();
        if (message != null) {
            String lowerMessage = message.toLowerCase();
            return lowerMessage.contains("timeout") ||
                    lowerMessage.contains("connection") ||
                    lowerMessage.contains("unavailable");
        }

        // Default: not retryable
        return false;
    }

    /**
     * Calculates exponential backoff time for retry attempts.
     *
     * @param attempt the current attempt number (1-based)
     * @return backoff time in milliseconds
     */
    private long calculateBackoff(int attempt) {
        // Exponential backoff: 100ms, 200ms, 400ms, 800ms, etc.
        // Capped at 5 seconds
        long backoff = (long) (100 * Math.pow(2, attempt - 1));
        return Math.min(backoff, 5000);
    }

    /**
     * Gets the maximum number of retries.
     *
     * @return max retries
     */
    public int getMaxRetries() {
        return maxRetries;
    }
}
