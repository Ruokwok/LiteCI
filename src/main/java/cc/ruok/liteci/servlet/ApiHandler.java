package cc.ruok.liteci.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ApiHandler {

    void handler(String str, HttpServletRequest req, HttpServletResponse resp) throws IOException;

}
