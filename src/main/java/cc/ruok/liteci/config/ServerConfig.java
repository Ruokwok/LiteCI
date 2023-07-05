package cc.ruok.liteci.config;

import java.io.File;

public class ServerConfig extends Config {

    public static File file = new File("config.json");

    public int http_port = 80;
    public String title = "LiteCI";
    public String theme = "indigo";
    public String accent = "pink";

    @Override
    public File getFile() {
        return file;
    }
}
