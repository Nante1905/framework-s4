package etu1752.framework.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
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
        // try {
        // this.initialize(this.mappingUrls);
        // } catch (ClassNotFoundException | IOException e) {
        // e.printStackTrace();
        // }
    }

    private void initialize(HashMap<String, Mapping> urlsMap) throws IOException, ClassNotFoundException {
        List<Class<Object>> cls = new ArrayList<Class<Object>>();
        URL url = getServletContext().getResource("/WEB-INF/classes");
        String path = url.toString().replace("%20", " ").substring(6);
        // out.println(path);
        File dir = new File(path);
        // out.println("existing : " + dir.exists());
        File[] files = dir.listFiles();
        // out.print(files.length);
        Vector<String> classNames = new Vector<>();
        Utils.getAllClass(files, classNames);

        for (String className : classNames) {
            // out.println(className);
            cls.add((Class<Object>) Class.forName(className));
        }

        for (Class c : cls) {
            Method[] methods = c.getDeclaredMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(etu1752.framework.decorators.App.class)) {
                    etu1752.framework.decorators.App a = m.getAnnotation(etu1752.framework.decorators.App.class);
                    Mapping map = new Mapping(c.getName(), m.getName());
                    urlsMap.put(a.url(), map);
                }
            }
        }

        // for (Map.Entry<String,String> u : urlsMap.entrySet()) {
        // System.out.println(u.getKey() + " => " + u.getValue());
        // }
        // }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        String path = Utils.getUrlPath(req);
        out.println("path url => " + path);
        out.println("url debug "+req.getRequestURI());

        // out.println(this.mappingUrls.size());
        for (Map.Entry<String, Mapping> u : this.mappingUrls.entrySet()) {
            // out.println(u.getKey() + " => " + u.getValue().getClassName() + " " + u.getValue().getMethod());
            if(u.getKey().equals(path)) {
                try {
                    Class cls = (Class) Class.forName(u.getValue().getClassName());
                    Method method = cls.getDeclaredMethod(u.getValue().getMethod());

                    Object o = cls.getConstructor().newInstance();

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
}