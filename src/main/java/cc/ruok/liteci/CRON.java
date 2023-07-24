package cc.ruok.liteci;

import cc.ruok.liteci.config.BuildConfig;
import cc.ruok.liteci.project.Job;
import cc.ruok.liteci.project.Project;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;

import java.util.ArrayList;

public class CRON implements Task {

    private Job job;

    public static void load() {
        ArrayList<Job> list = new ArrayList<>();
        Project.getJobList(Project.tree, list);
        for (Job job: list) {
            if (job.getConfig().cron.enable && job.getConfig().cron.expression != null) {
                CronUtil.schedule(job.getUUID(), job.getConfig().cron.expression, new CRON(job));
            }
        }
        CronUtil.start();
    }

    public static void load(Job job) {
        CronUtil.remove(job.getUUID());
        if (job.getConfig().cron.enable && job.getConfig().cron.expression != null) {
            CronUtil.schedule(job.getUUID(), job.getConfig().cron.expression, new CRON(job));
        }
    }

    public CRON(Job job) {
        this.job = job;
    }

    @Override
    public void execute() {
        BuildConfig.Trigger trigger = new BuildConfig.Trigger(2, "");
        BuildQueue.add(job, trigger);
    }
}
