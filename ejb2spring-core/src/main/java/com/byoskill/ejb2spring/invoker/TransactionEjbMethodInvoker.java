package com.byoskill.ejb2spring.invoker;

import com.byoskill.ejb2spring.scope.TransactionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.function.BiConsumer;

/**
 * Decorator that adds transaction management to EJB method invocations.
 * This decorator wraps another invoker and handles database transaction
 * commit/rollback based on the configured policy.
 *
 * <p>
 * Supported policies:
 * </p>
 * <ul>
 * <li>SUCCESS - Commit on success, rollback on exception (default)</li>
 * <li>ALWAYS - Always commit, even on exception</li>
 * <li>NEVER - Always rollback (useful for read-only operations)</li>
 * </ul>
 *
 * @author EJB2Spring Framework
 */
public class TransactionEjbMethodInvoker implements EjbMethodInvoker {

    private static final Logger log = LoggerFactory.getLogger(TransactionEjbMethodInvoker.class);

    private final EjbMethodInvoker delegate;
    private final TransactionPolicy policy;
    private final BiConsumer<Connection, Exception> rollbackCallback;

    /**
     * Constructs a new TransactionEjbMethodInvoker with all options.
     *
     * @param delegate         the wrapped invoker to delegate to
     * @param policy           the transaction policy to use
     * @param rollbackCallback optional callback invoked when transaction is rolled
     *                         back
     */
    public TransactionEjbMethodInvoker(EjbMethodInvoker delegate,
            TransactionPolicy policy,
            BiConsumer<Connection, Exception> rollbackCallback) {
        this.delegate = delegate;
        this.policy = policy != null ? policy : TransactionPolicy.SUCCESS;
        this.rollbackCallback = rollbackCallback;
    }

    /**
     * Constructs a new TransactionEjbMethodInvoker with a policy.
     *
     * @param delegate the wrapped invoker to delegate to
     * @param policy   the transaction policy to use
     */
    public TransactionEjbMethodInvoker(EjbMethodInvoker delegate, TransactionPolicy policy) {
        this(delegate, policy, null);
    }

    /**
     * Constructs a new TransactionEjbMethodInvoker with default SUCCESS policy.
     *
     * @param delegate the wrapped invoker to delegate to
     */
    public TransactionEjbMethodInvoker(EjbMethodInvoker delegate) {
        this(delegate, TransactionPolicy.SUCCESS, null);
    }

    @Override
    public Object invoke(Object serviceRequest, Connection connection) throws Exception {
        if (connection == null) {
            // No transaction management needed
            log.debug("No connection provided, skipping transaction management");
            return delegate.invoke(serviceRequest, connection);
        }

        boolean success = false;
        boolean originalAutoCommit = connection.getAutoCommit();
        Exception capturedException = null;

        try {
            // Start transaction
            connection.setAutoCommit(false);
            log.debug("Transaction started with policy: {}", policy);

            // Delegate to wrapped invoker
            Object result = delegate.invoke(serviceRequest, connection);

            success = true;
            return result;

        } catch (Exception e) {
            capturedException = e;
            throw e;
        } finally {
            // Handle transaction based on policy
            handleTransaction(connection, success, capturedException);

            // Restore original auto-commit
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (Exception e) {
                log.warn("Failed to restore auto-commit state", e);
            }
        }
    }

    /**
     * Handles the transaction commit or rollback based on policy and success.
     *
     * @param conn      the database connection
     * @param success   whether the method invocation succeeded
     * @param exception the exception that occurred, if any
     */
    private void handleTransaction(Connection conn, boolean success, Exception exception) {
        try {
            boolean shouldCommit = determineCommit(success);

            if (shouldCommit) {
                conn.commit();
                log.debug("Transaction committed (policy: {}, success: {})", policy, success);
            } else {
                conn.rollback();
                log.debug("Transaction rolled back (policy: {}, success: {})", policy, success);

                // Invoke rollback callback if configured
                if (rollbackCallback != null) {
                    try {
                        rollbackCallback.accept(conn, exception);
                    } catch (Exception e) {
                        log.error("Error in rollback callback", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error handling transaction", e);
        }
    }

    /**
     * Determines whether to commit based on policy and success flag.
     *
     * @param success whether the method invocation succeeded
     * @return true if should commit, false if should rollback
     */
    private boolean determineCommit(boolean success) {
        switch (policy) {
            case SUCCESS:
                return success;
            case ALWAYS:
                return true;
            case NEVER:
                return false;
            default:
                return success;
        }
    }

    /**
     * Gets the transaction policy.
     *
     * @return the transaction policy
     */
    public TransactionPolicy getPolicy() {
        return policy;
    }
}
