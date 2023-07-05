package cc.ruok.liteci.i18n;

import static cc.ruok.liteci.servlet.ServerServlet.getResourcesToString;

public class Format {

    public static String language(String str) {
        return str;
    }

    public static String res(String url, String html) {
        html = html.replace("{include.overview}", getResourcesToString(url + ".html"));
        html = html.replace("{include.js}", "/js/" + url + ".js");
        return html;
    }

}
