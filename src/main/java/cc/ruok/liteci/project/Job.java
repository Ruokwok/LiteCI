package cc.ruok.liteci.project;

import cc.ruok.liteci.config.Config;
import cc.ruok.liteci.config.JobConfig;

import java.io.File;
import java.io.IOException;

public class Job extends Project {

    private JobConfig config;
    private File workspace;
    private boolean building;

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

    public File getBuild(int id) {
        return new File(workspace + "/build/" + id);
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

    public void setBuilding(boolean building) {
        this.building = building;
    }
}
