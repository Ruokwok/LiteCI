package cc.ruok.liteci.config;

import java.io.File;

public class UserConfig extends Config {

    public String username;
    public String password_hash;


    @Override
    public File getFile() {
        return new File("users/" + username + ".json");
    }

}
