package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;
import java.util.Random;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Creates and sends a HTML document that shows the game interface with
 * messages. The worker interacts with persistent parameters from the request
 * context to store information about game progress.
 * <p>
 * This program allows the user to enter all integer numbers, even those with an
 * invalid range.
 *
 * @author Mario Bobic
 */
public class Igra implements IWebWorker {
	
	/** Number key in the persistent parameters map. */
	private static final String NUMBER_KEY = "broj";
	/** Remaining number of attempts key in the persistent parameters map. */
	private static final String REMAINING_KEY = "preostalo";
	/** Candidate number key in the parameters map. */
	private static final String CANDIDATE_KEY = "kandidat";
	
	/** Initial amount of attempts. */
	private static final int ATTEMPT_AMOUNT = 7;
	/** The upper limit of number to guess. */
	private static final int LIMIT = 100;
	
	/** Message to the user. */
	private String message = null;
	
	/** Random number to be guessed. */
	private int numberToGuess;
	/** Remaining number of attempts. */
	private int remainingAttempts;

	@Override
	public void processRequest(RequestContext context) {
		context.setMimeType("text/html");
		
		synchronized(Igra.class) {
			initializeParameters(context);
			
			getAndProcessUserEntry(context);
		}
		
		appendRemainingMessage();
		
		try {
			context.write(getForm(message));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Initializes the persistent parameters by getting the existing information
	 * or by initializing the default parameters if no information is stored.
	 * 
	 * @param context the request context
	 */
	private void initializeParameters(RequestContext context) {
		/* Get existing information or initialize default parameters. */
		String number = context.getPersistentParameter(NUMBER_KEY);
		String remaining = context.getPersistentParameter(REMAINING_KEY);
		
		if (number == null) {
			numberToGuess = new Random().nextInt(LIMIT) + 1;
			remainingAttempts = ATTEMPT_AMOUNT;
			
			context.setPersistentParameter(NUMBER_KEY, Integer.toString(numberToGuess));
			context.setPersistentParameter(REMAINING_KEY, Integer.toString(remainingAttempts));
		} else {
			numberToGuess = Integer.valueOf(number);
			remainingAttempts = Integer.valueOf(remaining);
		}
	}
	
	/**
	 * Gets the user entry from <tt>context</tt> and
	 * {@link #processEntry(String, RequestContext) processes} if it exists. If
	 * user has guessed the number or the remaining number of attempts reached
	 * zero, the game is over and persistent parameters are removed.
	 * 
	 * @param context the request context
	 */
	private void getAndProcessUserEntry(RequestContext context) {
		String userEntry = context.getParameter(CANDIDATE_KEY);
		
		if (userEntry != null) {
			boolean over = processEntry(userEntry, context);
			if (over) {
				context.removePersistentParameter(NUMBER_KEY);
				context.removePersistentParameter(REMAINING_KEY);
			} else {
				context.setPersistentParameter(REMAINING_KEY, Integer.toString(remainingAttempts));
			}
		}
	}
	
	/**
	 * Processes the user entry and returns <tt>true</tt> if the game is
	 * supposed to be over (user has guessed the number or the remaining number
	 * of attempts reached zero), or <tt>false</tt> if the game must continue
	 * (user has entered an invalid integer or the remaining number of attempts
	 * is greater than zero.
	 * 
	 * @param userEntry the user parameter entry
	 * @param context the request context
	 * @return true if the game must end, false otherwise
	 */
	private boolean processEntry(String userEntry, RequestContext context) {
		if (!isInteger(userEntry)) {
			message = "Molim unesite ispravan cijeli broj.";
			return false;
		}

		int userParameter = Integer.valueOf(userEntry);
		if (remainingAttempts-- > 1) {
			if (userParameter == numberToGuess) {
				message = "Bravo, pogodili ste!";
				return true;
			} else {
				message = "Odgovor " + userParameter + " je ";
				message += userParameter > numberToGuess ? "previsok" : "prenizak";
				return false;
			}
		} else {
			// Last attempt
			if (userParameter == numberToGuess) {
				message = "Bravo, pogodili ste!";
				return true;
			}
		}
		
		message = "Niste pogodili broj. Zamišljeni broj je: " + numberToGuess;
		return true;
	}
	
	/**
	 * Returns true if string <tt>s</tt> can be parsed as an <tt>Integer</tt>
	 * using the {@linkplain Integer#parseInt(String)} method. False otherwise.
	 * 
	 * @param s the user parameter entry
	 * @return true if <tt>s</tt> can be parsed as Integer, false otherwise
	 */
	private boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Appends the remaining number of attempts to the existing message. If the
	 * message was previously <tt>null</tt>, it is set to the remaining message.
	 */
	private void appendRemainingMessage() {
		/* Append the remaining number of attempts. */
		String remainingMessage = "(preostali broj pokušaja: " + remainingAttempts + ")";
		if (message == null) {
			message = remainingMessage;
		} else {
			message += " " + remainingMessage;
		}
	}
	
	/**
	 * Creates a HTML code necessary for displaying the user interface and
	 * appends the optional <tt>message</tt> in red color. Returns the HTML
	 * code as a <tt>String</tt>.
	 * 
	 * @param message the optional message, may be <tt>null</tt>
	 * @return a string containing HTML code
	 */
	private static String getForm(String message) {
		return ("<html>\r\n" +
				"  <head>\r\n" + 
				"    <title>Pogodi broj</title>\r\n" +
				"  </head>\r\n" + 
				"  <body>\r\n" + 
				"    <h1>Pogodi broj [0-"+LIMIT+"]</h1>\r\n" +
				"    <form action=\"/ext/Igra\">\r\n" +
				"      Što mislite koji je broj? <input type=\"text\" name=\"kandidat\">" +
				" <font color=\"red\">" + (message == null ? "" : message) + "</font> <br>\r\n" +
				"      <input type=\"submit\">" +
				"    </form>" +
				"  </body>\r\n" + 
				"</html>\r\n");
	}
	
}
