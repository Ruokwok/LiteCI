package cc.ruok.liteci.config;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Description {

    private File file;
    private String description;

    public Description(File file) {
        this.file = file;
        try {
            this.description = FileUtils.readFileToString(file, "utf8");
        } catch (IOException e) {
            this.description = "";
        }
    }

    public Description(String str) {
        description = str;
    }

    @Override
    public String toString() {
        return description;
    }

    public void set(String str) throws IOException {
        description = str;
        if (file != null) FileUtils.write(file, str, "utf8");
    }
}
