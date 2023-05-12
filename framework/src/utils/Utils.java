package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Vector;

import jakarta.servlet.http.HttpServletRequest;

public class Utils {
    
    public static String getUrlPath(HttpServletRequest req) {
        if(req.getRequestURI().split("/").length >= 3)
            return "/"+req.getRequestURI().split("/")[2];
        else
            return "/";
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

    public static Object strToObject(String toParse, Class type) throws Exception {
    
        if(toParse == "") {
            return null;
        }
        if(type == int.class || type == Integer.class) {
            return Integer.parseInt(toParse);
        }
        else if(type == double.class || type == Double.class) {
            return Double.parseDouble(toParse);
        }
        else if(type == float.class || type == Float.class) {
            return Float.parseFloat(toParse);
        }
        else if(type == Date.class) {
            return Date.valueOf(toParse);
        }
        else if(type == Time.class) {
            return Time.valueOf(toParse + ":00");
        }
        else if(type == Timestamp.class) {
            return Timestamp.valueOf(toParse);
        }
        else if(type == String.class) {
            return toParse;
        }

        else {
            throw new Exception("Type not supported exception");
        }
    }
}
