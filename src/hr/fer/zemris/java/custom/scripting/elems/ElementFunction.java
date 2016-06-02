package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import static hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods.*;

/**
 * The {@linkplain Element} subclass. All elements contain some data that is
 * used for interpreting types of elements given to the {@link SmartScriptParser
 * parser}. This element overrides the {@link Element#asText} method and returns
 * the string representation of the function name stored in this element.
 * <p>
 * Please note that if the function name given to the constructor contains a
 * {@link SmartScriptConstantsAndMethods#FUNCTION_MARK function mark} symbol, it
 * is removed and only the function name in raw format is stored.
 *
 * @author Mario Bobic
 */
public class ElementFunction extends Element {

	/** The function name. */
	private final String name;
	
	/**
	 * Constructs an instance of ElementFunction with the given function name
	 * without a {@link SmartScriptConstantsAndMethods#FUNCTION_MARK function
	 * mark} symbol.
	 * 
	 * @param name name to be set to this function
	 */
	public ElementFunction(String name) {
		super();
		this.name = processName(name);
	}

	/**
	 * Returns a string representation of the function name stored in this
	 * element.
	 */
	@Override
	public String asText() {
		return FUNCTION_MARK + name;
	}
	
	/**
	 * Processes and returns the function name without a
	 * {@link SmartScriptConstantsAndMethods#FUNCTION_MARK function mark}
	 * symbol.
	 * 
	 * @param name name to be processed
	 * @return a name without a function mark symbol.
	 */
	private static String processName(String name) {
		return name.replace(FUNCTION_MARK, "");
	}

}
