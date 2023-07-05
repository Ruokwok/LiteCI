package cc.ruok.liteci.i18n;

import cc.ruok.liteci.LiteCI;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class L {

    public static Map<String, String> lang;

    public static void load(String l) {
        try {
            lang = loadLang(L.class.getResourceAsStream("/lang/" + l + ".lang"));
        } catch (IOException e) {
            try {
                lang = loadLang(L.class.getResourceAsStream("/lang/chs.lang"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static String format(String str) {
        for (Map.Entry<String, String> entry : lang.entrySet()) {
            str = str.replaceAll(entry.getKey(), entry.getValue());
        }
        str = str.replaceAll("\\{config\\.title}", LiteCI.serverConfig.title);
        return str;
    }

    private static Map<String, String> loadLang(InputStream stream) throws IOException {
        String content = IOUtils.toString(stream, StandardCharsets.UTF_8);
        Map<String, String> d = new HashMap<>();
        for (String line : content.split("\n")) {
            line = line.trim();
            if (line.equals("") || line.charAt(0) == '#') {
                continue;
            }
            String[] t = line.split("=");
            if (t.length < 2) {
                continue;
            }
            String key = t[0];
            String value = "";
            for (int i = 1; i < t.length - 1; i++) {
                value += t[i] + "=";
            }
            value += t[t.length - 1];
            if (value.equals("")) {
                continue;
            }
            d.put("\\{" + key + "}", value);
        }
        return d;
    }

}
