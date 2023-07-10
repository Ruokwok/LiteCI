package cc.ruok.liteci.config;

import com.google.gson.Gson;

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
    public String description = "";
    public String path;

    public JobConfig(File file) {
        this.file = file;
    }

    public JobConfig() {
    }

    @Override
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
