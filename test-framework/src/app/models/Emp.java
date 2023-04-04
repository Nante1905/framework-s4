package app.models;

import java.util.HashMap;

import etu1752.framework.decorators.App;
import etu1752.framework.view.ModelView;

public class Emp {
    int id;
    String name;

    public Emp() {
        
    }

    @App(url = "/hey.etu" , method = "get")
    public ModelView sayHey() {
        return new ModelView("emp.jsp");
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
