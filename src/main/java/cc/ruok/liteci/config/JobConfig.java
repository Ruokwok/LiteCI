package cc.ruok.liteci.config;

import cc.ruok.liteci.project.Job;

import java.io.File;

public class JobConfig extends Config {

    private transient File file;

    public String uuid;
    public String name;
    public String length;
    public long last_time;
    public long last_success;
    public long last_fail;
    public int status;
    public String workspace;

    public JobConfig(File file) {
        this.file = file;
    }

    @Override
    public File getFile() {
        return file;
    }
}
