package cc.ruok.liteci.i18n;

import static cc.ruok.liteci.servlet.ServerServlet.getResourcesToString;

public class HTML {

    public static String res(String url, String html) {
        html = html.replace("{include.overview}", getResourcesToString(url + ".html"));
        html = html.replace("{include.js}", "/js/" + url + ".js");
        return html;
    }

    private String html;
    private int level;

    public HTML(String html, int level) {
        this.html = html;
        this.level = level;
    }

    @Override
    public String toString() {
        return html;
    }

    public int getLevel() {
        return level;
    }
}
