package cc.ruok.liteci.project;

import cc.ruok.liteci.config.Config;
import cc.ruok.liteci.config.JobConfig;

import java.io.File;
import java.io.IOException;

public class Job extends Project {

    private JobConfig config;

    public Job(File file, Dir father) throws IOException {
        super(file, father);
        config = Config.loadJobConfig(file);
        this.name = config.name;
    }

    @Override
    public boolean isDir() {
        return false;
    }

    public JobConfig getConfig() {
        return config;
    }
}
