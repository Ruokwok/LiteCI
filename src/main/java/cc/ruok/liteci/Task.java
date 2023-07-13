package cc.ruok.liteci;

import cc.ruok.liteci.pipe.Pipeline;
import cc.ruok.liteci.project.Job;

import java.io.File;

public class Task implements Runnable {

    private Job job;
    private Thread thread;
    private File work;
    private File build;
    private Pipeline pipe;
    private StringBuffer output;
    private static String and;

    static {
        if (System.getProperty("os.name").contains("Windows")) {
            and = "&";
        } else {
            and = ";";
        }
    }

    @Override
    public void run() {
        if (!work.exists()) work.mkdir();
        if (!build.exists()) build.mkdir();
        pipe = new Pipeline();
        output = new StringBuffer();
        pipe.setHandler(this::output);
        pipe.setPath(work);
        pipe.setCommand((System.getProperty("os.name").contains("Windows") ? "cmd /C " : "") + formatShell(job));
        try {
            pipe.run();
        } catch (Exception e) {
            e.printStackTrace();
            output(e.getMessage());
        }
    }

    public void setJob(Job job) {
        this.job = job;
        BuildQueue.remove(job);
        this.work = new File(job.getWorkspace() + "/work");
        this.build = new File(job.getWorkspace() + "/build");
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public Thread getThread() {
        return thread;
    }

    public boolean isIdle() {
        if (thread == null) return true;
        return !thread.isAlive();
    }

    public boolean isActive() {
        if (thread == null) return false;
        return thread.isAlive();
    }

    public void output(String str) {
        output.append("\n").append(str.trim());
    }

    public String formatShell(Job job) {
        StringBuilder shell = new StringBuilder();
        for (String cmd : job.getConfig().shell) {
            shell.append(cmd).append(and);
        }
        return shell.toString();
    }

}
