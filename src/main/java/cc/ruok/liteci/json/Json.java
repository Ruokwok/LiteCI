package cc.ruok.liteci.json;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Json {

    public Map<String, String> params = new HashMap<>();
    public List<String> list = new ArrayList<>();

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
