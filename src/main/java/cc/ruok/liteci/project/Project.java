package cc.ruok.liteci.project;

import cc.ruok.liteci.LiteCI;
import cc.ruok.liteci.config.JobConfig;
import cc.ruok.liteci.i18n.L;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public abstract class Project {

    public static Dir tree;
    public Dir up;
    public File file;
    public List<Project> internal;
    public String name;

    public static void load() {
        tree = new Dir(LiteCI.JOBS, null);
        System.out.println(tree);
    }

    public Project(File file, Dir father) {
        this.file = file;
        this.up = father;
    }

    public abstract boolean isDir();

    public Dir getUp() {
        return up;
    }

    public File getFile() {
        return file;
    }

    public static String checkPath(String path, String name) {
        if (path == null || path.isEmpty()) return L.get("project.path.null");
        if (name == null || name.isEmpty()) return L.get("project.name.null");
        File file = new File(LiteCI.JOBS + path);
        if (!file.exists()) return L.get("project.path.null");
        return null;
    }

    public static String createDir(String path, String name) {
        String s = checkPath(path, name);
        if (s != null) return s;
        File file = new File(LiteCI.JOBS + path + "/" + name);
        if (file.exists()) return L.get("project.target.exists");
        file.mkdir();
        return null;
    }

    public static String createJob(String path, String name) {
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
        } catch (Exception e) {
            return L.get("project.target.write.fail");
        }
        return null;
    }

}
