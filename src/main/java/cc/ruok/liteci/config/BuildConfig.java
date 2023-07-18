package cc.ruok.liteci.config;

import java.io.File;
import java.util.List;

public class BuildConfig extends Config {

    private transient File file;

    public int id;
    public boolean status;
    public long date;
    public long time;
    public int exit;
    public List<Commit> commits;
    public List<String> artifacts;

    public BuildConfig(File file) {
        this.file = file;
    }

    @Override
    public File getFile() {
        return file;
    }

    public static class Commit {

        public String hash;
        public String change;
        public String user;

    }

    public static class Trigger {

        public int type;
        public String content;

    }
}
