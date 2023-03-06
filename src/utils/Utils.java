package utils;

import jakarta.servlet.http.HttpServletRequest;

public class Utils {
    
    public static String getUrlPath(HttpServletRequest req) {
        return req.getPathInfo();
    }
}
