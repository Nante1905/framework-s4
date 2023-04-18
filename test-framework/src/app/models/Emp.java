package app.models;

import java.util.HashMap;
import java.util.Vector;

import etu1752.framework.decorators.App;
import etu1752.framework.view.ModelView;

public class Emp {
    int id;
    String name;

    public Emp() {
        
    }
    

    public Emp(int id, String name) {
        this.id = id;
        this.name = name;
    }


    @App(url = "/hey.etu" , method = "get")
    public ModelView sayHey() {
        ModelView view = new ModelView("emp.jsp");

        Vector<Emp> emps = new Vector<>();
        Emp e1 = new Emp(1, "Jean");
        Emp e2 = new Emp(2, "Jak");
        Emp e3 = new Emp(3, "Haha");

        emps.add(e1);
        emps.add(e2);
        emps.add(e3);

        view.addItem("emps", emps);


        return view;
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
