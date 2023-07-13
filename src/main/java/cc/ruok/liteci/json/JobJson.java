package cc.ruok.liteci.json;

import java.util.List;

public class JobJson extends Json {

    public String name;
    public long date;
    public long time;
    public String description;
    public List<File> artifact;

    public static class File {

        public String name;
        public long size;

    }

}
