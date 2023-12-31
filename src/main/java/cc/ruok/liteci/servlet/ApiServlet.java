package cc.ruok.liteci.servlet;

import cc.ruok.liteci.*;
import cc.ruok.liteci.config.BuildConfig;
import cc.ruok.liteci.config.JobConfig;
import cc.ruok.liteci.config.ServerConfig;
import cc.ruok.liteci.json.*;
import cc.ruok.liteci.project.Dir;
import cc.ruok.liteci.project.Job;
import cc.ruok.liteci.project.Project;
import cc.ruok.liteci.i18n.L;
import cn.hutool.crypto.SecureUtil;
import com.github.mervick.aes_everywhere.Aes256;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ApiServlet extends HttpServlet {

    private static HashMap<String, ApiHandler> map = new HashMap<>();

    public static void _init() {
        map.put("/api/login", ApiServlet::login);
        map.put("/api/quit", ApiServlet::quit);
        map.put("/api/permission", ApiServlet::permission);
        map.put("/api/jobs", ApiServlet::getJobs);
        map.put("/api/info/job", ApiServlet::jobInfo);
        map.put("/api/queue", ApiServlet::getQueue);
        map.put("/api/build/info", ApiServlet::buildInfo);
        map.put("/api/builds", ApiServlet::builds);
        map.put("/api4/setting/theme/get", ApiServlet::getTheme);
        map.put("/api4/setting/theme/set", ApiServlet::setTheme);
        map.put("/api3/create/dir", ApiServlet::createDir);
        map.put("/api3/create/job", ApiServlet::createJob);
        map.put("/api2/build", ApiServlet::build);
        map.put("/api3/edit/dir", ApiServlet::editDir);
        map.put("/api3/edit/job", ApiServlet::editJob);
        map.put("/api3/get/job", ApiServlet::getConfig);
        map.put("/api3/remove/job", ApiServlet::removeJob);
        map.put("/api3/remove/dir", ApiServlet::removeDir);
        map.put("/api4/setting/server/get", ApiServlet::getServer);
        map.put("/api4/setting/server/set", ApiServlet::setServer);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ApiHandler handler = map.get(req.getRequestURI());
        User user = LiteCI.getOnlineUser(req.getSession().getId());
        if (check(req.getRequestURI(), user)) {
            resp.setCharacterEncoding("UTF-8");
            if (handler != null) handler.handler(req.getReader().readLine(), req, resp);
        } else {
            resp.setStatus(403);
        }
    }

    public boolean check(String api, User user) {
        if (api.equals("/api/login")) return true;
        if (api.equals("/api/quit")) return true;
        if (api.equals("/api/permission")) return true;
        if (user == null) {
            if (api.startsWith("/api/") && LiteCI.serverConfig.anonymous.get_item) return true;
            if (api.startsWith("/api1") && LiteCI.serverConfig.anonymous.download) return true;
            if (api.startsWith("/api2") && LiteCI.serverConfig.anonymous.build) return true;
            if (api.startsWith("/api3") && LiteCI.serverConfig.anonymous.set_item) return true;
            if (api.startsWith("/api4") && LiteCI.serverConfig.anonymous.setting) return true;
            if (api.startsWith("/api5") && LiteCI.serverConfig.anonymous.user) return true;
        } else {
            if (user.isAdmin()) return true;
            if (api.startsWith("/api/") && LiteCI.serverConfig.register.get_item) return true;
            if (api.startsWith("/api1") && LiteCI.serverConfig.register.download) return true;
            if (api.startsWith("/api2") && LiteCI.serverConfig.register.build) return true;
            if (api.startsWith("/api3") && LiteCI.serverConfig.register.set_item) return true;
            if (api.startsWith("/api4") && LiteCI.serverConfig.register.setting) return true;
            if (api.startsWith("/api5") && LiteCI.serverConfig.register.user) return true;
        }
        return false;
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
                CRON.load(job);
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
                File build = job.getBuildDir(job.getConfig().success_id);
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
                BuildConfig b = job.getBuild(job.getConfig().success_id);
                if (b != null) j.commits = b.commits;
                j.list = Build.getBuildList(job);
                if (job.isBuilding()) {
                    j.building = job.getTask().getBuildId();
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
                if (job.isBuilding()) {
                    resp.getWriter().println(new DialogJson(L.get("web.remove.job.cancel")));
                    return;
                }
                if (Project.removeJob(job)) {
                    json.params.put("status", "success");
                    resp.getWriter().println(json);
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println(new DialogJson(L.get("project.fail.console")));
        }
    }

    public static void removeDir(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Json json = new Gson().fromJson(str, Json.class);
            Project project = Project.getProject(json.params.get("path"));
            if (project.isDir()) {
                Dir dir = (Dir) project;
                if (dir.isRoot()) {
                    resp.getWriter().println(L.get("web.remove.dir.root"));
                    return;
                }
                if (Project.removeDir(dir)) {
                    json.params.put("status", "success");
                    resp.getWriter().println(json);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println(new DialogJson(L.get("project.fail.console")));
        }
    }

    public static void build(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Json json = new Gson().fromJson(str, Json.class);
        Project project = Project.getProject(json.params.get("path"));
        if (project instanceof Job) {
            User user = LiteCI.getOnlineUser(req.getSession().getId());
            BuildQueue.add((Job) project, new BuildConfig.Trigger(0, user == null ? L.get("set.secure.anonymous") : user.getName()));
        }
        resp.setStatus(200);
        resp.getWriter().println("{}");
    }

    public static void getQueue(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            QueueJson json = new QueueJson();
            json.task = Build.getTaskList();
            json.queue = Build.getQueueList();
            resp.setStatus(200);
            resp.getWriter().println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void buildInfo(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Json json = new Gson().fromJson(str, Json.class);
            Project project = Project.getProject(json.params.get("path"));
            int id = Integer.parseInt(json.params.get("id"));
            if (project instanceof Job) {
                Job job = (Job) project;
                Task task = job.getTask();
                BuildConfig build = job.getBuild(id);
                if (task == null && build == null || build == null && task.getBuildId() != id) {
                    json.params.put("message", L.get("web.build.null"));
                    resp.getWriter().println(json);
                } else {
                    BuildJson bj = new BuildJson();
                    bj.params.put("message", "success");
                    bj.name = job.getName();
                    bj.id = id;
                    if (task != null && task.getBuildId() == id) {
                        bj.status = 0;
                        bj.output = task.getOutput().split("\n");
                        bj.trigger = task.getTrigger();
                    } else {
                        bj.commits = build.commits;
                        bj.status = build.status ? 1 : 2;
                        bj.output = FileUtils.readFileToString(new File(job.getBuildDir(id) + "/terminal.txt"), "utf8").split("\n");
                        bj.date = build.date;
                        bj.time = build.time;
                        bj.exit = build.exit;
                        bj.trigger = build.trigger;
                        File file = new File(job.getBuildDir(id) + "/artifacts");
                        if (file.isDirectory()) {
                            File[] fs = file.listFiles();
                            if (fs.length > 0) {
                                bj.artifacts = new ArrayList<>();
                                for (File f : fs) {
                                    if (f.isFile()) {
                                        JobJson.File _f = new JobJson.File();
                                        _f.name = f.getName();
                                        _f.size = f.length();
                                        bj.artifacts.add(_f);
                                    }
                                }
                            }
                        }
                    }
                    resp.setStatus(200);
                    resp.getWriter().println(bj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Json json = new Json();
            json.params.put("message", L.get("web.build.null"));
            resp.getWriter().println(json);
        }
    }

    public static void quit(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = LiteCI.getOnlineUser(req.getSession().getId());
        req.changeSessionId();
        if (user != null) user.quit();
        resp.setStatus(200);
        resp.getWriter().println("{}");
    }

    public static void getServer(String str, HttpServletRequest req, HttpServletResponse resp) {
        try {
            resp.setStatus(200);
            resp.getWriter().println(new Gson().toJson(LiteCI.serverConfig));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setServer(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(200);
        try {
            ServerConfig json = new Gson().fromJson(str, ServerConfig.class);
            LiteCI.serverConfig.http_port = json.http_port;
            LiteCI.serverConfig.task_count = json.task_count;
            LiteCI.serverConfig.build_timeout = json.build_timeout;
            LiteCI.serverConfig.ssl = json.ssl;
            LiteCI.serverConfig.keystore_password = json.keystore_password;
            if (json.domains.contains("")) json.domains.remove("");
            if (json.domains.contains(null)) json.domains.remove(null);
            LiteCI.serverConfig.domains = json.domains;
            if (json.anonymous != null) LiteCI.serverConfig.anonymous = json.anonymous;
            if (json.register != null) LiteCI.serverConfig.register = json.register;
            LiteCI.serverConfig.save();
            resp.getWriter().println(new DialogJson(L.get("set.save.success")));
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println(new DialogJson(L.get("project.fail.console")));
        }
    }

    public static void permission(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = LiteCI.getOnlineUser(req.getSession().getId());
        resp.setStatus(200);
        if (user == null) {
            resp.getWriter().println(new Gson().toJson(LiteCI.serverConfig.anonymous));
        } else {
            if (user.isAdmin()) {
                ServerConfig.Secure secure = new ServerConfig.Secure();
                secure.setting = true;
                secure.get_item = true;
                secure.user = true;
                secure.download = true;
                secure.build = true;
                secure.set_item = true;
                secure.name = user.getName();
                resp.getWriter().println(new Gson().toJson(secure));
            } else {
                resp.getWriter().println(new Gson().toJson(LiteCI.serverConfig.register));
            }
        }
    }

    public static void builds(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<JobJson.Info> list = Build.getBuildList();
            resp.setStatus(200);
            resp.getWriter().println(new Gson().toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
