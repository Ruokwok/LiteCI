package cc.ruok.liteci.config;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;

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
    public List<String> shell;
    public Webhook webhook = new Webhook();
    public Cron cron = new Cron();
    public Check check = new Check();
    public Artifact artifact = new Artifact();

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

    public static class Webhook {

        public boolean enable;
        public String token;

    }
    public static class Cron {

        public boolean enable;
        public String expression;

    }

    public static class Check {

        public boolean enable;
        public List<String> shell;
        public boolean only_cron;

    }

    public static class Artifact {

        public boolean enable;
        public List<String> files;

    }
}
