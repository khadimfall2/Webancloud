package counters;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;

@WebServlet(name = "TheCount", urlPatterns = { "/thecount" })
public class TheGlobalCounter extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = ds.beginTransaction();

        try {
            Key counterKey = KeyFactory.createKey("Counter", "TheCounter");
            Entity counterEntity;

            try {
                // Récupérer l'entité Counter si elle existe
                counterEntity = ds.get(counterKey);
            } catch (EntityNotFoundException e) {
                // Si l'entité n'existe pas, en créer une nouvelle
                counterEntity = new Entity("Counter", "TheCounter");
                counterEntity.setProperty("val", 0L);
                ds.put(counterEntity);
            }

            // Incrémenter la valeur du compteur
            Long value = (Long) counterEntity.getProperty("val");
            counterEntity.setProperty("val", value + 1);
            ds.put(counterEntity);

            txn.commit();

            // Afficher la nouvelle valeur du compteur
            response.getWriter().print("Final value: " + value + 1);

        } catch (Exception e) {
            response.getWriter().print("Error: " + e.getMessage());
        } finally {
            if (txn.isActive()) {
                txn.rollback(); // Si la transaction n'est pas terminée, la rollback
            }
        }
    }
}

