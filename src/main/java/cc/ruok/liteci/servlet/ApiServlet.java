package cc.ruok.liteci.servlet;

import cc.ruok.liteci.LiteCI;
import cc.ruok.liteci.project.Project;
import cc.ruok.liteci.User;
import cc.ruok.liteci.i18n.L;
import cc.ruok.liteci.json.DialogJson;
import cc.ruok.liteci.json.Json;
import cn.hutool.crypto.SecureUtil;
import com.github.mervick.aes_everywhere.Aes256;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;

public class ApiServlet extends ServerServlet {

    private static HashMap<String, ApiHandler> map = new HashMap<>();

    public static void _init() {
        map.put("/api/login", ApiServlet::login);
        map.put("/api/jobs", ApiServlet::getJobs);
        map.put("/api1/setting/theme/get", ApiServlet::getTheme);
        map.put("/api1/setting/theme/set", ApiServlet::setTheme);
        map.put("/api1/create/dir", ApiServlet::createDir);
        map.put("/api1/create/job", ApiServlet::createJob);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ApiHandler handler = map.get(req.getRequestURI());
        resp.setCharacterEncoding("UTF-8");
        if (handler != null) handler.handler(req.getReader().readLine(), req, resp);
    }

    public static void login(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json json = new Gson().fromJson(str, Json.class);
        try {
            User user = new User(json.params.get("username"));
            String key = Aes256.decrypt(json.params.get("key"), SecureUtil.md5(user.getConfig().password_hash));
            resp.setStatus(200);
            if (key.equals("login")) {
                Json j = new Json();
                j.params.put("user", user.getConfig().username);
                j.params.put("status", "success");
                resp.getWriter().println(j);
                user.session = req.getSession().getId();
                LiteCI.addOnlineUser(user);
            }
        } catch (Exception e) {
            Json j = new Json();
            j.params.put("status", "failed");
            resp.getWriter().println(j);
        }
    }

    public static void getTheme(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json json = new Json();
        resp.setStatus(200);
        json.params.put("theme", LiteCI.serverConfig.theme);
        json.params.put("accent", LiteCI.serverConfig.accent);
        json.params.put("title", LiteCI.serverConfig.title);
        resp.getWriter().println(json);
    }

    public static void setTheme(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json json = new Gson().fromJson(str, Json.class);
        LiteCI.serverConfig.title = json.params.get("title");
        LiteCI.serverConfig.theme = json.params.get("theme");
        LiteCI.serverConfig.accent = json.params.get("accent");
        LiteCI.serverConfig.save();
        LiteCI.init();
        resp.getWriter().println(new DialogJson(L.get("set.save.success")));
    }

    public static void createDir(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json json = new Gson().fromJson(str, Json.class);
        String s = Project.createDir(json.params.get("path"), json.params.get("name"));
        if (s != null) {
            resp.getWriter().println(new DialogJson(s));
        } else {
            json.params.put("status", "success");
            resp.getWriter().println(json);
        }
    }

    public static void createJob(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json json = new Gson().fromJson(str, Json.class);
        String s = Project.createJob(json.params.get("path"), json.params.get("name"));
        if (s != null) {
            resp.getWriter().println(new DialogJson(s));
        } else {
            json.params.put("status", "success");
            resp.getWriter().println(json);
        }
    }

    public static void getJobs(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json json = new Gson().fromJson(str, Json.class);

    }
}
