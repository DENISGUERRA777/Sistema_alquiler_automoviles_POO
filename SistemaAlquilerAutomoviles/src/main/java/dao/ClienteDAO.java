/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;
import modelo.MongoDB;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author denis
 */
public class ClienteDAO {
    //Colección de MongoDB donde se almacenan los documentos de empleados y usuarios.
    private MongoCollection<Document> col;
    
    public ClienteDAO() {
        // Obtiene la colección "empleados" desde la conexión a MongoDB
        col = MongoDB.getDatabase().getCollection("clientes");
    }
    
    public void insert(Cliente c){
        
        // Crear un documento BSON con los campos del empleado
        Document d = new Document()
                .append("nombres", c.getNombre())
                .append("apellidos", c.getApellido())
                .append("licencia", c.getLicencia())
                .append("telefono", c.getTelefono())
                .append("correo", c.getCorreo());
        //insertar documento a la coleccion de empleado
        col.insertOne(d);
        // Obtener el ID generado por MongoDB y asignarlo al objeto cliente
        
        c.setId(d.getObjectId("_id"));
    }
    
    public Cliente findByLicence(String licencia){
        // Buscar el primer documento donde el campo "licencia" coincida con la licencia de conducir 
        Document d = col.find(Filters.eq("licencia", licencia)).first();
        // Si no existe ningún cliente con esa licencia, retornar null
        if (d == null) {
            return null;
        }
        // Crear un nuevo objeto cliente y mapear los campos desde el documento BSON
        Cliente c = new Cliente(
                d.getString("licencia"),
                d.getString("telefono"), d.getString("correo"),
                d.getString("nombres"), d.getString("apellidos"));
        c.setId(d.getObjectId("_id"));
     
        // Devolver el empleado encontrado
        return c;
    }
    
    //Metodo para retornar todos los empleados
    public List<Cliente> findAllClientes() {
        List<Cliente> clientes = new ArrayList<>();
        for (Document d : col.find()) {
            Cliente c = new Cliente(
                d.getString("licencia"),
                d.getString("telefono"), d.getString("correo"),
                d.getString("nombres"), d.getString("apellidos"));
            c.setId(d.getObjectId("_id"));
        
            clientes.add(c);
        }
        return clientes;
    }
    
    //Metodo para actualizar clientes
    public boolean updateCliente(Cliente c) {
        // Usamos set para actualizar campos específicos
        UpdateResult result = col.updateOne(
                Filters.eq("_id", c.getId()),
                Updates.combine(
                        Updates.set("nombres", c.getNombre()),
                        Updates.set("apellidos", c.getApellido()),
                        Updates.set("licencia", c.getLicencia()),
                        Updates.set("telefono", c.getTelefono()),
                        Updates.set("correo", c.getCorreo())
                )
        );
        return result.getModifiedCount() > 0;
    }
    
    //elimina cliente
     public boolean deleteCliente(ObjectId id){
         // Buscar el primer documento donde el campo "licencia" coincida con la licencia de conducir 
        Document d = col.find(Filters.eq("_id", id)).first();
        // Si no existe ningún cliente con esa licencia, retornar null
        if (d == null) {
            return false;
        }
        //borra el documento
        col.deleteOne(d);
        return true;
     }
}
