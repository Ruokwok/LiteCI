package cc.ruok.liteci.servlet;

import cc.ruok.liteci.HttpServer;
import cc.ruok.liteci.LiteCI;
import cc.ruok.liteci.config.ServerConfig;
import cc.ruok.liteci.config.UserConfig;
import cc.ruok.liteci.json.GuideJson;
import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class GuideServlet extends HttpServlet {

    private String html = ServerServlet.getResourcesToString("guide.html");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(200);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        resp.getWriter().println(html);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().equals("/save")) {
            GuideJson json = new Gson().fromJson(req.getReader().readLine(), GuideJson.class);
            if (json.port < 0 || json.port > 65535) {
                resp.getWriter().println("port");
                return;
            }
            if (json.title == null || json.title.isEmpty()) {
                resp.getWriter().println("title");
                return;
            }
            if (json.username == null || json.username.isEmpty()) {
                resp.getWriter().println("title");
                return;
            }
            if (json.password == null || json.password.isEmpty()) {
                resp.getWriter().println("title");
                return;
            }
            ServerConfig config = new ServerConfig();
            config.http_port = json.port;
            config.title = json.title;
            config.save();
            UserConfig userConfig = new UserConfig();
            userConfig.username = json.username;
            userConfig.password_hash = SecureUtil.sha256(SecureUtil.sha256(json.username + json.password));
            userConfig.save();
            resp.getWriter().println("success");
            new Thread(() -> {
                try {
                    LiteCI.guideServer.stop();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
