package cc.ruok.liteci;

import cc.ruok.liteci.config.Config;
import cc.ruok.liteci.config.UserConfig;
import cn.hutool.core.util.RandomUtil;

import java.io.IOException;

public class User {

    private String name;
    private String token;
    private UserConfig config;

    public String session;
    public long active;

    public User(String name) throws IOException {
        this.name = name;
        token = RandomUtil.randomString(16);
        config = Config.loadUserConfig(name);
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public UserConfig getConfig() {
        return config;
    }

    public void active() {
        this.active = System.currentTimeMillis();
    }
}
