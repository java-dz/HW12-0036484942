package hr.fer.zemris.java.custom.scripting.demo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;;

/**
 * A demonstration program that demonstrates the usage of the
 * {@linkplain SmartScriptParser} in combination with a
 * {@linkplain SmartScriptEngine}. There are four possible demonstration methods
 * in this program, each bonded to a certain <tt>.smscr</tt> file.
 *
 * @author Mario Bobic
 */
@SuppressWarnings("unused")
public class SmartScriptEngineDemo {

    /** Path of files that are read from the disk. */
    private static final String FILE_PATH = "./webroot/scripts/";

    /**
     * Program entry point.
     *
     * @param args not used in this demo
     */
    public static void main(String[] args) {
        osnovni();
//        zbrajanje();
//        brojPoziva();
//        fibonacci();
    }

    /**
     * First example that demonstrates the for-loop and some functions.
     */
    private static void osnovni() {
        String documentBody = readFromDisk("osnovni.smscr");
        Map<String, String> parameters = new HashMap<>();
        Map<String, String> persistentParameters = new HashMap<>();
        List<RCCookie> cookies = new ArrayList<>();

        new SmartScriptEngine(
            new SmartScriptParser(documentBody).getDocumentNode(),
            new RequestContext(System.out, parameters, persistentParameters, cookies)
        ).execute();
    }

    /**
     * Second example that demonstrates adding two numbers together using
     * parameters.
     */
    private static void zbrajanje() {
        String documentBody = readFromDisk("zbrajanje.smscr");
        Map<String, String> parameters = new HashMap<>();
        Map<String, String> persistentParameters = new HashMap<>();
        List<RCCookie> cookies = new ArrayList<>();

        parameters.put("a", "4");
        parameters.put("b", "2");

        new SmartScriptEngine(
            new SmartScriptParser(documentBody).getDocumentNode(),
            new RequestContext(System.out, parameters, persistentParameters, cookies)
        ).execute();
    }

    /**
     * Third example that demonstrates the number of times that the document was
     * called.
     */
    private static void brojPoziva() {
        String documentBody = readFromDisk("brojPoziva.smscr");
        Map<String, String> parameters = new HashMap<>();
        Map<String, String> persistentParameters = new HashMap<>();
        List<RCCookie> cookies = new ArrayList<>();

        persistentParameters.put("brojPoziva", "3");
        RequestContext rc = new RequestContext(System.out, parameters, persistentParameters, cookies);

        new SmartScriptEngine(
            new SmartScriptParser(documentBody).getDocumentNode(), rc
        ).execute();
        System.out.println();
        System.out.println("Vrijednost u mapi: " + rc.getPersistentParameter("brojPoziva"));
    }

    /**
     * Fourth example that demonstrates the fibonacci sequence.
     */
    private static void fibonacci() {
        String documentBody = readFromDisk("fibonacci.smscr");
        Map<String, String> parameters = new HashMap<>();
        Map<String, String> persistentParameters = new HashMap<>();
        List<RCCookie> cookies = new ArrayList<>();

        new SmartScriptEngine(
            new SmartScriptParser(documentBody).getDocumentNode(),
            new RequestContext(System.out, parameters, persistentParameters, cookies)
        ).execute();
    }

    /**
     * Returns a String containing text from file with the specified
     * <tt>path</tt>, or terminates the program if an exception occurs.
     *
     * @param path path of file whose text is to be returned
     * @return text of the file with the specified path
     */
    private static String readFromDisk(String path) {
        path = FILE_PATH + path;
        try {
            return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Exception while reading " + path + ": " + e.getMessage());
            System.exit(-1);
            return null;
        }
    }

}
