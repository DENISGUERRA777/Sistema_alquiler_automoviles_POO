
package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import modelo.MongoDB;
import modelo.Alquiler;

/**
 * Clase para manejo de la coleccion empleado junto con la base de datos
 * @author halfa
 */
public class AlquilerDAO {
    //Colección de MongoDB donde se almacenan los documentos de empleados y usuarios.
    private MongoCollection<Document> col;
    
    public AlquilerDAO() {
        // Obtiene la colección "alquileres" desde la conexión a MongoDB
        col = MongoDB.getDatabase().getCollection("alquileres");
        
    }
    //Inserta un objeto alquiler en la Db
    public void insert(Alquiler a){
        // Crear un documento BSON con los campos del alquiler
        Document d = new Document()
                .append("fechaInicio", a.getFechaInicio())
                .append("fechaFin", a.getFechaFin())
                .append("totalPago", a.getTotalPago());
        //insertar documento a la coleccion de alquileres
        col.insertOne(d);
        // Obtener el ID generado por MongoDB y asignarlo al objeto vehiculo
        a.setId(d.getObjectId("_id"));
    }
    //Buscar un alquiler en la BD
    public Alquiler findByCode(double totalPago){
        // Buscar el primer documento donde el campo "total pago" coincida con el pago total del alquiler
        Document d = col.find(Filters.eq("totalPago", totalPago)).first();
        // Si no existe ningún cliente con ese alquiler, retornar null
        if (d == null) {
            return null;
        }
        // Crear un nuevo objeto alquiler y mapear los campos desde el documento BSON
        Alquiler a = new Alquiler(
                d.getString("fechaInicio"),
                d.getString("marca"),
                d.getDouble("totalPago"));
        a.setId(d.getObjectId("_id"));
        
        // Devolver el alquiler encontrado
        return a;
       
    }
    
    //retornar todos los alquileres
    public List<Alquiler>  findAllAlquileres() {
        List<Alquiler> alquileres = new ArrayList<>();
        //crea un objeto por cada documento y los enlista
        for (Document d : col.find()) {
            Alquiler a = new Alquiler(
                d.getString("fechaInicio"),
                d.getString("fechaFin"),
                d.getDouble("totalPago"));
            a.setId(d.getObjectId("_id"));
        
            alquileres.add(a);
        }
        //devuelve la lista
        return alquileres;
    }
    //actualiza alquileres
    public boolean updateAlquiler(Alquiler a) {
        // Usamos set para actualizar campos específicos
        UpdateResult result = col.updateOne(
                Filters.eq("_id", a.getId()),
                Updates.combine(
                        Updates.set("fechaInicio", a.getFechaInicio()),
                        Updates.set("fechaFin", a.getFechaFin()),
                        Updates.set("totalPago", a.getTotalPago())
                    
                )
        );
        return result.getModifiedCount() > 0;
    }
    //elimina Alquiler
     public boolean deleteAlquiler(ObjectId id){
         // Buscar el primer documento donde el campo "id" coincida con nuestro id 
        Document d = col.find(Filters.eq("_id", id)).first();
        // Si no existe ningún vehiculo con id, retornar null
        if (d == null) {
            return false;
        }
        //borra el documento
        col.deleteOne(d);
        return true;
     }
}