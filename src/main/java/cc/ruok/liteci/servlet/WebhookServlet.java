package cc.ruok.liteci.servlet;

import cc.ruok.liteci.BuildQueue;
import cc.ruok.liteci.config.BuildConfig;
import cc.ruok.liteci.config.GithubHookshot;
import cc.ruok.liteci.project.Job;
import cc.ruok.liteci.project.Project;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WebhookServlet extends ServerServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s = req.getReader().readLine();
        GithubHookshot json = null;
        try {
            json = new Gson().fromJson(s, GithubHookshot.class);
        } catch (Exception e) {}
        String path = req.getRequestURI().substring(8);
        Project project = Project.getProject(path);
        if (project instanceof Job) {
            Job job = (Job) project;
            if (job.getConfig().webhook.enable) {
                if (job.getConfig().webhook.token.equals(req.getParameter("token"))) {
                    List<BuildConfig.Commit> commits = new LinkedList<>();
                    if (json != null && json.commits != null) {
                        for (GithubHookshot.Commit commit : json.commits) {
                            BuildConfig.Commit c = new BuildConfig.Commit();
                            c.id = commit.id.substring(0, 7);
                            c.url = commit.url;
                            c.change = commit.message;
                            c.user = commit.author.username;
                            commits.add(c);
                        }
                        GithubHookshot.map.put(job.getUUID(), commits);
                    }
                    BuildQueue.add(job, new BuildConfig.Trigger(1, req.getHeader("User-Agent")));
                    resp.setStatus(200);
                    resp.getWriter().println("{ status: 'success', code: 200}");
                    req.setCharacterEncoding("utf8");
                    return;
                }
            }
            resp.setStatus(400);
            resp.getWriter().println("{ status: 'fail', code: 400}");
        }

    }
}
