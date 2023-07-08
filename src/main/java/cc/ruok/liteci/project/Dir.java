package cc.ruok.liteci.project;

import cc.ruok.liteci.Logger;
import cc.ruok.liteci.i18n.L;

import java.io.File;
import java.util.HashMap;

public class Dir extends Project {

    public Dir(File file, Dir father) {
        super(file, father);
        this.name = file.getName();
        File[] files = file.listFiles();
        if (files == null) return;
        internal = new HashMap<>();
        for (File _file : files) {
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
}
