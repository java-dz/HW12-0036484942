package hr.fer.zemris.java.custom.scripting.exec;

/**
 * The <tt>ValueWrapper</tt> class wraps a specified value and allows performing
 * operations of incrementing, decrementing, multiplying and dividing. This
 * class also provides a method for number comparing. In order to be able to use
 * these methods without getting a {@linkplain RuntimeException}, the specified
 * value has to be a parsable integer or a double number. This means that the
 * specified value must be an Integer or a Double, or a String that can be
 * parsed into one of those types.
 *
 * @author Mario Bobic
 */
public class ValueWrapper {

    /** Lowest decimal number value until it is regarded as zero. */
    private static final double ZERO_LIMIT = 1E-20;

    /** The value stored in this wrapper. */
    private Object value;

    /**
     * Constructs an instance of <tt>ValueWrapper</tt> with the specified value.
     *
     * @param value value to be stored in this wrapper
     */
    public ValueWrapper(Object value) {
        this.value = value;
    }

    /**
     * Returns the number representation of the value stored in this wrapper.
     *
     * @return the number representation of the value stored in this wrapper
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value of this wrapper.
     *
     * @param value value to be set to this wrapper
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Increments the value stored in this wrapper by <tt>incValue</tt>, if the
     * <tt>incValue</tt> can be interpreted as an {@linkplain Integer} or a
     * {@linkplain Double} number.
     * <p>
     * This method throws a {@linkplain RuntimeException} if the specified
     * argument is neither an Integer, a Double or a String. If the specified
     * argument is an instance of String class, but can not be parsed as an
     * Integer or a Double, a {@linkplain NumberFormatException} is thrown.
     *
     * @param incValue incrementing value
     * @throws RuntimeException
     *             if the specified argument is neither an Integer, a Double or
     *             a String
     * @throws NumberFormatException
     *             if the argument is an instance of String class, but can not
     *             be parsed as an Integer or a Double
     */
    public void increment(Object incValue) {
        Number value1 = getNumber(value);
        Number value2 = getNumber(incValue);
        value = cast(value1, value2, value1.doubleValue() + value2.doubleValue());
    }

    /**
     * Decrements the value stored in this wrapper by <tt>decValue</tt>, if the
     * <tt>decValue</tt> can be interpreted as an {@linkplain Integer} or a
     * {@linkplain Double} number.
     * <p>
     * This method throws a {@linkplain RuntimeException} if the specified
     * argument is neither an Integer, a Double or a String. If the specified
     * argument is an instance of String class, but can not be parsed as an
     * Integer or a Double, a {@linkplain NumberFormatException} is thrown.
     *
     * @param decValue decrementing value
     * @throws RuntimeException
     *             if the specified argument is neither an Integer, a Double or
     *             a String
     * @throws NumberFormatException
     *             if the argument is an instance of String class, but can not
     *             be parsed as an Integer or a Double
     */
    public void decrement(Object decValue) {
        Number value1 = getNumber(value);
        Number value2 = getNumber(decValue);
        value = cast(value1, value2, value1.doubleValue() - value2.doubleValue());
    }

    /**
     * Multiplies the value stored in this wrapper by <tt>mulValue</tt>, if the
     * <tt>mulValue</tt> can be interpreted as an {@linkplain Integer} or a
     * {@linkplain Double} number.
     * <p>
     * This method throws a {@linkplain RuntimeException} if the specified
     * argument is neither an Integer, a Double or a String. If the specified
     * argument is an instance of String class, but can not be parsed as an
     * Integer or a Double, a {@linkplain NumberFormatException} is thrown.
     *
     * @param mulValue multiplying value
     * @throws RuntimeException
     *             if the specified argument is neither an Integer, a Double or
     *             a String
     * @throws NumberFormatException
     *             if the argument is an instance of String class, but can not
     *             be parsed as an Integer or a Double
     */
    public void multiply(Object mulValue) {
        Number value1 = getNumber(value);
        Number value2 = getNumber(mulValue);
        value = cast(value1, value2, value1.doubleValue() * value2.doubleValue());
    }

    /**
     * Divides the value stored in this wrapper by <tt>divValue</tt>, if the
     * <tt>divValue</tt> can be interpreted as an {@linkplain Integer} or a
     * {@linkplain Double} number.
     * <p>
     * This method throws a {@linkplain RuntimeException} if the specified
     * argument is neither an Integer, a Double or a String. If the specified
     * argument is an instance of String class, but can not be parsed as an
     * Integer or a Double, a {@linkplain NumberFormatException} is thrown. If
     * the specified argument passed all checks, but is a zero, an
     * {@linkplain ArithmeticException} is thrown with an appropriate detail
     * message.
     *
     * @param divValue dividing value
     * @throws RuntimeException
     *             if the specified argument is neither an Integer, a Double or
     *             a String
     * @throws NumberFormatException
     *             if the argument is an instance of String class, but can not
     *             be parsed as an Integer or a Double
     * @throws ArithmeticException
     *             if the specified argument is a zero
     */
    public void divide(Object divValue) {
        Number value1 = getNumber(value);
        Number value2 = getNumber(divValue);

        if (Math.abs(value2.doubleValue()) < ZERO_LIMIT) {
            throw new ArithmeticException("/ by zero: " + value + " / 0");
        }

        value = cast(value1, value2, value1.doubleValue() / value2.doubleValue());
    }

    /**
     * Compares the value stored in this wrapper with the specified
     * <tt>withValue</tt> numerically.
     * <p>
     * This method throws a {@linkplain RuntimeException} if the specified
     * argument is neither an Integer, a Double or a String. If the specified
     * argument is an instance of String class, but can not be parsed as an
     * Integer or a Double, a {@linkplain NumberFormatException} is thrown.
     *
     * @param withValue value to be compared
     * @return <tt>0</tt> if <tt>withValue</tt> is numerically equal to the
     *         value stored in this wrapper; a value less than <tt>0</tt> if
     *         this value is numerically less than <tt>withValue</tt>; and a
     *         value greater than <tt>0</tt> if this <tt>value</tt> is
     *         numerically greater than <tt>withValue</tt>
     * @throws RuntimeException
     *             if the specified argument is neither an Integer, a Double or
     *             a String
     * @throws NumberFormatException
     *             if the argument is an instance of String class, but can not
     *             be parsed as an Integer or a Double
     */
    public int numCompare(Object withValue) {
        Double value1 = getNumber(value).doubleValue();
        Double value2 = getNumber(withValue).doubleValue();
        return value1.compareTo(value2);
    }

    /**
     * Returns a {@linkplain Number} that the specified object represents.
     * <p>
     * This method throws a {@linkplain RuntimeException} if the specified
     * argument is neither an Integer, a Double or a String. If the specified
     * argument is an instance of String class, but can not be parsed as an
     * Integer or a Double, a {@linkplain NumberFormatException} is thrown.
     *
     * @param obj object to be converted into a {@linkplain Number}
     * @return a {@linkplain Number} that the specified object represents
     * @throws RuntimeException
     *             if the specified argument is neither an Integer, a Double or
     *             a String
     * @throws NumberFormatException
     *             if the argument is an instance of String class, but can not
     *             be parsed as an Integer or a Double
     */
    private static Number getNumber(Object obj) {
        if (obj == null) {
            return Integer.valueOf(0);
        }
        if (obj instanceof Integer || obj instanceof Double) {
            return (Number) obj;
        }
        if (obj instanceof String) {
            return parseNumber((String) obj);
        }

        throw new RuntimeException(
            "The specified object must be either an"
            + "instance of Integer, Double or String: "
            + "[" + obj + "]"
        );
    }

    /**
     * Casts and returns the specified <tt>result</tt> into an integer if
     * neither <tt>value1</tt> nor </tt>value2</tt> are instances of Double.
     * If any of these values are of type {@linkplain Double}, the result
     * is cast into a double.
     *
     * @param value1 a numerical value
     * @param value2 a numerical value
     * @param result the result that is to be cast into an integer or double
     * @return an integer or double of the </tt>result</tt>
     */
    private static Number cast(Number value1, Number value2, Number result) {
        if (value1 instanceof Double || value2 instanceof Double) {
            return result.doubleValue();
        } else {
            return result.intValue();
        }
    }

    /**
     * Returns a number element parsed from <tt>s</tt> parameter. A number
     * element may be an {@linkplain Integer} or a {@linkplain Double}. This
     * method parses the string argument either as a signed decimal integer or a
     * signed decimal double.
     * <p>
     * In order to parse an integer number, the characters in the string must
     * all be decimal digits, except that the first character may be an ASCII
     * minus sign <tt>'-'</tt> to indicate a negative value or an ASCII plus
     * sign <tt>'+'</tt> to indicate a positive value. Integer parsing is done
     * by the {@link Integer#parseInt} method.
     * <p>
     * If the integer parsing does not succeed, the string is tried to be parsed
     * as a double. Double parsing is done by the {@link Double#parseDouble}
     * method.
     * <p>
     * If none of the parsing methods succeed, a
     * {@linkplain NumberFormatException} is thrown.
     *
     * @param s number to be parsed into an integer or a double
     * @return a number element parsed as an integer or a double
     * @throws NumberFormatException
     *             if all number parsing methods fail
     */
    private static Number parseNumber(String s) {
        try {
            // try to parse as integer
            return Integer.parseInt(s);
        } catch (NumberFormatException e1) {
            try {
                // if integer parsing fails, parse as double
                return Double.parseDouble(s);
            } catch (NumberFormatException e2) {
                throw new RuntimeException("Invalid number: " + s);
            }
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
