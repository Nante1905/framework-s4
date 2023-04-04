package utils;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import jakarta.servlet.http.HttpServletRequest;

public class Utils {
    
    public static String getUrlPath(HttpServletRequest req) {
        return "/"+req.getRequestURI().split("/")[2];
    }

    public static Vector<String> getAllClass(File[] files, Vector<String> result) throws IOException {
        for (File file : files) {
            if(file.isDirectory()) {
                getAllClass(file.listFiles(), result);
            }
            else {
                String name = file.getAbsolutePath().replace("\\", ".");
                int index = name.indexOf(".classes.");
                result.add(name.substring(index+9, name.length()-6));
            }
        }
        return result;
    }
}
