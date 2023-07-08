package cc.ruok.liteci.json;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Json {

    public Map<String, String> params = new HashMap<>();

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
