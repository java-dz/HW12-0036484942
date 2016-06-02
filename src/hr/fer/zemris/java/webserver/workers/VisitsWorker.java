package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * This class implements the {@linkplain IWebWorker} interface and processes client
 * requests just by writing the number of "visits" to this object, which is actually
 * the number of calls on {@linkplain #processRequest(RequestContext)} method.
 * <p>
 * The implementation is <strong>thread safe</strong>.
 *
 * @author Mario Bobic
 */
public class VisitsWorker implements IWebWorker {
	
	/** Number of visits. */
	private long counter = 0;

	@Override
	public void processRequest(RequestContext context) {
		context.setMimeType("text/plain");
		
		long currentCount;
		synchronized (this) {
			counter++;
			currentCount = counter;
		}
		
		try {
			context.write("Site visited " + currentCount + " times globally.\r\n");
			context.write("Try running from different web browsers.");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
