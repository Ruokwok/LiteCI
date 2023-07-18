package cc.ruok.liteci;

import cc.ruok.liteci.config.BuildConfig;
import cc.ruok.liteci.i18n.L;
import cc.ruok.liteci.pipe.Pipeline;
import cc.ruok.liteci.project.Job;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Task implements Runnable {

    private Job job;
    private Thread thread;
    private File work;
    private File build;
    private Pipeline pipe;
    private StringBuffer output;
    private Timer timer = new Timer();
    private File terminal;
    private int taskId;
    private int buildId;
    private BuildConfig.Trigger trigger;

    private static String and;
    private static String charset;

    static {
        if (System.getProperty("os.name").contains("Windows")) {
            and = "&";
            charset = "gbk";
        } else {
            and = ";";
            charset = "utf8";
        }
    }

    public Task(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        if (!work.exists()) work.mkdir();
        if (!build.exists()) build.mkdir();

        terminal = new File(build + "/" + buildId + "/terminal.txt");
        if (job.getConfig().artifact.enable) {
            for (String f : job.getConfig().artifact.files) {
                File file = new File(work + "/" + f);
                if (file.exists()) {
                    try {
                        FileUtils.delete(file);
                    } catch (IOException e) {
                        Logger.error(L.get("console.build.delete.artifact.fail"));
                        e.printStackTrace();
                    }
                }
            }
        }

        pipe = new Pipeline();
        output = new StringBuffer();
        pipe.setHandler(this::output);
        pipe.setPath(work);
        pipe.setCharset(charset);
        pipe.setCommand((System.getProperty("os.name").contains("Windows") ? "cmd /C" : "") + formatShell(job));
        try {
            int exit = pipe.run();
            job.getConfig().length = buildId;
            job.save();
            File file = new File(build + "/" + job.getConfig().length);
            file.mkdir();
            BuildConfig config = new BuildConfig(new File(file + "/build.json"));
            config.date = System.currentTimeMillis();
            config.time = System.currentTimeMillis() - start;
            config.id = buildId;
            config.exit = exit;
            config.trigger = trigger;
            if (exit == 0) {
                success(config);
            } else {
                fail(config);
            }
        } catch (Exception e) {
            e.printStackTrace();
            output(e.getMessage());
        }
        job.setBuilding(null);
        Build.run();
    }

    public void setJob(Job job) {
        this.job = job;
        BuildQueue.remove(job);
        this.work = new File(job.getWorkspace() + "/work");
        this.build = new File(job.getWorkspace() + "/build");
    }

    public void start() {
        job.setBuilding(this);
        buildId = job.getConfig().length + 1;
        Logger.info(L.get("console.build.start") + ": " + job.getName() + "(#" + buildId + ")");
        if (LiteCI.serverConfig.build_timeout > 0) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    timeout();
                }
            }, LiteCI.serverConfig.build_timeout * 1000);
        }
        thread = new Thread(this);
        thread.setName("BuildTask-" + taskId);
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
        try {
            FileUtils.writeStringToFile(terminal, str + "\n", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String formatShell(Job job) {
        StringBuilder shell = new StringBuilder();
        for (String cmd : job.getConfig().shell) {
            shell.append(cmd).append(and);
        }
        return shell.toString();
    }

    public void timeout() {
        if (isActive()) {
            try {
                pipe.kill();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void success(BuildConfig config) {
        config.status = true;
        Build.addBuild(job.getUUID(), config);
        job.getConfig().last_success = config.date;
        job.getConfig().success_id = buildId;
        job.getConfig().last_time = config.time;
        job.getConfig().status = 1;
        try {
            config.save();
            job.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (job.getConfig().artifact.enable) {
            File dir = new File(build + "/" + config.id + "/artifacts");
            dir.mkdir();
            for (String f : job.getConfig().artifact.files) {
                File file = new File(work + "/" + f);
                if (file.exists()) {
                    try {
                        FileUtils.copyFile(file, new File(dir + "/" + file.getName()));
                    } catch (IOException e) {
                        Logger.error(L.get("console.build.copy.artifact.fail") + ": " + f);
                        e.printStackTrace();
                    }
                }
            }
        }
        Logger.info(L.get("console.build.success") + ": " + job.getName() + "(#" + buildId + ")");
    }

    public void fail(BuildConfig config) {
        config.status = false;
        Build.addBuild(job.getUUID(), config);
        job.getConfig().last_fail = config.date;
        job.getConfig().status = 2;
        try {
            config.save();
            job.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info(L.get("console.build.fail") + ": " + job.getName() + "(#" + buildId + ")");
    }

    public Job getJob() {
        return job;
    }

    public int getBuildId() {
        if (isIdle()) return 0;
        return buildId;
    }

    public String getOutput() {
        return output.toString();
    }

    public BuildConfig.Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(BuildConfig.Trigger trigger) {
        this.trigger = trigger;
    }
}
