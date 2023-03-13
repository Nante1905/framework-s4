package app.models;

import etu1752.framework.decorators.App;

public class Emp {
    int id;
    String name;

    @App(url = "/hey", method = "post")
    public void sayHey() {
        
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
