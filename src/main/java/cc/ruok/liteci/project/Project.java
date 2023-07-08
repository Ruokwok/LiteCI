package cc.ruok.liteci.project;

import cc.ruok.liteci.LiteCI;
import cc.ruok.liteci.config.Description;
import cc.ruok.liteci.config.JobConfig;
import cc.ruok.liteci.i18n.L;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public abstract class Project {

    public static Dir tree;
    public Dir up;
    public File file;
    public Map<String, Project> internal;
    public String name;
    public String path;

    public static void load() {
        tree = new Dir(LiteCI.JOBS, null);
        tree.name = "/";
    }

    public Project(File file, Dir father) {
        this.file = file;
        this.up = father;
        this.path = file.getPath().substring(4).replaceAll("\\\\", "/");
    }

    public abstract boolean isDir();

    public abstract String getDescription();

    public Dir getUp() {
        return up;
    }

    public File getFile() {
        return file;
    }

    public String getPath() {
        return path;
    }

    public static Project getRoot() {
        return tree;
    }

    public static Project getProject(String path) {
        path = path.replaceAll("%20", " ");
        if (path.equals("/")) return tree;
        if (path.startsWith("/")) path = path.substring(1);
        String[] p = path.split("/");
        Project project = tree;
        for (String f : p) {
            f = f.replaceAll("\\+", " ");
            if (!project.isDir()) return null;
            if (!project.internal.containsKey(f)) return null;
            project = project.internal.get(f);
        }
        return project;
    }

    public static String checkPath(String path, String name) {
        path = path.replaceAll("%20", " ");
        if (path == null || path.isEmpty()) return L.get("project.path.null");
        if (name == null || name.isEmpty()) return L.get("project.name.null");
        File file = new File(LiteCI.JOBS + path);
        if (!file.exists()) return L.get("project.path.null");
        return null;
    }

    public static String createDir(String path, String name) {
        path = path.replaceAll("%20", " ");
        String s = checkPath(path, name);
        if (s != null) return s;
        File file = new File(LiteCI.JOBS + path + "/" + name);
        if (file.exists()) return L.get("project.target.exists");
        file.mkdir();
        Project up = getProject(path);
        if (up != null) up.internal.put(name, new Dir(file, (Dir) up));
        return null;
    }

    public static String createJob(String path, String name) {
        path = path.replaceAll("%20", " ");
        String s = checkPath(path, name);
        if (s != null) return s;
        File file = new File(LiteCI.JOBS + path + "/" + name);
        if (file.exists()) return L.get("project.target.exists");
        try {
            file.createNewFile();
            JobConfig jobConfig = new JobConfig(file);
            jobConfig.name = name;
            jobConfig.uuid = UUID.randomUUID().toString();
            jobConfig.workspace = LiteCI.WORKSPACE + "/" + name + "_" + jobConfig.uuid;
            new File(jobConfig.workspace).mkdir();
            jobConfig.save();
            Project up = getProject(path);
            Job job = new Job(file, (Dir) up);
            if (up != null) up.internal.put(name, job);
        } catch (Exception e) {
            return L.get("project.target.write.fail");
        }
        return null;
    }

    public static void saveDir(Dir dir) {
        for (Map.Entry<String, Project> entry : dir.internal.entrySet()) {
            if (entry.getValue() instanceof Job) {
                try {
                    ((Job) entry.getValue()).save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (entry.getValue() instanceof Dir) {
                saveDir((Dir) entry.getValue());
            }
        }
    }

    public static void saveAll() {
        saveDir(tree);
    }

}
