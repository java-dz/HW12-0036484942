package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Creates a HTML page with current time displayed and gives a different message
 * depending on the parameter called "name" which was provided in URL that
 * started this worker.
 *
 * @author Mario Bobic
 */
public class HelloWorker implements IWebWorker {
	
	@Override
	public void processRequest(RequestContext context) {
		context.setMimeType("text/html");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		
		String name = context.getParameter("name");
		try {
			context.write("<html><body>");
			context.write("<h1>Hello!!!</h1>");
			context.write("<p>Now is: " + sdf.format(now) + "</p>");
			if (name == null || name.trim().isEmpty()) {
				context.write("<p>You did not send me your name!</p>");
			} else {
				context.write("<p>Your name has " + name.trim().length() + " letters.</p>");
			}
			context.write("</body></html>");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}