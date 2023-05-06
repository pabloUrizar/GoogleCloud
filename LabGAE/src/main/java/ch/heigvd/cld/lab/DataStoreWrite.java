package ch.heigvd.cld.lab;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "DatastoreWrite", value = "/datastorewrite")
public class DataStoreWrite extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        ServletOutputStream out = resp.getOutputStream();

        // Retrieve parameters list
        List<String> parameters = Collections.list(req.getParameterNames());

        // Copy entity kind
        if (parameters.contains("_kind")) {
            out.println("Writing entity to datastore.");
            String kind = req.getParameter("_kind");
            String key = "";

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            Entity entity;

            // Check either the _key parameter exists or not
            if(parameters.contains("_key")) {
                out.println("_key parameter is provided");
                key = req.getParameter("_key");
                entity = new Entity(kind, key);
            }
            else {
                out.println("_key parameter is not provided. Name/ID will be automatically generated.");
                entity = new Entity(kind);
            }

            // Create all others key-value except for _key and _kind
            for(String parameter : parameters) {
                if(!Objects.equals(parameter, "_key") && !Objects.equals(parameter, "_kind")) {
                    String value = req.getParameter(parameter);
                    out.println("Setting property : " + parameter + " with value : " + value);
                    entity.setProperty(parameter, value);
                }
            }
            out.println("Adding entity to datastore.");
            datastore.put(entity);
        } else {
            out.println("The _kind parameter is missing.");
        }
        out.flush();
        out.close();
    }
}
