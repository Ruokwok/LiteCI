package cc.ruok.liteci.json;

import cc.ruok.liteci.config.BuildConfig;

import java.util.List;

public class BuildJson extends Json {

    public String name;
    public int id;
    public int status;
    public long date;
    public long time;
    public int exit;
    public String[] output;
    public List<JobJson.File> artifacts;
    public BuildConfig.Trigger trigger;
    public List<BuildConfig.Commit> commits;
}
