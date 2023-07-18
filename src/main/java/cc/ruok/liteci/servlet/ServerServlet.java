package cc.ruok.liteci.servlet;

import cc.ruok.liteci.LiteCI;
import cc.ruok.liteci.User;
import cc.ruok.liteci.i18n.Format;
import cc.ruok.liteci.i18n.L;
import cc.ruok.liteci.project.Job;
import cc.ruok.liteci.project.Project;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

import java.io.File;
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
        htmlMap.put("/", L.format(Format.res("overview", html)));
        htmlMap.put("/setting/theme", L.format(Format.res("setting-theme", html)));
        htmlMap.put("/setting/build", L.format(Format.res("setting-build", html)));
        htmlMap.put("/job/dir", L.format(Format.res("dir", html)));
        htmlMap.put("/job/job", L.format(Format.res("job", html)));
        htmlMap.put("/build", L.format(Format.res("build", html)));
        htmlMap.put("/edit/job", L.format(Format.res("edit-job", html)));
        htmlMap.put("/new-job", L.format(Format.res("new-job", html)));
        htmlMap.put("/login", L.format(getResourcesToString("login.html")));
        htmlMap.put("/js/liteci.js", L.format(getResourcesToString("/js/liteci.js")));
        htmlMap.put("/js/overview.js", L.format(getResourcesToString("/js/overview.js")));
        htmlMap.put("/js/setting-theme.js", L.format(getResourcesToString("/js/setting-theme.js")));
        htmlMap.put("/js/setting-build.js", L.format(getResourcesToString("/js/setting-build.js")));
        htmlMap.put("/js/new-job.js", L.format(getResourcesToString("/js/new-job.js")));
        htmlMap.put("/js/dir.js", L.format(getResourcesToString("/js/dir.js")));
        htmlMap.put("/js/job.js", L.format(getResourcesToString("/js/job.js")));
        htmlMap.put("/js/edit-job.js", L.format(getResourcesToString("/js/edit-job.js")));
        htmlMap.put("/js/build.js", L.format(getResourcesToString("/js/build.js")));
        privateUrl.add("/");
        privateUrl.add("/setting/theme");
        privateUrl.add("/setting/build");
        privateUrl.add("/new-job");
        privateUrl.add("/job");
        privateUrl.add("/edit");
        privateUrl.add("/build");
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
        String html = htmlMap.get(path);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        if (path.startsWith("/job")) {
            if (checkPermission("/job", req.getSession().getId())) {
                Project project = Project.getProject(path.substring(5));
                if (project != null && project.isDir()) {
                    String jhtml = htmlMap.get("/job/dir");
                    resp.setStatus(200);
                    resp.getWriter().println(jhtml);
                } else if (project != null && !project.isDir()) {
                    String jhtml = htmlMap.get("/job/job");
                    resp.setStatus(200);
                    resp.getWriter().println(jhtml);
                }
            } else  {
                resp.setStatus(200);
                resp.getWriter().println(redirect("/login"));
            }
        } else if (path.startsWith("/edit")) {
            if (checkPermission("/edit", req.getSession().getId())) {
                Project project = Project.getProject(path.substring(5));
                if (project != null && !project.isDir()) {
                    String jhtml = htmlMap.get("/edit/job");
                    resp.setStatus(200);
                    resp.getWriter().println(jhtml);
                }
            } else  {
                resp.setStatus(200);
                resp.getWriter().println(redirect("/login"));
            }
        } else if (path.startsWith("/build/")) {
            if (checkPermission("/build", req.getSession().getId())) {
                String[] split = path.split("/");
                int id = Integer.parseInt(split[split.length - 1]);
                String _path = StrUtil.removeSuffix(path.substring(6), "/" + id);
                Project project = Project.getProject(_path);
                if (project instanceof Job) {
                    resp.setStatus(200);
                    resp.getWriter().println(htmlMap.get("/build"));
                    return;
                }
                resp.setStatus(404);
                resp.getWriter().println(error);
            } else {
                resp.setStatus(200);
                resp.getWriter().println(redirect("/login"));
            }
        } else if (checkPermission(path, req.getSession().getId())) {
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
        } else if (req.getRequestURI().startsWith("/webhook")) {
            new WebhookServlet().doPost(req, resp);
        }
    }

    public static boolean checkPermission(String url, String session) {
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
