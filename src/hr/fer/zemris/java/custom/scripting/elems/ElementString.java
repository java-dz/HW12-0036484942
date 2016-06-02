package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import static hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods.*;

/**
 * The {@linkplain Element} subclass. All elements contain some data that is
 * used for interpreting types of elements given to the {@link SmartScriptParser
 * parser}. This element overrides the {@link Element#asText} method and returns
 * the string value stored in this element.
 * <p>
 * Please note that the string given to the constructor must be surrounded with
 * {@link SmartScriptConstantsAndMethods#QUOT_MARK quotation mark} symbols, as
 * the string value is trimmed and internally stored without the surrounding
 * symbols.
 *
 * @author Mario Bobic
 */
public class ElementString extends Element {

	/** The string value of this element. */
	private final String value;
	
	/**
	 * Constructs an instance of ElementString with the given string value.
	 * 
	 * @param value the string value for this element
	 */
	public ElementString(String value) {
		super();
		this.value = processValue(value);
	}
	
	/**
	 * Returns the string value stored in this element.
	 */
	@Override
	public String asText() {
		return value;
	}
	
	/**
	 * Processes and returns the string value without
	 * {@link SmartScriptConstantsAndMethods#QUOT_MARK quotation mark} symbols.
	 * 
	 * @param value value to be processed
	 * @return value without the surrounding quotation marks
	 * @throw IllegalArgumentException if value does not start and end with
	 *        &quot; symbol
	 */
	private static String processValue(String value) {
		if (!(value.startsWith(QUOT_MARK) && value.endsWith(QUOT_MARK))) {
			throw new IllegalArgumentException(
				"Strings must start and end with " + QUOT_MARK + " symbols");
		}
		return value.substring(1, value.length()-1)
				.replace("\\n", "\n")
				.replace("\\r", "\r")
				.replace("\\t", "\t");
	}

}
