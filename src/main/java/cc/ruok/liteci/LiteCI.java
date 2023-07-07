package cc.ruok.liteci;

import cc.ruok.liteci.config.Config;
import cc.ruok.liteci.config.ServerConfig;
import cc.ruok.liteci.i18n.L;
import cc.ruok.liteci.project.Project;
import cc.ruok.liteci.servlet.ApiServlet;
import cc.ruok.liteci.servlet.ServerServlet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LiteCI {

    public static ServerConfig serverConfig;
    public static GuideServer guideServer;
    public static Map<String, User> userMap = new HashMap<>();
    public static final File JOBS = new File("jobs");
    public static final File WORKSPACE = new File("workspace");

    public static void main(String[] args) throws Exception {
//        BasicConfigurator.configure();
        File users = new File("users");
        if (!users.exists()) users.mkdir();
        if (!JOBS.exists()) JOBS.mkdir();
        if (!WORKSPACE.exists()) WORKSPACE.mkdir();
        if (!ServerConfig.file.exists()) {
            guideServer = new GuideServer(80);
            guideServer.start();
        }
        serverConfig = Config.loadServerConfig();
        init();
        HttpServer server = HttpServer.getInstance();
        server.start();
    }

    public static void init() {
        try {
            L.load("chs");
            ServerServlet._init();
            ApiServlet._init();
            Project.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addOnlineUser(User user) {
        user.active();
        userMap.put(user.session, user);
    }

    public static User getOnlineUser(String session) {
        return userMap.get(session);
    }

}
