package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * An interface that provides a single method for processing a HTML request.
 *
 * @author Mario Bobic
 */
public interface IWebWorker {

    /**
     * Processes the request specified by the <tt>context</tt>.
     *
     * @param context the request context
     */
    public void processRequest(RequestContext context);

}
