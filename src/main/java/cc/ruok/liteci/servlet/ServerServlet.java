package cc.ruok.liteci.servlet;

import cc.ruok.liteci.LiteCI;
import cc.ruok.liteci.User;
import cc.ruok.liteci.i18n.Format;
import cc.ruok.liteci.i18n.L;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerServlet extends HttpServlet {

    private static HashMap<String, String> htmlMap = new HashMap<>();
    private static String error = Format.language(getResourcesToString("error.html"));
    private static List<String> privateUrl = new ArrayList<>();

    public static void _init() {
        String html = getResourcesToString("index.html");
        htmlMap.put("/", L.format(html.replace("{include.overview}", getResourcesToString("overview.html"))));
        htmlMap.put("/setting/theme", L.format(html.replace("{include.overview}", getResourcesToString("setting-theme.html"))));
        htmlMap.put("/login", L.format(getResourcesToString("login.html")));
        htmlMap.put("/js/liteci.js", L.format(getResourcesToString("/js/liteci.js")));
        privateUrl.add("/");
        privateUrl.add("/setting/theme");
    }

    protected static InputStream getResources(String path) {
        return LiteCI.class.getResourceAsStream("/web/" + path);
    }

    protected static String getResourcesToString(String path) {
        try {
            return IOUtils.toString(getResources(path));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI();
        String html = htmlMap.get(path);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        if (checkPermission(path, req.getSession().getId())) {
            if (html != null) {
                resp.setStatus(200);
                resp.getWriter().println(html);
            } else {
                resp.setStatus(404);
                resp.getWriter().println(error);
            }
        } else {
            resp.setStatus(200);
            resp.getWriter().println(redirect("/login"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().startsWith("/api")) {
            new ApiServlet().doPost(req, resp);
        }
    }

    private boolean checkPermission(String url, String session) {
        if (!privateUrl.contains(url)) return true;
        User user = LiteCI.getOnlineUser(session);
        if (user == null) return false;
        user.active();
        return true;
    }

    private String redirect(String url) {
        return "<script>window.location.href = '" + url + "'</script>";
    }
}
