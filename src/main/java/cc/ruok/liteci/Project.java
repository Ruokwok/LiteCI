package cc.ruok.liteci;

import cc.ruok.liteci.i18n.L;

import java.io.File;

public class Project {

    public static String checkPath(String path, String name) {
        if (path == null || path.isEmpty()) return L.get("project.path.null");
        if (name == null || name.isEmpty()) return L.get("project.name.null");
        File file = new File(LiteCI.WORKSPACE + path);
        if (!file.exists()) return L.get("project.path.null");
        return null;
    }

    public static String createDir(String path, String name) {
        String s = checkPath(path, name);
        if (s != null) return s;
        File file = new File(LiteCI.WORKSPACE + path + "/" + name);
        if (file.exists()) return L.get("project.target.exists");
        file.mkdir();
        return null;
    }

}
