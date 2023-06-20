package etu1752.framework.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import etu1752.framework.Mapping;
import etu1752.framework.decorators.Auth;
import etu1752.framework.decorators.Params;
import etu1752.framework.decorators.Scope;
import etu1752.framework.decorators.Session;
import etu1752.framework.view.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import utils.FileUpload;
import utils.Utils;

/**
 * FrontServlet
 */


@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
maxFileSize = 1024 * 1024 * 10,      // 10 MB
maxRequestSize = 1024 * 1024 * 100)  // 100 MB)

@WebServlet(name = "FrontServlet", urlPatterns = { "*.etu", "/" })
public class FrontServlet extends HttpServlet {

    HashMap<String, Mapping> mappingUrls;
    HashMap<String, Object> singletons;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.mappingUrls = new HashMap<>();
            this.singletons = new HashMap<>();
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
            if(c.isAnnotationPresent(Scope.class)) {
                singletons.put(c.getName(), null);
            }

            Method[] methods = c.getDeclaredMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(etu1752.framework.decorators.App.class)) {
                    etu1752.framework.decorators.App a = m.getAnnotation(etu1752.framework.decorators.App.class);
                    Mapping map = new Mapping(c.getName(), m.getName());
                    map.setParamsTypes(m.getParameterTypes());
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
                    Method method = cls.getDeclaredMethod(u.getValue().getMethod(), u.getValue().getParamsTypes());

                    Object o = null;

                    if(this.singletons.containsKey(cls.getName())) {
                        if(this.singletons.get(cls.getName()) == null) {
                            o = cls.getConstructor().newInstance();
                            this.singletons.put(cls.getName(), o);
                        } else {
                            o = this.singletons.get(cls.getName());
                        }
                    }

                    if(o == null) {
                        o = cls.getConstructor().newInstance();
                    }
                    
                    // data binding fields

                    Field[] fields = o.getClass().getDeclaredFields();
                    for(Field f : fields) {
                        String fName = f.getName();
                        Method setter = o.getClass().getDeclaredMethod("set"+toCamel(fName), f.getType());


                        if(req.getContentType() != null && req.getContentType().startsWith("multipart/")) {
                            if(f.getType() == FileUpload.class) {
                                for(Part part : req.getParts()) {
                                    if(fName.equals(part.getName())) {
                                        FileUpload file = new FileUpload();
                                        file.setName(part.getSubmittedFileName());
                                        file.setBytes(part.getInputStream().readAllBytes());
    
                                        setter.invoke(o, file);
                                    }
                                }
                            } 
                        }
                        else {
                            if(req.getParameter(fName) != null && req.getParameter(fName) != "") {
                                // out.print(req.getParameter(fName));
                                Object value = Utils.strToObject(req.getParameter(fName), f.getType());
                                setter.invoke(o, value);
                            }
                        }
                    }

                    // for(Part part : req.getParts()) {
                    //     // debug
                    //     out.println(part.getSubmittedFileName());
                    // }
                    
                    // data binding parameters

                    Parameter[] methodParams = method.getParameters();
                    Object[] invokationParams = new Object[methodParams.length];
                    int i = 0;
                    for(Parameter p : methodParams) {
                        String paramName = p.getAnnotation(Params.class).name();
                        Enumeration<String> attrNames = req.getParameterNames();
                        while(attrNames.hasMoreElements()) {
                            if(paramName.equals(attrNames.nextElement())) {
                                invokationParams[i] = (Utils.strToObject(req.getParameter(paramName), p.getType()));
                                i++;
                            }
                        }
                        if(p.getType() == FileUpload.class) {
                            for(Part part : req.getParts()) {
                                if(paramName.equals(part.getName())) {
                                    FileUpload f = new FileUpload();
                                    f.setName(part.getSubmittedFileName());
                                    f.setBytes(part.getInputStream().readAllBytes());
    
                                    invokationParams[i] = f;
                                    i++;
                                }
                            }
                        }
                    }

                    // session binding
                    
                    if(method.isAnnotationPresent(Session.class)) {
                        Field sessionField = o.getClass().getDeclaredField("sessions");
                        sessionField.setAccessible(true);
                        HashMap<String, Object> sessions = (HashMap<String, Object>)sessionField.get(o);

                        Enumeration<String> names = req.getSession().getAttributeNames();
                        while(names.hasMoreElements()) {
                            String sessionName = names.nextElement();
                            Object sessionObject = req.getSession().getAttribute(sessionName);
                            sessions.put(sessionName, sessionObject);
                        }
                    }

                    // auth

                    ModelView view = null;

                    if(method.isAnnotationPresent(Auth.class)) {
                        HttpSession servletSession = req.getSession();
                        String authProfile = method.getAnnotation(Auth.class).profile();
                        String sessionKey = this.getInitParameter("sessionname");
                        
                        String currentUser = (String) servletSession.getAttribute(sessionKey);
                        if(currentUser == null) {
                            throw new ServletException("Invalid session");
                        }
                        else {
                            if(currentUser.equals(authProfile)) {
                                view = (ModelView) method.invoke(o, invokationParams);
                            }
                            else if(currentUser.equals(authProfile) == false && authProfile.equals("")) {
                                view = (ModelView) method.invoke(o, invokationParams);
                            }
                            else if(currentUser.equals(authProfile) == false && authProfile.equals("") == false) {
                                throw new ServletException("Invalid session");
                            }
                        }
                    }

                    else if(method.isAnnotationPresent(Auth.class) == false) {
                        view = (ModelView) method.invoke(o, invokationParams);
                    }

                    if(view != null) {
                        HashMap<String, Object> data = view.getData();

                        for (Map.Entry<String,Object> reqData : data.entrySet()) {
                            req.setAttribute(reqData.getKey(), reqData.getValue());
                        }

                        HashMap<String, String> sessions = view.getSessions();
                        for(Map.Entry<String, String> session : sessions.entrySet()) {
                            req.getSession().setAttribute(session.getKey(), session.getValue());
                        }
                    }

                    // session binding
                    
                    out.println(method.isAnnotationPresent(Session.class));
                    if(method.isAnnotationPresent(Session.class)) {
                        Field sessionField = o.getClass().getDeclaredField("sessions");
                        sessionField.setAccessible(true);
                        HashMap<String, Object> sessions = (HashMap<String, Object>)sessionField.get(o);
                        
                        for(Map.Entry<String, Object> s : sessions.entrySet()) {
                            req.getSession().setAttribute(s.getKey(), s.getValue());
                        }

                        out.println(req.getSession().getAttribute("nantesession"));

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