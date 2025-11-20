package com.byoskill.ejb2spring.scope;

/**
 * Defines the lifecycle scope for EJB bean instances.
 * This enum determines how long a bean instance lives and when it should be
 * reused or recreated.
 *
 * @author EJB2Spring Framework
 */
public enum EjbScope {

    /**
     * Create a new bean instance for each HTTP request.
     * This is the default scope and provides the best isolation between requests.
     */
    REQUEST,

    /**
     * Reuse the same bean instance within an HTTP session.
     * Use this for stateful operations that need to maintain state across multiple
     * requests
     * from the same user.
     */
    SESSION,

    /**
     * Use a single shared bean instance for all requests (singleton pattern).
     * This is the most efficient but requires the bean to be thread-safe.
     * Use this for stateless beans that don't maintain any per-request state.
     */
    SINGLETON,

    /**
     * Create a new bean instance every time it's requested.
     * This provides maximum isolation but has the highest overhead.
     * Use this when you need a completely fresh bean for every operation.
     */
    PROTOTYPE
}
