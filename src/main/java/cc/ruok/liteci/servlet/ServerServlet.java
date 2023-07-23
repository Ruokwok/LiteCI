package cc.ruok.liteci.servlet;

import cc.ruok.liteci.LiteCI;
import cc.ruok.liteci.User;
import cc.ruok.liteci.config.ServerConfig;
import cc.ruok.liteci.i18n.HTML;
import cc.ruok.liteci.i18n.L;
import cc.ruok.liteci.project.Job;
import cc.ruok.liteci.project.Project;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ServerServlet extends HttpServlet {

    private static HashMap<String, HTML> htmlMap = new HashMap<>();
    private static String error = getResourcesToString("error.html");

    public static void _init() {
        String html = getResourcesToString("index.html");
        htmlMap.put("/", new HTML(L.format(HTML.res("overview", html)), 0));
        htmlMap.put("/setting/theme", new HTML(L.format(HTML.res("setting-theme", html)), 4));
        htmlMap.put("/setting/server", new HTML(L.format(HTML.res("setting-server", html)), 4));
        htmlMap.put("/job/dir", new HTML(L.format(HTML.res("dir", html)), 0));
        htmlMap.put("/job/job", new HTML(L.format(HTML.res("job", html)), 0));
        htmlMap.put("/build", new HTML(L.format(HTML.res("build", html)), 0));
        htmlMap.put("/builds", new HTML(L.format(HTML.res("builds", html)), 0));
        htmlMap.put("/edit/job", new HTML(L.format(HTML.res("edit-job", html)), 3));
        htmlMap.put("/new-job", new HTML(L.format(HTML.res("new-job", html)), 3));
        htmlMap.put("/login", new HTML(L.format(getResourcesToString("login.html")), -1));
        htmlMap.put("/js/liteci.js", new HTML(L.format(getResourcesToString("/js/liteci.js")), -1));
        htmlMap.put("/js/overview.js", new HTML(L.format(getResourcesToString("/js/overview.js")), 0));
        htmlMap.put("/js/setting-theme.js", new HTML(L.format(getResourcesToString("/js/setting-theme.js")), 4));
        htmlMap.put("/js/setting-server.js", new HTML(L.format(getResourcesToString("/js/setting-server.js")), 4));
        htmlMap.put("/js/new-job.js", new HTML(L.format(getResourcesToString("/js/new-job.js")), 3));
        htmlMap.put("/js/dir.js", new HTML(L.format(getResourcesToString("/js/dir.js")), 0));
        htmlMap.put("/js/job.js", new HTML(L.format(getResourcesToString("/js/job.js")), 0));
        htmlMap.put("/js/edit-job.js", new HTML(L.format(getResourcesToString("/js/edit-job.js")), 3));
        htmlMap.put("/js/build.js", new HTML(L.format(getResourcesToString("/js/build.js")), 0));
        htmlMap.put("/js/builds.js", new HTML(L.format(getResourcesToString("/js/builds.js")), 0));
    }

    protected static InputStream getResources(String path) {
        return LiteCI.class.getResourceAsStream("/web/" + path);
    }

    public static String getResourcesToString(String path) {
        try {
            return IOUtils.toString(getResources(path));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI();
        User user = LiteCI.getOnlineUser(req.getSession().getId());
        if (!domain(req)) return;

        if (req.getRequestURI().startsWith("/download/")) {
            if (user == null && LiteCI.serverConfig.anonymous.download) {
                new DownloadServlet().doGet(req, resp);
            } else if (user != null && LiteCI.serverConfig.register.download) {
                new DownloadServlet().doGet(req, resp);
            } else if (user != null && user.isAdmin()) {
                new DownloadServlet().doGet(req, resp);
            }
            return;
        } else if (req.getRequestURI().equals("/favicon.ico") || req.getRequestURI().equals("/logo.png")) {
            icon(req, resp);
            return;
        }

        String html = checkPermission(htmlMap.get(path), req.getSession().getId());
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        if (path.startsWith("/job")) {
            Project project = Project.getProject(path.substring(5));
            if (project != null && project.isDir()) {
                html = checkPermission(htmlMap.get("/job/dir"), req.getSession().getId());
                resp.setStatus(200);
            } else if (project != null && !project.isDir()) {
                html = checkPermission(htmlMap.get("/job/job"), req.getSession().getId());
                resp.setStatus(200);
            }
        } else if (path.startsWith("/edit")) {
                Project project = Project.getProject(path.substring(5));
                if (project != null && !project.isDir()) {
                    html = checkPermission(htmlMap.get("/edit/job"), req.getSession().getId());
                    resp.setStatus(200);
                }
        } else if (path.startsWith("/build/")) {
                String[] split = path.split("/");
                int id = Integer.parseInt(split[split.length - 1]);
                String _path = StrUtil.removeSuffix(path.substring(6), "/" + id);
                Project project = Project.getProject(_path);
                if (project instanceof Job) {
                    resp.setStatus(200);
                    html = checkPermission(htmlMap.get("/build"), req.getSession().getId());
                } else {
                    html = "";
                }
        }
        if (html == null) {
            resp.setStatus(200);
            resp.getWriter().println(checkPermission(htmlMap.get("/login"), req.getSession().getId()));
        } else if (html.isEmpty()) {
            resp.setStatus(404);
            resp.getWriter().println(error);
        } else {
            resp.setStatus(200);
            resp.getWriter().println(html);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!domain(req)) return;
        if (req.getRequestURI().startsWith("/api")) {
            new ApiServlet().doPost(req, resp);
        } else if (req.getRequestURI().startsWith("/webhook")) {
            new WebhookServlet().doPost(req, resp);
        }
    }

    public static String checkPermission(HTML html, String session) {
        if (html == null) return "";
        try {
            if (html.getLevel() < 0) return html.toString();
            User user = LiteCI.getOnlineUser(session);
            if (user == null) {
                ServerConfig.Secure anonymous = LiteCI.serverConfig.anonymous;
                if (html.getLevel() == 0 && anonymous.get_item) return html.toString();
                if (html.getLevel() == 1 && anonymous.download) return html.toString();
                if (html.getLevel() == 2 && anonymous.build) return html.toString();
                if (html.getLevel() == 3 && anonymous.set_item) return html.toString();
                if (html.getLevel() == 4 && anonymous.setting) return html.toString();
                if (html.getLevel() == 5 && anonymous.user) return html.toString();
            } else {
                if (user.isAdmin()) return html.toString();
                ServerConfig.Secure register = LiteCI.serverConfig.register;
                if (html.getLevel() == 0 && register.get_item) return html.toString();
                if (html.getLevel() == 1 && register.download) return html.toString();
                if (html.getLevel() == 2 && register.build) return html.toString();
                if (html.getLevel() == 3 && register.set_item) return html.toString();
                if (html.getLevel() == 4 && register.setting) return html.toString();
                if (html.getLevel() == 5 && register.user) return html.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private String redirect(String url) {
        return "<script>window.location.href = '" + url + "'</script>";
    }

    public void icon(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(200);
            if (req.getRequestURI().equals("/favicon.ico")) {
                InputStream inputStream = getResources("favicon.ico");
                IOUtils.write(inputStream.readAllBytes(), resp.getOutputStream());
            } else {
                InputStream inputStream = getResources("logo.png");
                IOUtils.write(inputStream.readAllBytes(), resp.getOutputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean domain(HttpServletRequest req) {
        if (LiteCI.serverConfig.domains == null || LiteCI.serverConfig.domains.isEmpty()) return true;
        return LiteCI.serverConfig.domains.contains(req.getHeader("Host"));
    }
}
