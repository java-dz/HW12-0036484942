package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * The {@linkplain Element} subclass. All elements contain some data that is
 * used for interpreting types of elements given to the {@link SmartScriptParser
 * parser}. This element overrides the {@link Element#asText} method and returns
 * the string representation of a constant double value stored in this element.
 *
 * @author Mario Bobic
 */
public class ElementConstantDouble extends Element {

	/** The constant double value of this element. */
	private final double value;
	
	/**
	 * Constructs an instance of ElementConstantDouble with the given constant
	 * double value.
	 * 
	 * @param value the constant double value
	 */
	public ElementConstantDouble(double value) {
		super();
		this.value = value;
	}

	/**
	 * Returns a string representation of a constant double value stored in this
	 * element.
	 */
	@Override
	public String asText() {
		return Double.toString(value);
	}

}
