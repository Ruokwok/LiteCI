package cc.ruok.liteci.json;

import cc.ruok.liteci.config.BuildConfig;

import java.util.List;

public class JobJson extends Json {

    public String name;
    public long date;
    public long time;
    public String description;
    public List<File> artifact;
    public List<Info> list;
    public int building;
    public List<BuildConfig.Commit> commits;

    public static class File {

        public String name;
        public long size;

    }

    public static class Info {

        public int id;
        public boolean status;
        public long date;
        public int time;
        public String name;
        public int trigger = -1;

    }

}
