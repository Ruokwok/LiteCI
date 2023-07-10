package cc.ruok.liteci.json;

import java.util.ArrayList;
import java.util.List;

public class JobJson extends Json {

    public static class Job {

        public boolean is_dir;
        public String name;
        public int status;
        public long success;
        public long fail;
        public long time;

    }

}
