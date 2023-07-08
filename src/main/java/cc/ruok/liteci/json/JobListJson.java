package cc.ruok.liteci.json;

import java.util.List;

public class JobListJson extends Json {

    public List<Job> list;
    public String description;
    public String father;

    public static class Job {

        public String name;
        public boolean is_dir;
        public int status;
        public long last_success;
        public long last_fail;
        public long last_time;

    }
}
