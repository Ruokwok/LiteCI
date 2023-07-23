package cc.ruok.liteci.config;

import java.io.File;

public class UserConfig extends Config {

    public String username;
    public String password_hash;
    public String group = "user";


    @Override
    public File getFile() {
        return new File("users/" + username + ".json");
    }

    public boolean isAdmin() {
        return group.equals("admin");
    }

}
