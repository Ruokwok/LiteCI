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
        s = "{\n" +
                "  \"ref\": \"refs/heads/master\",\n" +
                "  \"before\": \"6d1b199e78ad5fca7974415e9458a676c0f099a5\",\n" +
                "  \"after\": \"3a89021a09c656b8e2365ec165a1eeb75f0f85a2\",\n" +
                "  \"repository\": {\n" +
                "    \"id\": 599171874,\n" +
                "    \"node_id\": \"R_kgDOI7ajIg\",\n" +
                "    \"name\": \"Tetris\",\n" +
                "    \"full_name\": \"Ruokwok/Tetris\",\n" +
                "    \"private\": false,\n" +
                "    \"owner\": {\n" +
                "      \"name\": \"Ruokwok\",\n" +
                "      \"email\": \"72373519+Ruokwok@users.noreply.github.com\",\n" +
                "      \"login\": \"Ruokwok\",\n" +
                "      \"id\": 72373519,\n" +
                "      \"node_id\": \"MDQ6VXNlcjcyMzczNTE5\",\n" +
                "      \"avatar_url\": \"https://avatars.githubusercontent.com/u/72373519?v=4\",\n" +
                "      \"gravatar_id\": \"\",\n" +
                "      \"url\": \"https://api.github.com/users/Ruokwok\",\n" +
                "      \"html_url\": \"https://github.com/Ruokwok\",\n" +
                "      \"followers_url\": \"https://api.github.com/users/Ruokwok/followers\",\n" +
                "      \"following_url\": \"https://api.github.com/users/Ruokwok/following{/other_user}\",\n" +
                "      \"gists_url\": \"https://api.github.com/users/Ruokwok/gists{/gist_id}\",\n" +
                "      \"starred_url\": \"https://api.github.com/users/Ruokwok/starred{/owner}{/repo}\",\n" +
                "      \"subscriptions_url\": \"https://api.github.com/users/Ruokwok/subscriptions\",\n" +
                "      \"organizations_url\": \"https://api.github.com/users/Ruokwok/orgs\",\n" +
                "      \"repos_url\": \"https://api.github.com/users/Ruokwok/repos\",\n" +
                "      \"events_url\": \"https://api.github.com/users/Ruokwok/events{/privacy}\",\n" +
                "      \"received_events_url\": \"https://api.github.com/users/Ruokwok/received_events\",\n" +
                "      \"type\": \"User\",\n" +
                "      \"site_admin\": false\n" +
                "    },\n" +
                "    \"html_url\": \"https://github.com/Ruokwok/Tetris\",\n" +
                "    \"description\": \"在Nukkit上实现经典游戏俄罗斯方块\",\n" +
                "    \"fork\": false,\n" +
                "    \"url\": \"https://github.com/Ruokwok/Tetris\",\n" +
                "    \"forks_url\": \"https://api.github.com/repos/Ruokwok/Tetris/forks\",\n" +
                "    \"keys_url\": \"https://api.github.com/repos/Ruokwok/Tetris/keys{/key_id}\",\n" +
                "    \"collaborators_url\": \"https://api.github.com/repos/Ruokwok/Tetris/collaborators{/collaborator}\",\n" +
                "    \"teams_url\": \"https://api.github.com/repos/Ruokwok/Tetris/teams\",\n" +
                "    \"hooks_url\": \"https://api.github.com/repos/Ruokwok/Tetris/hooks\",\n" +
                "    \"issue_events_url\": \"https://api.github.com/repos/Ruokwok/Tetris/issues/events{/number}\",\n" +
                "    \"events_url\": \"https://api.github.com/repos/Ruokwok/Tetris/events\",\n" +
                "    \"assignees_url\": \"https://api.github.com/repos/Ruokwok/Tetris/assignees{/user}\",\n" +
                "    \"branches_url\": \"https://api.github.com/repos/Ruokwok/Tetris/branches{/branch}\",\n" +
                "    \"tags_url\": \"https://api.github.com/repos/Ruokwok/Tetris/tags\",\n" +
                "    \"blobs_url\": \"https://api.github.com/repos/Ruokwok/Tetris/git/blobs{/sha}\",\n" +
                "    \"git_tags_url\": \"https://api.github.com/repos/Ruokwok/Tetris/git/tags{/sha}\",\n" +
                "    \"git_refs_url\": \"https://api.github.com/repos/Ruokwok/Tetris/git/refs{/sha}\",\n" +
                "    \"trees_url\": \"https://api.github.com/repos/Ruokwok/Tetris/git/trees{/sha}\",\n" +
                "    \"statuses_url\": \"https://api.github.com/repos/Ruokwok/Tetris/statuses/{sha}\",\n" +
                "    \"languages_url\": \"https://api.github.com/repos/Ruokwok/Tetris/languages\",\n" +
                "    \"stargazers_url\": \"https://api.github.com/repos/Ruokwok/Tetris/stargazers\",\n" +
                "    \"contributors_url\": \"https://api.github.com/repos/Ruokwok/Tetris/contributors\",\n" +
                "    \"subscribers_url\": \"https://api.github.com/repos/Ruokwok/Tetris/subscribers\",\n" +
                "    \"subscription_url\": \"https://api.github.com/repos/Ruokwok/Tetris/subscription\",\n" +
                "    \"commits_url\": \"https://api.github.com/repos/Ruokwok/Tetris/commits{/sha}\",\n" +
                "    \"git_commits_url\": \"https://api.github.com/repos/Ruokwok/Tetris/git/commits{/sha}\",\n" +
                "    \"comments_url\": \"https://api.github.com/repos/Ruokwok/Tetris/comments{/number}\",\n" +
                "    \"issue_comment_url\": \"https://api.github.com/repos/Ruokwok/Tetris/issues/comments{/number}\",\n" +
                "    \"contents_url\": \"https://api.github.com/repos/Ruokwok/Tetris/contents/{+path}\",\n" +
                "    \"compare_url\": \"https://api.github.com/repos/Ruokwok/Tetris/compare/{base}...{head}\",\n" +
                "    \"merges_url\": \"https://api.github.com/repos/Ruokwok/Tetris/merges\",\n" +
                "    \"archive_url\": \"https://api.github.com/repos/Ruokwok/Tetris/{archive_format}{/ref}\",\n" +
                "    \"downloads_url\": \"https://api.github.com/repos/Ruokwok/Tetris/downloads\",\n" +
                "    \"issues_url\": \"https://api.github.com/repos/Ruokwok/Tetris/issues{/number}\",\n" +
                "    \"pulls_url\": \"https://api.github.com/repos/Ruokwok/Tetris/pulls{/number}\",\n" +
                "    \"milestones_url\": \"https://api.github.com/repos/Ruokwok/Tetris/milestones{/number}\",\n" +
                "    \"notifications_url\": \"https://api.github.com/repos/Ruokwok/Tetris/notifications{?since,all,participating}\",\n" +
                "    \"labels_url\": \"https://api.github.com/repos/Ruokwok/Tetris/labels{/name}\",\n" +
                "    \"releases_url\": \"https://api.github.com/repos/Ruokwok/Tetris/releases{/id}\",\n" +
                "    \"deployments_url\": \"https://api.github.com/repos/Ruokwok/Tetris/deployments\",\n" +
                "    \"created_at\": 1675871307,\n" +
                "    \"updated_at\": \"2023-07-02T00:33:53Z\",\n" +
                "    \"pushed_at\": 1688306770,\n" +
                "    \"git_url\": \"git://github.com/Ruokwok/Tetris.git\",\n" +
                "    \"ssh_url\": \"git@github.com:Ruokwok/Tetris.git\",\n" +
                "    \"clone_url\": \"https://github.com/Ruokwok/Tetris.git\",\n" +
                "    \"svn_url\": \"https://github.com/Ruokwok/Tetris\",\n" +
                "    \"homepage\": null,\n" +
                "    \"size\": 125,\n" +
                "    \"stargazers_count\": 3,\n" +
                "    \"watchers_count\": 3,\n" +
                "    \"language\": \"Java\",\n" +
                "    \"has_issues\": true,\n" +
                "    \"has_projects\": true,\n" +
                "    \"has_downloads\": true,\n" +
                "    \"has_wiki\": true,\n" +
                "    \"has_pages\": false,\n" +
                "    \"has_discussions\": false,\n" +
                "    \"forks_count\": 1,\n" +
                "    \"mirror_url\": null,\n" +
                "    \"archived\": false,\n" +
                "    \"disabled\": false,\n" +
                "    \"open_issues_count\": 0,\n" +
                "    \"license\": null,\n" +
                "    \"allow_forking\": true,\n" +
                "    \"is_template\": false,\n" +
                "    \"web_commit_signoff_required\": false,\n" +
                "    \"topics\": [\n" +
                "\n" +
                "    ],\n" +
                "    \"visibility\": \"public\",\n" +
                "    \"forks\": 1,\n" +
                "    \"open_issues\": 0,\n" +
                "    \"watchers\": 3,\n" +
                "    \"default_branch\": \"master\",\n" +
                "    \"stargazers\": 3,\n" +
                "    \"master_branch\": \"master\"\n" +
                "  },\n" +
                "  \"pusher\": {\n" +
                "    \"name\": \"Ruokwok\",\n" +
                "    \"email\": \"72373519+Ruokwok@users.noreply.github.com\"\n" +
                "  },\n" +
                "  \"sender\": {\n" +
                "    \"login\": \"Ruokwok\",\n" +
                "    \"id\": 72373519,\n" +
                "    \"node_id\": \"MDQ6VXNlcjcyMzczNTE5\",\n" +
                "    \"avatar_url\": \"https://avatars.githubusercontent.com/u/72373519?v=4\",\n" +
                "    \"gravatar_id\": \"\",\n" +
                "    \"url\": \"https://api.github.com/users/Ruokwok\",\n" +
                "    \"html_url\": \"https://github.com/Ruokwok\",\n" +
                "    \"followers_url\": \"https://api.github.com/users/Ruokwok/followers\",\n" +
                "    \"following_url\": \"https://api.github.com/users/Ruokwok/following{/other_user}\",\n" +
                "    \"gists_url\": \"https://api.github.com/users/Ruokwok/gists{/gist_id}\",\n" +
                "    \"starred_url\": \"https://api.github.com/users/Ruokwok/starred{/owner}{/repo}\",\n" +
                "    \"subscriptions_url\": \"https://api.github.com/users/Ruokwok/subscriptions\",\n" +
                "    \"organizations_url\": \"https://api.github.com/users/Ruokwok/orgs\",\n" +
                "    \"repos_url\": \"https://api.github.com/users/Ruokwok/repos\",\n" +
                "    \"events_url\": \"https://api.github.com/users/Ruokwok/events{/privacy}\",\n" +
                "    \"received_events_url\": \"https://api.github.com/users/Ruokwok/received_events\",\n" +
                "    \"type\": \"User\",\n" +
                "    \"site_admin\": false\n" +
                "  },\n" +
                "  \"created\": false,\n" +
                "  \"deleted\": false,\n" +
                "  \"forced\": false,\n" +
                "  \"base_ref\": null,\n" +
                "  \"compare\": \"https://github.com/Ruokwok/Tetris/compare/6d1b199e78ad...3a89021a09c6\",\n" +
                "  \"commits\": [\n" +
                "    {\n" +
                "      \"id\": \"3a89021a09c656b8e2365ec165a1eeb75f0f85a2\",\n" +
                "      \"tree_id\": \"f41b9afc99761437309268607dd2c192d9577c9e\",\n" +
                "      \"distinct\": true,\n" +
                "      \"message\": \"update pom.xml\",\n" +
                "      \"timestamp\": \"2023-07-02T22:05:59+08:00\",\n" +
                "      \"url\": \"https://github.com/Ruokwok/Tetris/commit/3a89021a09c656b8e2365ec165a1eeb75f0f85a2\",\n" +
                "      \"author\": {\n" +
                "        \"name\": \"Administrator\",\n" +
                "        \"email\": \"i@ruok.cc\",\n" +
                "        \"username\": \"Ruokwok\"\n" +
                "      },\n" +
                "      \"committer\": {\n" +
                "        \"name\": \"Administrator\",\n" +
                "        \"email\": \"i@ruok.cc\",\n" +
                "        \"username\": \"Ruokwok\"\n" +
                "      },\n" +
                "      \"added\": [\n" +
                "\n" +
                "      ],\n" +
                "      \"removed\": [\n" +
                "\n" +
                "      ],\n" +
                "      \"modified\": [\n" +
                "        \"pom.xml\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"head_commit\": {\n" +
                "    \"id\": \"3a89021a09c656b8e2365ec165a1eeb75f0f85a2\",\n" +
                "    \"tree_id\": \"f41b9afc99761437309268607dd2c192d9577c9e\",\n" +
                "    \"distinct\": true,\n" +
                "    \"message\": \"update pom.xml\",\n" +
                "    \"timestamp\": \"2023-07-02T22:05:59+08:00\",\n" +
                "    \"url\": \"https://github.com/Ruokwok/Tetris/commit/3a89021a09c656b8e2365ec165a1eeb75f0f85a2\",\n" +
                "    \"author\": {\n" +
                "      \"name\": \"Administrator\",\n" +
                "      \"email\": \"i@ruok.cc\",\n" +
                "      \"username\": \"Ruokwok\"\n" +
                "    },\n" +
                "    \"committer\": {\n" +
                "      \"name\": \"Administrator\",\n" +
                "      \"email\": \"i@ruok.cc\",\n" +
                "      \"username\": \"Ruokwok\"\n" +
                "    },\n" +
                "    \"added\": [\n" +
                "\n" +
                "    ],\n" +
                "    \"removed\": [\n" +
                "\n" +
                "    ],\n" +
                "    \"modified\": [\n" +
                "      \"pom.xml\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";
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
                    if (json != null) {
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
