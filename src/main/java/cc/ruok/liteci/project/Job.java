package cc.ruok.liteci.project;

import java.io.File;

public class Job extends Project {

    public Job(File file, Dir father) {
        super(file, father);
    }

    @Override
    public boolean isDir() {
        return false;
    }
}
