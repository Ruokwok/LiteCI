package cc.ruok.liteci.servlet;

import cc.ruok.liteci.BuildQueue;
import cc.ruok.liteci.LiteCI;
import cc.ruok.liteci.config.JobConfig;
import cc.ruok.liteci.json.JobJson;
import cc.ruok.liteci.json.JobListJson;
import cc.ruok.liteci.project.Dir;
import cc.ruok.liteci.project.Job;
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
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ApiServlet extends ServerServlet {

    private static HashMap<String, ApiHandler> map = new HashMap<>();

    public static void _init() {
        map.put("/api/login", ApiServlet::login);
        map.put("/api/jobs", ApiServlet::getJobs);
        map.put("/api/info/job", ApiServlet::jobInfo);
        map.put("/api1/setting/theme/get", ApiServlet::getTheme);
        map.put("/api1/setting/theme/set", ApiServlet::setTheme);
        map.put("/api1/create/dir", ApiServlet::createDir);
        map.put("/api1/create/job", ApiServlet::createJob);
        map.put("/api1/build", ApiServlet::build);
        map.put("/api2/edit/dir", ApiServlet::editDir);
        map.put("/api2/edit/job", ApiServlet::editJob);
        map.put("/api2/get/job", ApiServlet::getConfig);
        map.put("/api2/remove/job", ApiServlet::removeJob);
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
        json.params.put("description", LiteCI.getDescription());
        resp.getWriter().println(json);
    }

    public static void setTheme(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json json = new Gson().fromJson(str, Json.class);
        LiteCI.serverConfig.title = json.params.get("title");
        LiteCI.serverConfig.theme = json.params.get("theme");
        LiteCI.serverConfig.accent = json.params.get("accent");
        LiteCI.serverConfig.save();
        if (json.params.get("description") != null && !json.params.get("description").isEmpty()) {
            LiteCI.setDescription(json.params.get("description"));
        }
        LiteCI.init();
        resp.getWriter().println(new DialogJson(L.get("set.save.success")));
    }

    public static void createDir(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Json json = new Gson().fromJson(str, Json.class);
            String s = Project.createDir(json.params.get("path"), json.params.get("name"));
            if (s != null) {
                resp.getWriter().println(new DialogJson(s));
            } else {
                json.params.put("status", "success");
                resp.getWriter().println(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println(new DialogJson(L.get("project.target.write.fail")));
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
        Project project = Project.getProject(json.params.get("path"));
        if (project.isDir()) {
            JobListJson list = new JobListJson();
            if (project.getUp() != null) {
                list.father = project.getUp().getPath();
            }
            if (json.params.get("path").equals("/")) {
                list.description = LiteCI.getDescription();
            }
            list.list = new LinkedList<>();
            list.name = project.name;
            list.description = project.getDescription();
            for (Map.Entry<String, Project> entry : project.internal.entrySet()) {
                try {
                    if (entry.getValue() instanceof Dir) {
                        JobListJson.Job j = new JobListJson.Job();
                        j.is_dir = true;
                        j.name = entry.getKey();
                        list.list.add(j);
                    }
                    if (entry.getValue() instanceof Job) {
                        Job job = (Job) entry.getValue();
                        JobListJson.Job j = new JobListJson.Job();
                        j.is_dir = false;
                        j.name = job.name;
                        j.last_success = job.getConfig().last_success;
                        j.last_fail = job.getConfig().last_fail;
                        j.last_time = job.getConfig().last_time;
                        j.status = job.getConfig().status;
                        list.list.add(j);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            resp.getWriter().println(list);
        }
    }

    public static void editDir(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json json = new Gson().fromJson(str, Json.class);
        resp.setStatus(200);
        try {
            Project project = Project.getProject(json.params.get("path"));
            if (project instanceof Dir) {
                Dir dir = (Dir) project;
                dir.description.set(json.params.get("description"));
                json.params.put("status", "success");
                resp.getWriter().println(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println(new DialogJson(L.get("project.target.write.fail")));
        }
    }

    public static void editJob(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(200);
        try {
            JobConfig json = new Gson().fromJson(str, JobConfig.class);
            Project project = Project.getProject(json.path);
            if (project instanceof Job) {
                Job job = (Job) project;
                job.getConfig().description = json.description;
                job.getConfig().webhook = json.webhook;
                job.getConfig().cron = json.cron;
                job.getConfig().check = json.check;
                job.getConfig().artifact = json.artifact;
                job.getConfig().shell = json.shell;
                job.save();
                resp.getWriter().println(new DialogJson(L.get("set.save.success")));
            } else {
                resp.getWriter().println(new DialogJson(L.get("project.target.write.fail")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println(new DialogJson(L.get("project.target.write.fail")));
        }
    }

    public static void getConfig(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Json json = new Gson().fromJson(str, Json.class);
            Project project = Project.getProject(json.params.get("path"));
            if (project instanceof Job) {
                resp.getWriter().println(((Job) project).getConfig());
            } else {
                resp.getWriter().println(new DialogJson(L.get("project.target.write.fail")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println(new DialogJson(L.get("project.target.read.fail")));
        }
    }

    public static void jobInfo(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Json json = new Gson().fromJson(str, Json.class);
            Project project = Project.getProject(json.params.get("path"));
            if (project instanceof Job) {
                Job job = (Job) project;
                JobJson j = new JobJson();
                j.name = job.name;
                j.date = job.getConfig().last_success;
                j.time = job.getConfig().last_time;
                j.description = job.getDescription();
                File build = job.getBuild(job.getConfig().length);
                File arti = new File(build + "/artifacts");
                if (arti.exists() && arti.isDirectory() && arti.listFiles() != null && Objects.requireNonNull(arti.listFiles()).length > 0) {
                    j.artifact = new ArrayList<>();
                    for (File file :  arti.listFiles()){
                        JobJson.File f = new JobJson.File();
                        f.name = file.getName();
                        f.size = file.length();
                        j.artifact.add(f);
                    }
                }
                resp.getWriter().println(j);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println(new DialogJson(L.get("project.target.read.fail")));
        }
    }

    public static void removeJob(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Json json = new Gson().fromJson(str, Json.class);
            Project project = Project.getProject(json.params.get("path"));
            if (project instanceof Job) {
                Job job = (Job) project;
                Dir up = job.getUp();
                up.getSons().remove(job.name);
                FileUtils.delete(job.getFile());
                FileUtils.delete(job.getWorkspace());
                Runtime.getRuntime().gc();
                json.params.put("status", "success");
                resp.getWriter().println(json);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println(new DialogJson(L.get("project.target.read.fail")));
        }
    }

    public static void build(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json json = new Gson().fromJson(str, Json.class);
        Project project = Project.getProject(json.params.get("path"));
        if (project instanceof Job) {
            BuildQueue.add((Job) project);
        }
    }
}
