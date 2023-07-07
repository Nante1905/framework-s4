# Description
Java web framework MVC

# Installation
Ajouter le fichier .jar dans la liste des librairies de votre projet

Ajouter une declaration de servlet dans votre web.xml
- servlet-name : FrontServlet
- servlet-class : etu1752.framework.servlet.FrontServlet
- url-mapping : *.etu

```xml
<servlet>
    <servlet-name>FrontServlet</servlet-name>
    <servlet-class>etu1752.framework.servlet.FrontServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>FrontServlet</servlet-name>
    <url-pattern>*.etu</url-pattern>
</servlet-mapping>
```

Vous pouver maintenant annoter vos fonctions dans vos class model :
- Annotation : etu1752.framework.decorators.App
- annotation parameters : url :string, method :string
- <strong>ATTENTION</strong> : toute url pour l'annotation doit commencer par ce modele => /votre-url.etu
```java
@App(url = "/hello.etu")
public void hello() {
    // process ...
}
```

# Les views
Pour qu'une methode annotée affiche un vue elle doit retourner un objet de type etu1752.framework.views.ModelView
Mettez les views que vous retourner dans un repertoire nommé "views" au meme niveau que WEB-INF
Les views sont en .jsp. <br>
Exemple : 
```java
@App(url = "/details")
public ModelView details() {
    // some code ...
    return new ModelView("details.jsp");
}
```
## Data binding model -> views
L'objet de type ModelView possede une methode addItem(key :string, value :Object). Utiliser cette methode pour passer des données vers la vue depuis votre model.

### Emp.java
```java
@App(url = "/hey.etu")
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
```

Pour recuperer l'objet passer dans la vue effectuer un request.getAttribute(key) ou 'key' est la clé que vous avez fournis depuis le model. Vous devez aussi caster l'objet reçu avec la class correspondante.

### emp.jsp

```jsp
<%@page import="app.models.Emp, java.util.Vector" %>

<% Vector<Emp> emps = (Vector<Emp>)request.getAttribute("emps"); %>
<% for(Emp e : emps) { %>
    <p><%= e.getName() %></p>
<% } %>
```

## Data binding views -> model
## 1- Formulaire
Les formulaires doivent avoir des inputs correspondant aux proprietes du model dont la fonction qui va être appele est issue
Les "name" des inputs doivent être le meme que les noms des propriétés du model

### form.jsp
```jsp
<form action="./add.etu" method="post">
    <input type="text" name="id" placeholder="id">
    <input type="text" name="name" placeholder="name">
    <input type="date" name="embauche">
    <input type="submit" value="Valider">
</form>
```

Pour utiliser les données obtenues depuis les views dans les class model, utiliser le contexte "this"

### Emp.java
```java
@App(url = "/add.etu")
public ModelView insert() {
    ModelView view = new ModelView("insert.jsp");

    view.addItem("emp", this);

    return view;
}
```

### insert.jsp
```jsp
<%@page import="app.models.Emp" %>

<% 
    Emp e = (Emp) request.getAttribute("emp");
%>
<p><%= e.getId() %></p>
<p><%= e.getName() %></p>
<p><%= e.getEmbauche() %></p>
```
## 2- Query params
On peut utiliser les query param pour passer des données depuis les vues vers le modele.

Par exemple on veut passer le données `id` vers la methode de modele details qui est appelée sur l'url /details.etu

Dans le model, on déclare la methode détails comme suit :
### Emp.java
```java
    @App(url = "/details.etu", method = "")
    public ModelView details(@Params(name = "id") int id) {
        // TODO ...
    }
```
La méthode details prend en parametre `int id` qui represente id depuis le query param. Il est décoré par `@Param(name = "id")` dont le name de @Param doit etre le meme que celui de votre query param

Dans notre exemple, l'url sera
```
    exemple.com/details.etu?id=2
```

La valeur de `id` dans la methode details sera alors 2

# Session
## 1 - Protection
Pour proteger vos methodes vous pouvez les annoter avec `@Auth(profile = "votreProfile")`.

Ajouter la variable init-param dans votre web.xml pour identifer le nom de la variable session utiliser pour proteger les methodes ici on utiles isconnected

```xml
<init-param>
    <param-name>sessionname</param-name>
    <param-value>isconnected</param-value>
</init-param>
```
Utiliser la methode addSession de ModelView pour ajouter l'utilisateur connecter

```java
@App(url = "/login.etu")
public ModelView login(@Params(name = "nom") String nom) {
    ModelView view = new ModelView("logged.jsp");
    view.addSession("isconnected", nom);
    // ...
    return view;
}
```
Les methodes sans annotations ou avec annotées `"public"` seront pas protegées.

## 2- Ajout de variable de session dans HttpSession
Vous pouvez utiliser la methode `addSession` de ModelView pour ajouter des variables de session qui seront directement stocker dans HttpSession.

## 3- Utilisation des variables de session dans les methodes d'actions
Vous pouvez creer dans votre modele une propriété `HashMap<String, Object> sessions` qui sera relié à `HttpSession`.
Si vous rajouter des objets ils seront enregister dans `HttpSession` et vous pourrez utiliser aussi ces variables dans les methodes d'action.

Annotez vos methodes avec `@Session()` pour les utiliser

## 4- Suppression des variables de session

Dans votre view vous avez la propriété `invalidateSession` de type boolean. Mettez le la valeur `true` pour supprimer toutes les variables de session.

Pour supprimer une session spécifique, ajouter son nom à `deleteSession` de ModelView.