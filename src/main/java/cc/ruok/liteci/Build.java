package cc.ruok.liteci;

import cc.ruok.liteci.project.Job;

import java.util.ArrayList;
import java.util.List;

public class Build {

    private static List<Task> tasks = new ArrayList<>();

    public static void init() {
        for (int i = 0; i < LiteCI.serverConfig.task_count; i++) {
            tasks.add(new Task());
        }
    }

    public static void run() {
        if (BuildQueue.isEmpty()) return;
        Task task = getIdleTask();
        if (task == null) return;
        Job job = BuildQueue.get();
        if (job != null) {
            task.setJob(job);
            task.start();
        }
    }

    public static Task getIdleTask() {
        for (Task task : tasks) {
            if (task.isIdle()) return task;
        }
        return null;
    }

}
