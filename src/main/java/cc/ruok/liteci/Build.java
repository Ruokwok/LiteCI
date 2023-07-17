package cc.ruok.liteci;

import cc.ruok.liteci.config.BuildConfig;
import cc.ruok.liteci.i18n.L;
import cc.ruok.liteci.json.JobJson;
import cc.ruok.liteci.json.QueueJson;
import cc.ruok.liteci.project.Job;
import org.h2.jdbc.JdbcSQLNonTransientConnectionException;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Build {

    private static List<Task> tasks = new ArrayList<>();
    private static final String DATABASE = "jdbc:h2:" + new File("build").getAbsolutePath();
    private static Connection conn;

    public static void init() {
        for (int i = 0; i < LiteCI.serverConfig.task_count; i++) {
            tasks.add(new Task(i));
        }
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(DATABASE);
            Statement stmt = conn.createStatement();
            try {
                stmt.execute("create table build("
                        + "    uuid varchar,"
                        + "    jobid int,"
                        + "    status boolean,"
                        + "    time int,"
                        + "    date bigint)"
                        + "    ");
            } catch (Exception e) {}
            stmt.close();
        } catch (JdbcSQLNonTransientConnectionException e) {
            Logger.error(L.get("console.database.lock"));
            System.exit(1);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void run() {
        if (BuildQueue.isEmpty()) return;
        Task task = getIdleTask();
        if (task == null) return;
        Job job = BuildQueue.get();
        if (job != null && !job.isBuilding()) {
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

    public static void addBuild(String uuid, BuildConfig config) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO build VALUES('{uuid}',{jobid},{status},{time},{date})"
                    .replace("{uuid}", uuid)
                    .replace("{jobid}", String.valueOf(config.id))
                    .replace("{status}", String.valueOf(config.status))
                    .replace("{time}", String.valueOf(config.time))
                    .replace("{date}", String.valueOf(config.date))
            );
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<JobJson.Info> getBuildList(Job job) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM build WHERE uuid='" + job.getUUID() + "' ORDER BY date DESC");
            ArrayList<JobJson.Info> list = new ArrayList<>();
            while (rs.next()) {
                JobJson.Info info = new JobJson.Info();
                info.id = rs.getInt("jobid");
                info.status = rs.getBoolean("status");
                info.date = rs.getLong("date");
                info.time = rs.getInt("time");
                list.add(info);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<QueueJson.Task> getTaskList() {
        List<QueueJson.Task> list = new ArrayList<>();
        try {
            for (Task task : tasks) {
                QueueJson.Task t = new QueueJson.Task();
                if (task.isActive()) {
                    t.name = task.getJob().getName();
                    t.thread = task.getThread().getName();
                }
                list.add(t);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> getQueueList() {
        return BuildQueue.getQueueList();
    }

    public static boolean execute(String sql) throws SQLException {
        Statement stat = conn.createStatement();
        boolean b = stat.execute(sql);
        stat.close();
        return b;
    }

}
