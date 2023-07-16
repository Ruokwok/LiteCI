package cc.ruok.liteci;

import cc.ruok.liteci.i18n.L;
import cc.ruok.liteci.project.Job;

import java.util.Vector;

public class BuildQueue {

    private static Vector<Job> queue = new Vector(10, 3);

    public static boolean add(Job job) {
        if (queue.contains(job)) return false;
        queue.add(job);
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

    public static Job get() {
        if (queue.isEmpty()) return null;
        return queue.get(0);
    }

}
