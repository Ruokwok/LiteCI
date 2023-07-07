package cc.ruok.liteci.json;

import java.util.ArrayList;
import java.util.List;

public class JobsJson extends Json {

    public List<Job> jobs = new ArrayList<>();

    public static class Job {

        public boolean is_dir;
        public String name;
        public int status;
        public long success;
        public long fail;
        public long time;

    }

}
