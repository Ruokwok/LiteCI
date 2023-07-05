package cc.ruok.liteci;

import cc.ruok.liteci.servlet.GuideServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import static cc.ruok.liteci.LiteCI.serverConfig;

public class GuideServer {

    private Server server;

    public GuideServer(int port) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(GuideServlet.class, "/");
        server = new Server(port);
        server.setHandler(context);
    }

    public void start() throws Exception {
        server.start();
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

}
