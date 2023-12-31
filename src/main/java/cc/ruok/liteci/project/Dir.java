package cc.ruok.liteci.project;

import cc.ruok.liteci.LiteCI;
import cc.ruok.liteci.Logger;
import cc.ruok.liteci.config.Description;
import cc.ruok.liteci.i18n.L;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Dir extends Project {

    public Description description;

    public Dir(File file, Dir father) {
        super(file, father);
        this.name = file.getName();
        if (!file.getPath().equals("jobs")) {
            this.description = new Description(new File(file + "/.description"));
        } else {
            this.description = new Description(LiteCI.DESC_FILE);
        }
        File[] files = file.listFiles();
        if (files == null) return;
        internal = new HashMap<>();
        for (File _file : files) {
            if (_file.getName().equals(".description")) continue;
            if (_file.isDirectory()) {
                internal.put(_file.getName(), new Dir(_file, this));
            } else {
                try {
                    Job job = new Job(_file, this);
                    internal.put(job.name, job);
                } catch (Exception e) {
                    Logger.warning(L.get("project.read.fail") + ": " + _file);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean isDir() {
        return true;
    }

    @Override
    public String getDescription() {
        if (description == null) return "";
        return description.toString();
    }

    public Map<String, Project> getSons() {
        return internal;
    }

    public boolean isRoot() {
        return name.equals("/");
    }
}
