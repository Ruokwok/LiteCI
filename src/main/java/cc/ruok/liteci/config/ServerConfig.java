package cc.ruok.liteci.config;

import java.io.File;
import java.util.HashSet;

public class ServerConfig extends Config {

    public static File file = new File("config.json");

    public int http_port = 80;
    public String title = "LiteCI";
    public String theme = "indigo";
    public String accent = "pink";
    public HashSet<String> domains;
    public int task_count = 2;
    public long build_timeout = 10;
    public boolean ssl;
    public String keystore_password;
    public Secure anonymous = new Secure();
    public Secure register = new Secure();

    public ServerConfig() {
        register.get_item = true;
        register.build = true;
        register.download = true;
    }

    @Override
    public File getFile() {
        return file;
    }

    public static class Secure {
        public String name;

        public boolean get_item;
        public boolean download;
        public boolean build;
        public boolean set_item;
        public boolean setting;
        public boolean user;

    }
}
