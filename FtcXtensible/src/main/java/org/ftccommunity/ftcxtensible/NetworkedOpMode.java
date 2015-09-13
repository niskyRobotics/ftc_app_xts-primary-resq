package org.ftccommunity.ftcxtensible;

import org.ftccommunity.ftcxtensible.networking.http.HttpHelloWorldServer;

/**
 * The Networking components of the library; inherit this class to use the Networking components
 * in an {@link com.qualcomm.robotcore.eventloop.opmode.OpMode}
 *
 * @author David Sargent
 * @since 0.5
 */
public class NetworkedOpMode {
    private Thread serverThread;
    private HttpHelloWorldServer server;
    private RobotContext context;

    /**
     * Create the networked version of an OpMode
     */
    protected NetworkedOpMode(final RobotContext ctx) {
        super();
        context = ctx;
        server = new HttpHelloWorldServer(context);
        serverThread = new Thread(server);
    }

    /**
     * Handles the server bootstrap, starts the HTTP server in a new thread
     */
    public void startServer() {
        serverThread.start();
    }

    /**
     * Kills the HTTP server
     */
    public void stopServer() {
        serverThread.interrupt();
    }
}
