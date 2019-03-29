package hr.fer.zemris.java.webserver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;
import hr.fer.zemris.java.webserver.workers.IWebWorker;

/**
 * Server used to serve clients. Clients may connect to this server with the
 * following custom configuration in <tt>server.properties</tt> file:
 * <ul>
 * <li><tt>server.address</tt> is the address which the server listens. Use
 * <tt>127.0.0.1</tt> if server should be available only to the local host. Use
 * <tt>0.0.0.0</tt> if server should be available across the network.
 * <li><tt>server.port</tt> is the port number which the server listens. To make
 * sure port is available on all machines, use port numbers greater than
 * <tt>1023</tt>.
 * <li><tt>server.workerThreads</tt> is the amount of threads that should be
 * used by the thread pool to serve clients their requests.
 * <li><tt>server.documentRoot</tt> is a path to root directory from which the
 * server serves files.
 * <li><tt>server.mimeConfig</tt> is a path to configuration file that
 * associates extension to mime-types.
 * <li><tt>session.timeout</tt> the duration of user sessions in seconds. After
 * the client session has timed out, session map entry becomes invalid and new
 * security identifier (SID) is generated for the client upon creating a new
 * session.
 * <li><tt>server.workers</tt> is a path to configuration file that associates
 * URL to workers.
 * </ul>
 * <p>
 * This server is in close association with the {@linkplain SmartScriptParser}
 * and {@linkplain SmartScriptEngine} classes, which make use when client wants
 * to run a server script.
 * <p>
 * Client communication is established by using the {@linkplain RequestContext}
 * class, which defines useful HTML context like encoding, status code, status
 * text, mime type, cookies, header etc.
 *
 * @author Mario Bobic
 */
public class SmartHttpServer {

    /** Fully-qualified name of workers package with a dot on the end. */
    private static final String WORKERS_PACKAGE =
            IWebWorker.class.getPackage().getName() + '.';

    /** The date-time formatter used for tracking client sockets. */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    /** Address which the server listens. */
    private String address;
    /** Port number which the server listens. */
    private int port;
    /** Session timeout in seconds. */
    private int sessionTimeout;

    /** Supported mime types. */
    private Map<String, String> mimeTypes = new HashMap<>();
    /** Document root of the server. */
    private Path documentRoot;

    /** Amount of threads used by the thread pool. */
    private int workerThreads;
    /** The non-stop working server thread. */
    private ServerThread serverThread;
    /** Thread pool of client workers. */
    private ExecutorService threadPool;

    /** The session map cleaner. */
    private CleanupJob cleanupJob;
    /** The scheduled executor service for cleanup job. */
    private ScheduledExecutorService scheduledExecutor;

    /** Map of {@linkplain IWebWorker}s for running applications. */
    private Map<String, IWebWorker> workersMap = new HashMap<>();

    /** Stored sessions. */
    private Map<String, SessionMapEntry> sessions = new HashMap<>();
    /** Session random SID generator. */
    private Random sessionRandom = new Random();

    /**
     * Constructs an instance of {@code SmartHttpServer} and initializes
     * settings from property files starting with the specified
     * <tt>configFileName</tt>.
     *
     * @param configFileName property file from where to load settings
     * @throws IllegalArgumentException if the config file is not valid
     */
    public SmartHttpServer(String configFileName) {
        Properties properties = loadProperties(configFileName);

        address = properties.getProperty("server.address");
        port = Integer.parseInt(properties.getProperty("server.port"));
        workerThreads = Integer.parseInt(properties.getProperty("server.workerThreads"));
        sessionTimeout = Integer.parseInt(properties.getProperty("session.timeout"));
        documentRoot = Paths.get(properties.getProperty("server.documentRoot"))
                .toAbsolutePath().normalize();

        initializeMimeTypes(properties);
        initializeWorkers(properties);
    }

    /**
     * Initializes the mime types by obtaining the <tt>server.mimeConfig</tt>
     * property from the specified <tt>properties</tt>.
     *
     * @param properties properties from where the mimeConfig property is obtained
     * @throws IllegalArgumentException if <tt>server.mimeConfig</tt> is not valid
     */
    private void initializeMimeTypes(Properties properties) {
        String mimePath = properties.getProperty("server.mimeConfig");
        Properties mimeProperties = loadProperties(mimePath);

        mimeProperties.forEach((k, v) -> {
            mimeTypes.put(k.toString(), v.toString());
        });
    }

    /**
     * Initializes the workers by obtaining the <tt>server.workers</tt> property
     * from the specified <tt>properties</tt>.
     * <p>
     * If there are multiple lines with the same path, an exception is
     * <strong>not</strong> thrown, but the value from the last line is taken.
     *
     * @param properties properties from where the workers property is obtained
     * @throws IllegalArgumentException if <tt>server.workers</tt> is not valid
     */
    private void initializeWorkers(Properties properties) {
        String workersPath = properties.getProperty("server.workers");
        Properties workerProperties = loadProperties(workersPath);

        workerProperties.forEach((path, fqcn) -> {
            try {
                IWebWorker worker = getWebWorker((String) fqcn);
                workersMap.put((String) path, worker);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        });
    }

    /**
     * Loads the properties of a file specified by the <tt>path</tt> parameter and
     * returns an instance of {@code Properties} object.
     *
     * @param path path of the property file
     * @return {@code Properties} object containing file properties
     * @throws IllegalArgumentException if an I/O error occurs, if a security
     *         exception occurs, if the path string cannot be converted to a
     *         {@code Path} or if the file contains a malformed Unicode escape
     *         sequence
     */
    private static Properties loadProperties(String path) {
        Properties properties = new Properties();

        try {
            properties.load(Files.newInputStream(Paths.get(path)));
        } catch (Exception e) {
            throw new IllegalArgumentException("Error loading file " + path, e);
        }

        return properties;
    }

    /**
     * Returns a web worker with the specified fully-qualified class name.
     * Throws {@linkplain IOException} if the class can not be found or the
     * class can not be instantiated.
     *
     * @param className fully-qualified class name
     * @return an instance of {@code IWebWorker}
     * @throws IOException if there are problems loading class file
     */
    private IWebWorker getWebWorker(String className) throws IOException {
        try {
            Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(className);
            return (IWebWorker) referenceToClass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IOException(e);
        }
    }

    /**
     * Starts the server thread if it is not already running. Initializes and
     * starts a new fixed thread pool.
     */
    protected synchronized void start() {
        if (serverThread == null) {
            serverThread = new ServerThread();
            cleanupJob = new CleanupJob(sessions);

            threadPool = Executors.newFixedThreadPool(workerThreads);
            serverThread.start();

            scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutor.scheduleWithFixedDelay(cleanupJob, 5, 5, TimeUnit.MINUTES);

            System.out.println("Server started at " + FORMATTER.format(LocalDateTime.now()));
        }
    }

    /**
     * Signals the server to stop running. Shuts down the thread pool.
     */
    protected synchronized void stop() {
        if (serverThread != null) {
            serverThread.stopThread();
            threadPool.shutdown();
            scheduledExecutor.shutdown();

            System.out.println("Server stopped at " + FORMATTER.format(LocalDateTime.now()));
        }
    }

    /**
     * This class represents a server-running thread. Its {@linkplain #run()}
     * method is overridden to create a {@code ServerSocket} that
     * {@linkplain ServerSocket#accept() accepts} connections, creates instances
     * of {@linkplain ClientWorker} jobs and submits them to the thread pool in
     * order for the clients to be served.
     *
     * @author Mario Bobic
     */
    protected class ServerThread extends Thread {

        /** Indicates if the server should be active or not. */
        private boolean active = true;

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(address, port));
                serverSocket.setSoTimeout(5000); // timeout for server shutdown

                while (active) {
                    try {
                        acceptClient(serverSocket);
                    } catch (SocketTimeoutException e) {
                        continue;
                    }
                }

                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Accepts the client and sets its timeout to <tt>sessionTimeout</tt>.
         * <p>
         * This method <strong>blocks</strong> the thread while waiting for a
         * client socket, or throws a {@linkplain SocketTimeoutException} if the
         * specified <tt>serverSocket</tt> has set a timeout.
         *
         * @param serverSocket server socket that accepts clients
         * @throws SocketTimeoutException if the server socket has timed out
         * @throws IOException if an I/O or other socket exception occurs
         */
        private void acceptClient(ServerSocket serverSocket) throws IOException {
            Socket clientSocket = serverSocket.accept();
            clientSocket.setSoTimeout(sessionTimeout);
            ClientWorker cw = new ClientWorker(clientSocket);
            threadPool.submit(cw);

            System.out.println("Accepted " + clientSocket + " at " + FORMATTER.format(LocalDateTime.now()));
        }

        /**
         * Stops the server.
         */
        public void stopThread() {
            active = false;
        }
    }

    /**
     * This class is a {@linkplain Runnable runnable} job that serves clients
     * their requests.
     *
     * @author Mario Bobic
     */
    private class ClientWorker implements Runnable {
        /** The client socket. */
        private Socket csocket;
        /** Input stream from where the request is read. */
        private PushbackInputStream istream;
        /** Output stream for serving the client. */
        private OutputStream ostream;

        /** HTTP version. Replaced with client's request version. */
        private String version = "HTTP/1.1";
        /** HTTP method. */
        private String method;
        /** HTTP domain. Replaced with client's request domain in session. */
        private String domain = address;

        /** Parameters fetched from the request. */
        private Map<String, String> params = new HashMap<>();
        /** Persistent parameters. */
        private Map<String, String> permParams = null;
        /** Output cookies. */
        private List<RCCookie> outputCookies = new ArrayList<>();
        /** Client SID. */
        @SuppressWarnings("unused")
        private String SID;

        /**
         * Constructs an instance of {@code ClientWorker} with the specified
         * client socket.
         *
         * @param csocket client socket to be used to serve the client
         */
        public ClientWorker(Socket csocket) {
            this.csocket = csocket;
        }

        @Override
        public void run() {
            try {

                // Obtain input stream and output stream
                istream = new PushbackInputStream(csocket.getInputStream());
                ostream = csocket.getOutputStream();

                // Read complete request header from client
                List<String> request = readRequest();
                if (request.isEmpty()) {
                    sendError(400, "Bad request");
                    return;
                }

                String firstLine = request.get(0);

                // Extract method, requestedPath, version from firstLine
                String[] args = firstLine.split(" ");
                if (args.length != 3) {
                    sendError(400, "Bad request");
                    return;
                }

                method = args[0].toUpperCase();
                if (!method.equals("GET")) {
                    sendError(405, "Method Not Allowed");
                    return;
                }

                version = args[2].toUpperCase();
                if (!version.equals("HTTP/1.0") && !version.equals("HTTP/1.1")) {
                    sendError(505, "HTTP Version Not Supported");
                    return;
                }

                checkSession(request);


                String requestedPathStr = args[1];

                // Extract path and paramString
                String[] pathArgs = requestedPathStr.split("\\?");
                if (pathArgs.length == 2) {
                    String paramString = pathArgs[1];
                    parseParameters(paramString);
                } else if (pathArgs.length != 1) {
                    sendError(400, "Bad request");
                    return;
                }

                String path = pathArgs[0];

                // Create context
                RequestContext rc = new RequestContext(ostream, params, permParams, outputCookies);

                if (path.equals("/")) {
//                    path = "/index.html"; // does not redirect URL
                    rc.write("<meta http-equiv=\"refresh\" content=\"0; url=index.html\" />");
                    return;
                }

                // Look for IWebWorker object
                if (workersMap.containsKey(path)) {
                    workersMap.get(path).processRequest(rc);
                    return;
                }
                if (path.startsWith("/ext/")) {
                    String className = WORKERS_PACKAGE + path.substring(5);
                    if (!classExists(className)) {
                        sendError(404, "Not found");
                        return;
                    }

                    getWebWorker(className).processRequest(rc);
                    return;
                }

                // Resolve path with respect to documentRoot
                Path requestedPath = Paths.get(documentRoot.toString(), path).toAbsolutePath().normalize();
                // If requestedPath is not below documentRoot, return response status 403 forbidden
                if (!requestedPath.startsWith(documentRoot)) {
                    sendError(403, "Forbidden");
                    return;
                }

                // Check if requestedPath exists, is a regular file and is readable
                if (!Files.isRegularFile(requestedPath) || !Files.isReadable(requestedPath)) {
                    sendError(404, "Not found");
                    return;
                }

                // Extract file extension
                int dotIndex = requestedPath.toString().lastIndexOf('.');
                String extension = dotIndex != -1 ?
                        requestedPath.toString().substring(dotIndex+1) : null;

                if (extension != null && extension.equals("smscr")) {
                    parseSmscr(requestedPath);
                    return;
                }

                // Find in mimeTypes map appropriate mimeType for current file extension
                String mimeType = mimeTypes.get(extension);

                // If no mime type found, assume application/octet-stream
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }

                // Set mime-type; set status to 200; later set content-length
                rc.setMimeType(mimeType);
                rc.setStatusCode(200);

                // This will generate header and send file bytes to client
                BufferedInputStream in = new BufferedInputStream(Files.newInputStream(requestedPath));
                rc.setContentLength(in.available());

                int len = 0;
                byte[] bytes = new byte[1024];
                while ((len = in.read(bytes)) > 0) {
                    rc.write(bytes, 0, len);
                }
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                try { if (!csocket.isClosed()) csocket.close(); } catch (IOException ignorable) {}
            }
        }

        /**
         * Returns <tt>true</tt> if a class with the specified fully-qualified class name
         * exists. <tt>False</tt> otherwise.
         *
         * @param className fully-qualified class name
         * @return true if class exists, false otherwise
         */
        private boolean classExists(String className) {
            try {
                Class.forName(className);
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        /**
         * Reads request from the client scanning for next lines until an empty
         * line. Returns lines that were read as a list of strings.
         *
         * @return list of strings containing lines that were read
         */
        private List<String> readRequest() {
            List<String> lines = new ArrayList<>();

            @SuppressWarnings("resource")
            Scanner sc = new Scanner(istream, "UTF-8");

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.isEmpty()) break;
                lines.add(line);
            }

            return lines;
        }

        /**
         * Parses the parameters and puts them into the <tt>params</tt> map.
         *
         * @param paramString string of parameters
         * @throws RuntimeException if an invalid parameter is given
         */
        private void parseParameters(String paramString) {
            String[] parameters = paramString.split("\\&");

            for (String parameter : parameters) {
                String[] keyValue = parameter.split("=");
                if (keyValue.length == 1) {
                    params.put(keyValue[0], null);
                } else {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }

        /**
         * Parses the <tt>.smscr</tt> file with a {@linkplain SmartScriptParser}
         * and runs it through a {@linkplain SmartScriptEngine}.
         *
         * @param path path of the document
         * @throws IOException if an I/O error occurs while reading from the stream
         */
        private void parseSmscr(Path path) throws IOException {
            String documentBody = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

            new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(ostream, params, permParams, outputCookies)
            ).execute();
        }

        /**
         * Sends an error with the specified <tt>statusCode</tt> and
         * <tt>statusText</tt> to the client output stream.
         *
         * @param statusCode error status code
         * @param statusText error status text
         * @throws IOException if an I/O error occurs
         */
        private void sendError(int statusCode, String statusText) throws IOException {
            ostream.write(
                (version+" "+statusCode+" "+statusText+"\r\n"+
                "Server: SmartHttpServer\r\n"+
                "Content-Type: text/html; charset=UTF-8\r\n"+
                "Connection: close\r\n"+
                "\r\n").getBytes(StandardCharsets.US_ASCII)
            );

            // Write message to user
            ostream.write(
                ("<html>\r\n"+
                 "  <head><title>"+statusCode+" "+statusText+"</title></head>\r\n"+
                 "  <body bgcolor=\"#cc9999\">\r\n"+
                 "    <p><b>"+statusCode+"</b> "+statusText+"</p>\r\n"+
                 "    <hr/>"+
                 "  </body>\r\n"+
                 "</html>\r\n").getBytes(StandardCharsets.UTF_8)
            );

            ostream.flush();
        }

        /**
         * <strong>Synchronized</strong>. Checks for active sessions in the
         * <tt>sessions</tt> map which is done in this order:
         * <ol>
         * <li>If the client <tt>request</tt> contains <tt>Cookie</tt> line, the
         * cookie value associated with the cookie name <tt>sid</tt> is searched
         * for.
         * <li>If a cookie with the client-specified <tt>sid</tt> does not exist
         * in the sessions map, a new one is generated and stored into the map.
         * </ol>
         * The cookie's validity time is renewed and its timeout is specified by
         * the <tt>sessionTimeout</tt> variable.
         *
         * @param request client request
         */
        private void checkSession(List<String> request) {
            synchronized (serverThread) {
                String sidCandidate = null;

                for (String line : request) {
                    if (line.startsWith("Cookie:")) {
                        sidCandidate = processCookie(line);
                    }
                    if (line.startsWith("Host:")) {
                        domain = processHost(line);
                    }
                }

                SessionMapEntry entry = sessions.get(sidCandidate);

                if (sidCandidate == null) {
                    sidCandidate = createUniqueSID();
                }
                if (entry == null || !entry.isValid()) {
                    entry = storeSID(sidCandidate);
                }

                SID = sidCandidate;
                entry.validUntil = new Date().getTime() + 1000*sessionTimeout;

                permParams = entry.map;
            }
        }

        /**
         * Processes the cookie line and returns the cookie value associated
         * with the cookie name <tt>sid</tt> or <tt>null</tt> if that cookie
         * name was not found.
         *
         * @param cookiesLine cookie line to be processed
         * @return value associated with sid cookie or null if sid not found
         */
        private String processCookie(String cookiesLine) {
            String[] cookies = cookiesLine.replace("Cookie:", "").split(";");

            for (String cookie : cookies) {
                String[] nameValue = cookie.split("=");

                String cookieName = nameValue[0].trim();
                String cookieValue = nameValue[1].trim().replace("\"", "");

                if (cookieName.equals("sid")) {
                    return cookieValue;
                }
            }

            return null;
        }

        /**
         * Processes the host line and returns the domain of this host. This
         * host can be requested as a local host in two ways; by writing
         * <tt>127.0.0.1</tt> as an URL address or by entering
         * <tt>localhost</tt>; or can even be accessed through WAN by specifying
         * the external IP address of the router.
         *
         * @param hostLine host line to be processed
         * @return the domain of this host
         */
        private String processHost(String hostLine) {
            return hostLine.replace("Host:", "").split(":")[0].trim();
        }

        /** Alphabet used for SID generation. */
        private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        /** SID length. */
        private static final int SID_LEN = 20;

        /**
         * Creates and returns a new unique SID.
         *
         * @return a new unique SID
         */
        private String createUniqueSID() {
            char[] alphabet = ALPHABET.toCharArray();

            StringBuilder sid = new StringBuilder();
            for (int i = 0; i < SID_LEN; i++) {
                sid.append(alphabet[sessionRandom.nextInt(alphabet.length)]);
            }

            return sid.toString();
        }

        /**
         * Creates a session map entry with the specified <tt>sid</tt>, stores
         * it into the <tt>sessions</tt> map and adds an <tt>RCCookie</tt> to
         * the <tt>outputCookies</tt> list.
         *
         * @param sid sid to be stored
         * @return entry with the specified sid as key and
         *         <tt>date.now() + sessionTimeout</tt> as value
         */
        private SessionMapEntry storeSID(String sid) {
            long validUntil = new Date().getTime() + 1000*sessionTimeout;

            SessionMapEntry entry = new SessionMapEntry(sid, validUntil);
            sessions.put(sid, entry);

            RCCookie cookie = new RCCookie("sid", sid, domain, "/", sessionTimeout);
            cookie.setHttpOnly(true);
            outputCookies.add(cookie);

            return entry;
        }
    }

    /**
     * This class represents a session entry which is used for the
     * <tt>sessions</tt> map.
     *
     * @author Mario Bobic
     */
    private static class SessionMapEntry {
        /** Session SID. */
        @SuppressWarnings("unused")
        String sid;

        /** Time (in milliseconds) until this session entry is valid. */
        long validUntil;

        /** Concurrent map with SID as key and parameter as value. */
        Map<String, String> map = new ConcurrentHashMap<>();

        /**
         * Constructs an instance of {@code SessionMapEntry} with the specified
         * sid and validity end-time expressed in milliseconds. The
         * <tt>validUntil</tt> variable is most commonly determined by adding
         * <tt>date.now()</tt> and <tt>sessionTimeout</tt> together.
         *
         * @param sid session sid
         * @param validUntil validity end-time, in milliseconds
         */
        public SessionMapEntry(String sid, long validUntil) {
            this.sid = sid;
            this.validUntil = validUntil;
        }

        /**
         * Returns true if this session entry is valid, or more formally if
         * <tt>date.now() < validUntil</tt>. False otherwise.
         *
         * @return true if this session is valid, false otherwise
         */
        public boolean isValid() {
            return new Date().getTime() < validUntil;
        }
    }

    /**
     * Overrides the <tt>run</tt> method to clean up a map of invalid sessions.
     *
     * @author Mario Bobic
     */
    private static class CleanupJob implements Runnable {

        /** Sessions to be cleaned up. */
        private Map<String, SessionMapEntry> sessions;

        /**
         * Constructs an instance of {@code CleanupThread} with the specified
         * map of sessions to be cleaned up.
         *
         * @param sessions map of sessions to be cleaned up
         */
        public CleanupJob(Map<String, SessionMapEntry> sessions) {
            this.sessions = sessions;
        }

        @Override
        public void run() {
            int count = 0;

            Iterator<Map.Entry<String, SessionMapEntry>> iter = sessions.entrySet().iterator();
            while (iter.hasNext()) {
                SessionMapEntry entry = iter.next().getValue();
                if (!entry.isValid()) {
                    iter.remove();
                    count++;
                }
            }

            if (count > 0)
                System.out.println("Removed " + count + " invalid session entries.");
        }
    }

    /**
     * Program entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Expected one argument: path to server.properties file");
            return;
        }

        SmartHttpServer server = new SmartHttpServer(args[0]);
        server.start();
    }

}
