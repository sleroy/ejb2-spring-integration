package com.byoskill.ejb2spring.invoker;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Base implementation of EjbMethodInvoker that handles direct method
 * invocation.
 * This is the core invoker that performs the actual method call via reflection.
 * Other decorators wrap this to add cross-cutting concerns.
 *
 * <p>
 * This invoker uses reflection to call any method on the EJB bean instance,
 * passing the service request and database connection as parameters.
 * </p>
 *
 * @author EJB2Spring Framework
 */
public class BaseEjbMethodInvoker implements EjbMethodInvoker {

    private final Object beanInstance;
    private final Method method;

    /**
     * Constructs a new BaseEjbMethodInvoker.
     *
     * @param beanInstance the EJB bean instance to invoke methods on
     * @param method       the method to invoke via reflection
     */
    public BaseEjbMethodInvoker(Object beanInstance, Method method) {
        this.beanInstance = beanInstance;
        this.method = method;
    }

    /**
     * Invokes the method on the bean instance with the provided parameters.
     *
     * @param serviceRequest the service request object
     * @param connection     the database connection
     * @return the result of the method invocation
     * @throws Exception if the method invocation fails
     */
    @Override
    public Object invoke(Object serviceRequest, Connection connection) throws Exception {
        // Make method accessible if it's private or protected
        method.setAccessible(true);

        // Invoke the method with the service request and connection
        // The method signature should match the EJB bean's method
        return method.invoke(beanInstance, serviceRequest, connection);
    }

    /**
     * Gets the bean instance being invoked.
     *
     * @return the bean instance
     */
    public Object getBeanInstance() {
        return beanInstance;
    }

    /**
     * Gets the method being invoked.
     *
     * @return the method
     */
    public Method getMethod() {
        return method;
    }
}
