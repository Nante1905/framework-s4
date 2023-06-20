package app.models;

import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Vector;

import etu1752.framework.decorators.App;
import etu1752.framework.decorators.Auth;
import etu1752.framework.decorators.Params;
import etu1752.framework.decorators.Scope;
import etu1752.framework.view.ModelView;
import utils.FileUpload;

@Scope()
public class Emp {
    int id;
    String name;
    Date embauche;
    Time temps;
    int count;
    FileUpload f;

    public Time getTemps() {
        return temps;
    }


    public void setTemps(Time temps) {
        this.temps = temps;
    }


    public Date getEmbauche() {
        return embauche;
    }


    public void setEmbauche(Date embauche) {
        this.embauche = embauche;
    }


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

    @App(url = "/add.etu", method = "post")
    public ModelView insert() {
        ModelView view = new ModelView("insert.jsp");

        view.addItem("emp", this);

        return view;
    }

    @App(url = "/details.etu", method = "")
    public ModelView details(@Params(name = "id") int id) {
        ModelView view = new ModelView("details.jsp");
        view.addItem("number", id + 1);
        return view;
    }

    @App(url = "/upload", method = "")
    public ModelView testUpload(@Params(name = "nante") FileUpload file) {
        ModelView view = new ModelView("up.jsp");

        view.addItem("fileUp", file.getName());

        return view;
    }

    @App(url = "/count.etu", method = "")
    public ModelView count() {
        ModelView view = new ModelView("count.jsp");
        view.addItem("count", this.count);
        return view;
    }

    @App(url = "/addcount.etu", method = "")
    public ModelView addCount(@Params(name = "number") int number) {
        ModelView view = new ModelView("count.jsp");
        this.count += number;
        view.addItem("count", this.count);
        return view;
    }

    @App(url = "/login.etu", method = "")
    public ModelView login(@Params(name = "nom") String nom) {
        ModelView view = new ModelView("logged.jsp");
        view.addSession("isconnected", nom);
        view.addItem("session", nom);
        return view;
    }

    @App(url = "/nante.etu", method = "")
    @Auth(profile = "nante")
    public ModelView nante() {
        return new ModelView("nante.jsp");
    }

    @App(url = "/public.etu", method = "")
    @Auth(profile = "")
    public ModelView all() {
        return new ModelView("public.jsp");
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    // public void setId(String id) {
    //     this.id = Integer.parseInt(id);
    // }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public int getCount() {
        return count;
    }


    public void setCount(int count) {
        this.count = count;
    }


    public FileUpload getF() {
        return f;
    }


    public void setF(FileUpload f) {
        this.f = f;
    }
    
    
}
