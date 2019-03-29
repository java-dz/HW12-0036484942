package hr.fer.zemris.java.custom.collections;

/**
 * This class is a functional class for processing an Object. On each round, a
 * processor may be asked to {@linkplain #process process} the value passed as
 * an argument to the only method in this class.
 *
 * @author Mario Bobic
 */
public class Processor {

    /**
     * Processes the value. This method should be overridden by implementing the
     * actual processing of the object.
     *
     * @param value the object to be processed
     */
    public void process(Object value) {
    }

}
