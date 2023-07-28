package cc.ruok.liteci;

import cc.ruok.liteci.servlet.ApiServlet;
import cc.ruok.liteci.servlet.ServerServlet;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.net.URL;

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

        if (serverConfig.ssl) {
            HttpConfiguration config = new HttpConfiguration();
            config.setSecureScheme("https");
            config.setSecurePort(443);
            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStorePath("ssl/keystore.jks");
            sslContextFactory.setKeyStorePassword(serverConfig.keystore_password);
            sslContextFactory.setKeyManagerPassword(serverConfig.keystore_password);
            ServerConnector httpsConnector = new ServerConnector(server,
                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(config));
            httpsConnector.setPort(443);
            server.addConnector(httpsConnector);
        }

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
