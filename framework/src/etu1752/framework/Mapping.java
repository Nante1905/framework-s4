package etu1752.framework;

public class Mapping {
    String className;
    String method;

    
    public Mapping() {
        
    }
    
    public Mapping(String className, String method) {
        setClassName(className);
        setMethod(method);
    }
    
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
}
