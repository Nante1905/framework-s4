package etu1752.framework.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import etu1752.framework.Mapping;
import etu1752.framework.view.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.Utils;

/**
 * FrontServlet
 */

@WebServlet(name = "FrontServlet", urlPatterns = { "*.etu", "/" })
public class FrontServlet extends HttpServlet {

    HashMap<String, Mapping> mappingUrls;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.mappingUrls = new HashMap<>();
            this.initialize(this.mappingUrls);
        } catch (ClassNotFoundException | IOException e) {
            
        }
    }

    
    private void initialize(HashMap<String, Mapping> urlsMap) throws IOException, ClassNotFoundException {
        List<Class<Object>> cls = new ArrayList<Class<Object>>();
        URL url = getServletContext().getResource("/WEB-INF/classes");
        String path = url.toString().replace("%20", " ").substring(6);
        File dir = new File(path);
        File[] files = dir.listFiles();
        Vector<String> classNames = new Vector<>();
        Utils.getAllClass(files, classNames);
        
        for (String className : classNames) {
            cls.add((Class<Object>) Class.forName(className));
        }
        
        for (Class<?> c : cls) {
            Method[] methods = c.getDeclaredMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(etu1752.framework.decorators.App.class)) {
                    etu1752.framework.decorators.App a = m.getAnnotation(etu1752.framework.decorators.App.class);
                    Mapping map = new Mapping(c.getName(), m.getName());
                    urlsMap.put(a.url(), map);
                }
            }
        }
    }

    private String toCamel(String name) {
        return name.replaceFirst(String.valueOf(name.charAt(0)), String.valueOf(name.toUpperCase().charAt(0)));
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        String path = Utils.getUrlPath(req);
        out.println("path url => " + path);
        out.println("url debug "+req.getRequestURI());
        
        for (Map.Entry<String, Mapping> u : this.mappingUrls.entrySet()) {
            // debug
            // out.println(u.getValue().getMethod());
            // for(Class<?> c : u.getValue().getParamsTypes()) {
            //     out.println(c.getName());
            // }
            if(u.getKey().equals(path)) {
                try {
                    Class<?> cls = (Class<?>) Class.forName(u.getValue().getClassName());
                    Method method = cls.getDeclaredMethod(u.getValue().getMethod());

                    Object o = cls.getConstructor().newInstance();

                    
                    Field[] fields = o.getClass().getDeclaredFields();
                    for(Field f : fields) {
                        String fName = f.getName();
                        Method setter = o.getClass().getDeclaredMethod("set"+toCamel(fName), f.getType());

                        if(req.getParameter(fName) != null && req.getParameter(fName) != "") {
                            out.print(req.getParameter(fName));
                            Object value = Utils.strToObject(req.getParameter(fName), f.getType());
                            setter.invoke(o, value);
                        }
                    }


                    ModelView view = (ModelView) method.invoke(o);
                    HashMap<String, Object> data = view.getData();

                    for (Map.Entry<String,Object> reqData : data.entrySet()) {
                        req.setAttribute(reqData.getKey(), reqData.getValue());
                    }

                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/views/"+view.getView());
                    dispatcher.forward(req, res);
                } catch (Exception e) {
                    e.printStackTrace(out);
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}