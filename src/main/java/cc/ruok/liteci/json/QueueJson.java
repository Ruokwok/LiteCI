package cc.ruok.liteci.json;

import java.util.List;

public class QueueJson extends Json {

    public List<Task> task;
    public List<String> queue;

    public static class Task {

        public String name;
        public String thread;

    }

}
