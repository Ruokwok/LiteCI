package cc.ruok.liteci.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public abstract class Config {

    public abstract File getFile();

    public void save() throws IOException {
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(this);
        FileUtils.write(getFile(), json, "utf8");
    }

    public static ServerConfig loadServerConfig() throws IOException {
        String json = FileUtils.readFileToString(ServerConfig.file, "utf8");
        return new Gson().fromJson(json, ServerConfig.class);
    }

    public static UserConfig loadUserConfig(String name) throws IOException {
        String json = FileUtils.readFileToString(new File("users/" + name + ".json"), "utf8");
        return new Gson().fromJson(json, UserConfig.class);
    }

}
