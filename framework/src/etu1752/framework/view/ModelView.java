package etu1752.framework.view;

import java.util.HashMap;
import java.util.Vector;

public class ModelView {
    String view;
    HashMap<String, Object> data;
    HashMap<String, String> sessions;
    boolean isJson;
    boolean invalidateSession;
    Vector<String> deleteSession;

    public ModelView() {
        this.data = new HashMap<String, Object>();
        this.sessions = new HashMap<String, String>();
        this.deleteSession = new Vector<String>();
    }
    public ModelView(String view) {
        this.view = view;
        this.data = new HashMap<String, Object>();
        this.sessions = new HashMap<String, String>();
        this.deleteSession = new Vector<String>();
    }
    
    public String getView() {
        return view;
    }
    
    public void setView(String view) {
        this.view = view;
    }

    public HashMap<String, Object> getData() {
        return data;
    }
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public void addItem(String key, Object value) {
        this.data.put(key, value);
    }
    public HashMap<String, String> getSessions() {
        return sessions;
    }
    public void setSessions(HashMap<String, String> sessions) {
        this.sessions = sessions;
    }
    public void addSession(String key, String value) {
        this.sessions.put(key, value);
    }

    
    public boolean isJson() {
        return isJson;
    }
    public void setJson(boolean isJson) {
        this.isJson = isJson;
    }
    public boolean isInvalidateSession() {
        return invalidateSession;
    }
    public void setInvalidateSession(boolean invalidateSession) {
        this.invalidateSession = invalidateSession;
    }
    public Vector<String> getDeleteSession() {
        return deleteSession;
    }
    public void addDeleteSession(String value) {
        this.deleteSession.add(value);
    }
    
}
