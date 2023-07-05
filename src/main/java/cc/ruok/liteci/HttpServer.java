package cc.ruok.liteci;

import cc.ruok.liteci.servlet.ApiServlet;
import cc.ruok.liteci.servlet.ServerServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import static cc.ruok.liteci.LiteCI.serverConfig;

public class HttpServer {

    private static HttpServer httpServer = new HttpServer();
    private Server server;

    private HttpServer() {
    }

    public static HttpServer getInstance() {
        return httpServer;
    }

    public void start() throws Exception {
        if (server != null && server.isRunning()) return;
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(ServerServlet.class, "/");
        server = new Server(serverConfig.http_port);
        server.setHandler(context);
        server.start();
//        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public void restart() throws Exception {
        stop();
        start();
    }

}
