package cc.ruok.liteci.servlet;

import cc.ruok.liteci.LiteCI;
import cc.ruok.liteci.Logger;
import cc.ruok.liteci.User;
import cc.ruok.liteci.i18n.L;
import cc.ruok.liteci.project.Job;
import cc.ruok.liteci.project.Project;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DownloadServlet extends ServerServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String url = req.getRequestURI().replaceFirst("/download", "");
            String[] split = url.split("/");
            String filename = split[split.length - 1];
            String build = split[split.length - 2];
            String path = url.replace("/" + filename, "").replace("/" + build, "");
            Project project = Project.getProject(path);
            Job job = (Job) project;
            int id;
            if (build.equals("latest")) {
                id = job.getConfig().success_id;
            } else {
                id = Integer.parseInt(build);
            }
            if (id > 0) {
                File file = new File(job.getBuildDir(id) + "/artifacts/" + filename);
                if (file.exists() && file.isFile()) {
                    resp.setStatus(200);
                    resp.setHeader("content-disposition", "attachment;fileName=" + filename);
                    FileInputStream inputStream = new FileInputStream(file);
                    IOUtils.write(inputStream.readAllBytes(), resp.getOutputStream());
                    User user = LiteCI.getOnlineUser(req.getSession().getId());
                    Logger.info(L.get("console.download.file") + ": " + path + "#" + id + "(" + (user == null? L.get("console.download.anonymous"): user.getName()) + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}