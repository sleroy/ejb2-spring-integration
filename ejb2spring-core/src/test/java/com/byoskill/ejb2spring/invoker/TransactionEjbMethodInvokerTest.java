package com.byoskill.ejb2spring.invoker;

import com.byoskill.ejb2spring.scope.TransactionPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionEjbMethodInvoker.
 */
class TransactionEjbMethodInvokerTest {

    private Connection mockConnection;
    private EjbMethodInvoker mockDelegate;

    @BeforeEach
    void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockDelegate = mock(EjbMethodInvoker.class);

        // Setup default behavior
        when(mockConnection.getAutoCommit()).thenReturn(true);
    }

    @Test
    void shouldCommitOnSuccessWithSuccessPolicy() throws Exception {
        // Arrange
        when(mockDelegate.invoke(any(), any())).thenReturn("success");
        TransactionEjbMethodInvoker invoker = new TransactionEjbMethodInvoker(
                mockDelegate,
                TransactionPolicy.SUCCESS);

        // Act
        Object result = invoker.invoke("request", mockConnection);

        // Assert
        assertEquals("success", result);
        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).commit();
        verify(mockConnection, never()).rollback();
        verify(mockConnection).setAutoCommit(true);
    }

    @Test
    void shouldRollbackOnExceptionWithSuccessPolicy() throws Exception {
        // Arrange
        when(mockDelegate.invoke(any(), any())).thenThrow(new RuntimeException("error"));
        TransactionEjbMethodInvoker invoker = new TransactionEjbMethodInvoker(
                mockDelegate,
                TransactionPolicy.SUCCESS);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> invoker.invoke("request", mockConnection));

        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection, never()).commit();
        verify(mockConnection).rollback();
    }

    @Test
    void shouldAlwaysCommitWithAlwaysPolicy() throws Exception {
        // Arrange
        when(mockDelegate.invoke(any(), any())).thenThrow(new RuntimeException("error"));
        TransactionEjbMethodInvoker invoker = new TransactionEjbMethodInvoker(
                mockDelegate,
                TransactionPolicy.ALWAYS);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> invoker.invoke("request", mockConnection));

        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).commit();
        verify(mockConnection, never()).rollback();
    }

    @Test
    void shouldAlwaysRollbackWithNeverPolicy() throws Exception {
        // Arrange
        when(mockDelegate.invoke(any(), any())).thenReturn("success");
        TransactionEjbMethodInvoker invoker = new TransactionEjbMethodInvoker(
                mockDelegate,
                TransactionPolicy.NEVER);

        // Act
        Object result = invoker.invoke("request", mockConnection);

        // Assert
        assertEquals("success", result);
        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection, never()).commit();
        verify(mockConnection).rollback();
    }

    @Test
    void shouldSkipTransactionWhenConnectionIsNull() throws Exception {
        // Arrange
        when(mockDelegate.invoke(any(), isNull())).thenReturn("success");
        TransactionEjbMethodInvoker invoker = new TransactionEjbMethodInvoker(
                mockDelegate,
                TransactionPolicy.SUCCESS);

        // Act
        Object result = invoker.invoke("request", null);

        // Assert
        assertEquals("success", result);
        verifyNoInteractions(mockConnection);
    }

    @Test
    void shouldInvokeRollbackCallbackOnRollback() throws Exception {
        // Arrange
        when(mockDelegate.invoke(any(), any())).thenThrow(new RuntimeException("error"));

        boolean[] callbackInvoked = { false };
        TransactionEjbMethodInvoker invoker = new TransactionEjbMethodInvoker(
                mockDelegate,
                TransactionPolicy.SUCCESS,
                (conn, ex) -> callbackInvoked[0] = true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> invoker.invoke("request", mockConnection));

        assertTrue(callbackInvoked[0], "Rollback callback should have been invoked");
        verify(mockConnection).rollback();
    }
}
