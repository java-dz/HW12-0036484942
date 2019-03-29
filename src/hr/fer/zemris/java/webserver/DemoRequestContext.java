package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

/**
 * This demonstration program generates three files in the root directory of the
 * project. These files contain header and some body written by an instance of
 * {@linkplain RequestContext} class. The first example file is encoded with
 * <tt>ISO-8859-2</tt> encoding, while second and third example files are
 * encoded with <tt>UTF-8</tt> encoding.
 *
 * @author Mario Bobic
 */
public class DemoRequestContext {

    /**
     * Program entry point.
     *
     * @param args not used in this demo
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        demo1("primjer1.txt", "ISO-8859-2");
        demo1("primjer2.txt", "UTF-8");
        demo2("primjer3.txt", "UTF-8");
    }

    /**
     * Creates a file output stream and writes plain text request to the file
     * with the specified <tt>filePath</tt> using the specified
     * <tt>encoding</tt>.
     *
     * @param filePath path of the file to be created
     * @param encoding encoding to be used
     * @throws IOException if an I/O error occurs
     */
    private static void demo1(String filePath, String encoding) throws IOException {
        OutputStream os = Files.newOutputStream(Paths.get(filePath));

        RequestContext rc = new RequestContext(os, new HashMap<>(), new HashMap<>(), new ArrayList<>());
        rc.setEncoding(encoding);
        rc.setMimeType("text/plain");
        rc.setStatusCode(205);
        rc.setStatusText("Idemo dalje");

        // Only at this point will header be created and written...
        rc.write("Čevapčići i Šiščevapčići.");

        os.close();
    }

    /**
     * Creates a file output stream and writes plain text request to the file
     * with the specified <tt>filePath</tt> using the specified
     * <tt>encoding</tt>.
     * <p>
     * Adds {@link RCCookie cookies} too.
     *
     * @param filePath path of the file to be created
     * @param encoding encoding to be used
     * @throws IOException if an I/O error occurs
     */
    private static void demo2(String filePath, String encoding) throws IOException {
        OutputStream os = Files.newOutputStream(Paths.get(filePath));

        RequestContext rc = new RequestContext(os, new HashMap<>(), new HashMap<>(), new ArrayList<>());
        rc.setEncoding(encoding);
        rc.setMimeType("text/plain");
        rc.setStatusCode(205);
        rc.setStatusText("Idemo dalje");
        rc.addRCCookie(new RCCookie("korisnik", "perica", "127.0.0.1", "/", 3600));

        rc.addRCCookie(new RCCookie("zgrada", "B4", null, "/", null));

        // Only at this point will header be created and written...
        rc.write("Čevapčići i Šiščevapčići.");

        os.close();
    }

}
