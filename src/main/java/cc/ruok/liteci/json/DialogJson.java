package cc.ruok.liteci.json;

public class DialogJson extends Json {

    public String type = "dialog";
    public String content;

    public DialogJson(String content) {
        this.content = content;
    }

}
