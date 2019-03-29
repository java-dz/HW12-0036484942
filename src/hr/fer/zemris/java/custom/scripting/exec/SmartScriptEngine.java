package hr.fer.zemris.java.custom.scripting.exec;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Stack;

import hr.fer.zemris.java.custom.scripting.elems.*;
import hr.fer.zemris.java.custom.scripting.nodes.*;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * The {@code SmartScriptEngine} is an engine for running documents parsed with
 * the {@linkplain SmartScriptParser}. Defines behavior when visiting
 * {@linkplain TextNode}, {@linkplain ForLoopNode}, {@linkplain EchoNode} and
 * {@linkplain DocumentNode}.
 *
 * @author Mario Bobic
 */
public class SmartScriptEngine {

    /** Document node to be visited. */
    private DocumentNode documentNode;
    /** Request context used by some functions. */
    private RequestContext requestContext;
    /** The variable multistack. */
    private ObjectMultistack multistack = new ObjectMultistack();

    /**
     * Constructs an instance of {@code SmartScriptEngine} with the specified
     * parameters.
     *
     * @param documentNode document node to be visited
     * @param requestContext request context used by some functions
     */
    public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
        this.documentNode = documentNode;
        this.requestContext = requestContext;
    }

    /**
     * This engine's visitor. Defines behavior when visiting
     * {@linkplain TextNode}, {@linkplain ForLoopNode}, {@linkplain EchoNode}
     * and {@linkplain DocumentNode}.
     */
    private INodeVisitor visitor = new INodeVisitor() {

        @Override
        public void visitTextNode(TextNode node) {
            write(node.getText());
        }

        @Override
        public void visitForLoopNode(ForLoopNode node) {
            ValueWrapper startValue = new ValueWrapper(node.getStartExpression().asText());
            ValueWrapper endValue = new ValueWrapper(node.getEndExpression().asText());
            ValueWrapper stepValue = new ValueWrapper(node.getStepExpression().asText());

            String variable = node.getVariable().asText();
            ValueWrapper currentValue = new ValueWrapper(startValue.getValue());

            multistack.push(variable, startValue);

            while (currentValue.numCompare(endValue.getValue()) <= 0) {
                for (int i = 0; i < node.numberOfChildren(); i++) {
                    node.getChild(i).accept(visitor);
                }

                currentValue = multistack.pop(variable);
                currentValue.increment(stepValue.getValue());
                multistack.push(variable, currentValue);
            }

            multistack.pop(node.getVariable().asText());
        }

        @Override
        public void visitEchoNode(EchoNode node) {
            Stack<Object> stack = new Stack<>();
            Element[] elements = node.getElements();

            for (Element el : elements) {
                if (el instanceof ElementConstantDouble ||
                    el instanceof ElementConstantInteger ||
                    el instanceof ElementString) {
                    stack.push(el.asText());
                } else if (el instanceof ElementVariable) {
                    ValueWrapper value = multistack.peek(el.asText());
                    stack.push(value.getValue());
                } else if (el instanceof ElementOperator) {
                    processOperator((ElementOperator) el, stack);
                } else if (el instanceof ElementFunction) {
                    processFunction((ElementFunction) el, stack);
                }
            }

            for (Object o : stack) {
                write(o.toString());
            }
        }

        /**
         * Processes the operator by popping two values from the stack and
         * performing an operation specified by the {@code ElementOperator}
         * parameter. The operation result is then pushed back onto the stack.
         *
         * @param op operator
         * @param stack stack from where to pop values and push result
         * @throws UnsupportedOperationException if the operation is not
         *         supported
         */
        private void processOperator(ElementOperator op, Stack<Object> stack) {
            String symbol = op.asText();

            ValueWrapper value1 = new ValueWrapper(stack.pop());
            Object value2 = stack.pop();

            if (symbol.equals("+")) {
                value1.increment(value2);
            } else if (symbol.equals("-")) {
                value1.decrement(value2);
            } else if (symbol.equals("*")) {
                value1.multiply(value2);
            } else if (symbol.equals("/")) {
                value1.divide(value2);
            } else {
                throw new UnsupportedOperationException(symbol);
            }

            stack.push(value1.getValue());
        }

        /**
         * Processes the function by using the stack and executing the function
         * specified by the {@code ElementFunction} parameter.
         *
         * @param fun function
         * @param stack stack from where to pop values and push result
         * @throws UnsupportedOperationException if the function is not
         *         supported
         */
        private void processFunction(ElementFunction fun, Stack<Object> stack) {
            String function = fun.asText().substring(1); // skip function mark

            if (function.equals("sin")) {
                Object x = stack.pop(); // x is expressed in degrees
                double xRadians = Math.toRadians(Double.parseDouble(x.toString()));
                double result = Math.sin(xRadians);
                stack.push(result);
            } else if (function.equals("decfmt")) {
                String f = (String) stack.pop();
                Object x = stack.pop();

                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                DecimalFormat df = new DecimalFormat(f, symbols);

                stack.push(df.format(x));
            } else if (function.equals("dup")) {
                stack.push(stack.peek());
            } else if (function.equals("swap")) {
                Object a = stack.pop();
                Object b = stack.pop();
                stack.push(a);
                stack.push(b);
            } else if (function.equals("setMimeType")) {
                String x = (String) stack.pop();
                requestContext.setMimeType(x);
            } else if (function.equals("paramGet")) {
                Object defValue = stack.pop();
                String name = (String) stack.pop();

                Object value = requestContext.getParameter(name);

                stack.push(value == null ? defValue : value);
            } else if (function.equals("pparamGet")) {
                Object defValue = stack.pop();
                String name = (String) stack.pop();

                Object value = requestContext.getPersistentParameter(name);

                stack.push(value == null ? defValue : value);
            } else if (function.equals("pparamSet")) {
                String name = (String) stack.pop();
                Object value = stack.pop();

                requestContext.setPersistentParameter(name, value.toString());
            } else if (function.equals("pparamDel")) {
                String name = (String) stack.pop();
                requestContext.removePersistentParameter(name);
            } else if (function.equals("tparamGet")) {
                Object defValue = stack.pop();
                String name = (String) stack.pop();

                Object value = requestContext.getTemporaryParameter(name);

                stack.push(value == null ? defValue : value);
            } else if (function.equals("tparamSet")) {
                String name = (String) stack.pop();
                Object value = stack.pop();

                requestContext.setTemporaryParameter(name, value.toString());
            } else if (function.equals("tparamDel")) {
                String name = (String) stack.pop();
                requestContext.removeTemporaryParameter(name);
            } else {
                throw new UnsupportedOperationException(function);
            }
        }

        @Override
        public void visitDocumentNode(DocumentNode node) {
            for (int i = 0, n = node.numberOfChildren(); i < n; i++) {
                node.getChild(i).accept(visitor);
            }
        }

        /**
         * Writes the specified <tt>text</tt> string to the request context.
         *
         * @param text text to be written to the request context
         * @throws RuntimeException if an I/O exception occurs
         */
        private void write(String text) {
            try {
                requestContext.write(text);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * Executes the document with this engine.
     */
    public void execute() {
        documentNode.accept(visitor);
    }

}
