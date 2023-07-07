package cc.ruok.liteci.project;

import java.io.File;
import java.util.ArrayList;

public class Dir extends Project {

    public Dir(File file, Dir father) {
        super(file, father);
        File[] files = file.listFiles();
        if (files == null) return;
        internal = new ArrayList<>();
        for (File _file : files) {
            if (_file.isDirectory()) {
                internal.add(new Dir(_file, this));
            } else {
                internal.add(new Job(_file, this));
            }
        }
    }

    @Override
    public boolean isDir() {
        return true;
    }
}
