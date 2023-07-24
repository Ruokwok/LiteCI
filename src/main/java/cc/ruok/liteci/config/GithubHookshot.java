package cc.ruok.liteci.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GithubHookshot {

    public static Map<String, List<BuildConfig.Commit>> map = new HashMap<>();

    public Commit[] commits;

    public static class Commit {

        public String id;
        public String message;
        public String url;
        public Author author;

    }

    public static class Author {

        public String username;

    }

}
