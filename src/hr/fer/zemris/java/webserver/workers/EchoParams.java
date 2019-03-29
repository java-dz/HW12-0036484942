package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Simply outputs back the parameters it obtained from URL by creating a HTML
 * table of the parameters.
 *
 * @author Mario Bobic
 */
public class EchoParams implements IWebWorker {

    @Override
    public void processRequest(RequestContext context) {
        context.setMimeType("text/html");

        String html = generateHTML(context);

        try {
            context.write(html);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the HTML code necessary for displaying parameter contents in a
     * HTML table and returns a string containing the code.
     *
     * @param context the request context
     * @return a string containing HTML code
     */
    private static String generateHTML(RequestContext context) {
        StringBuilder sb = new StringBuilder(
            "<html>\r\n" +
            "  <head>\r\n" +
            "    <title>Requested parameters</title>\r\n" +
            "  </head>\r\n" +
            "  <body>\r\n" +
            "    <h1>Requested parameters</h1>\r\n" +
            "    <table border='1'>\r\n"
        );

        for (String param : context.getParameterNames()) {
            sb    .append("      <tr><td>")
                .append(param)
                .append("</td><td>")
                .append(context.getParameter(param))
                .append("</td></tr>\r\n");
        }
        sb.append(
            "    </table>\r\n" +
            "  </body>\r\n" +
            "</html>\r\n"
        );

        return sb.toString();
    }

}
