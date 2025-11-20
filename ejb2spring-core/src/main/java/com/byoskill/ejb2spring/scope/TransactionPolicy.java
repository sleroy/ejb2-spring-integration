package com.byoskill.ejb2spring.scope;

/**
 * Defines transaction commit policies for EJB method invocations.
 * This enum determines when database transactions should be committed or rolled
 * back.
 *
 * @author EJB2Spring Framework
 */
public enum TransactionPolicy {

    /**
     * Commit the transaction only if the method completes successfully.
     * Roll back if an exception is thrown.
     * This is the default and most common policy.
     */
    SUCCESS,

    /**
     * Always commit the transaction, even if the method throws an exception.
     * This is useful for audit logging where you want to record failures.
     * Use with caution as it can lead to inconsistent state.
     */
    ALWAYS,

    /**
     * Never commit the transaction - always roll back.
     * This is useful for read-only operations or testing scenarios
     * where you want to examine data without making changes.
     */
    NEVER
}
