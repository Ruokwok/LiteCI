package cc.ruok.liteci.servlet;

import cc.ruok.liteci.LiteCI;
import cc.ruok.liteci.User;
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
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ApiHandler handler = map.get(req.getRequestURI());
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
}
