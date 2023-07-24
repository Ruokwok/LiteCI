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
    public Trigger trigger;

    public BuildConfig(File file) {
        this.file = file;
    }

    @Override
    public File getFile() {
        return file;
    }

    public static class Commit {

        public String id;
        public String change;
        public String user;
        public String url;

    }

    public static class Trigger {

        public int type;
        public String content;

        public Trigger(int type, String content) {
            this.type = type;
            this.content = content;
        }

    }
}
