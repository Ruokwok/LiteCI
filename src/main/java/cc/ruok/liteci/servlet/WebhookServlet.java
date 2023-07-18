package cc.ruok.liteci.servlet;

import cc.ruok.liteci.BuildQueue;
import cc.ruok.liteci.project.Job;
import cc.ruok.liteci.project.Project;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class WebhookServlet extends ServerServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(8);
        Project project = Project.getProject(path);
        if (project instanceof Job) {
            Job job = (Job) project;
            if (job.getConfig().webhook.enable) {
                if (job.getConfig().webhook.token.equals(req.getParameter("token"))) {
                    BuildQueue.add(job);
                    resp.setStatus(200);
                    resp.getWriter().println("{ status: 'success', code: 200}");
                    return;
                }
            }
            resp.setStatus(400);
            resp.getWriter().println("{ status: 'fail', code: 400}");
        }

    }
}
