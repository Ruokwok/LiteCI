package cc.ruok.liteci;

import cc.ruok.liteci.config.BuildConfig;
import cc.ruok.liteci.i18n.L;
import cc.ruok.liteci.project.Job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class BuildQueue {

    private static Vector<Job> queue = new Vector(10, 3);
    private static HashMap<Job, BuildConfig.Trigger> triggers = new HashMap<>();

    public static boolean add(Job job, BuildConfig.Trigger trigger) {
        if (queue.contains(job)) return false;
        queue.add(job);
        triggers.put(job, trigger);
        Logger.info(job.getName() + L.get("console.build.add.queue"));
        Build.run();
        return true;
    }

    public static void remove(Job job) {
        queue.remove(job);
    }

    public static boolean isEmpty() {
        return queue.isEmpty();
    }

    public static Map get() {
        if (queue.isEmpty()) return null;
        Map map = new Map();
        map.job = queue.get(0);
        map.trigger = triggers.get(map.job);
        triggers.remove(map.job);
        return map;
    }

    public static List<String> getQueueList() {
        List<String> list = new ArrayList<>();
        for (Job job : queue) {
            list.add(job.getName());
        }
        return list;
    }

    public static class Map {

        public Job job;
        public BuildConfig.Trigger trigger;

    }

}
