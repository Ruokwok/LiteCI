package cc.ruok.liteci.project;

import cc.ruok.liteci.Logger;
import cc.ruok.liteci.i18n.L;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Dir extends Project {

    public Dir(File file, Dir father) {
        super(file, father);
        this.name = file.getName();
        File[] files = file.listFiles();
        if (files == null) return;
        internal = new ArrayList<>();
        for (File _file : files) {
            if (_file.isDirectory()) {
                internal.add(new Dir(_file, this));
            } else {
                try {
                    internal.add(new Job(_file, this));
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
