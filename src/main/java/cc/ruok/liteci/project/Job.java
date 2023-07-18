package cc.ruok.liteci.project;

import cc.ruok.liteci.Task;
import cc.ruok.liteci.config.BuildConfig;
import cc.ruok.liteci.config.Config;
import cc.ruok.liteci.config.JobConfig;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Job extends Project {

    private JobConfig config;
    private File workspace;
    private boolean building;
    private Task task;

    public Job(File file, Dir father) throws IOException {
        super(file, father);
        config = Config.loadJobConfig(file);
        workspace = new File(config.workspace);
        this.name = config.name;
    }

    @Override
    public boolean isDir() {
        return false;
    }

    @Override
    public String getDescription() {
        return config.description;
    }

    public JobConfig getConfig() {
        return config;
    }

    public void save() throws IOException {
        config.save();
    }

    public File getWorkspace() {
        return workspace;
    }

    public File getBuildDir(int id) {
        return new File(workspace + "/build/" + id);
    }

    public BuildConfig getBuild(int id) {
        File dir = getBuildDir(id);
        if (!dir.exists()) return null;
        try {
            return new Gson().fromJson(FileUtils.readFileToString(new File(dir + "/build.json"), "utf8"), BuildConfig.class);
        } catch (IOException e) {
            return null;
        }
    }

    public String getUUID() {
        return getConfig().uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(Task task) {
        this.task = task;
        this.building = task != null;
    }

    public Task getTask() {
        return task;
    }
}
