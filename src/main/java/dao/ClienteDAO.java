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
    //Colección de MongoDB donde se almacenan los documentos de cliente.
    private MongoCollection<Document> col;
    
    //constructor
    public ClienteDAO() {
        // Obtiene la colección "cliente" desde la conexión a MongoDB
        col = MongoDB.getDatabase().getCollection("clientes");
    }
    
    //Inserta un objeto Cliente en la db
    public void insert(Cliente c){
        
        // Crear un documento BSON con los campos del cliente
        Document d = new Document()
                .append("nombres", c.getNombre())
                .append("apellidos", c.getApellido())
                .append("licencia", c.getLicencia())
                .append("telefono", c.getTelefono())
                .append("correo", c.getCorreo());
        //insertar documento a la coleccion de cliente
        col.insertOne(d);
        // Obtener el ID generado por MongoDB y asignarlo al objeto cliente
        
        c.setId(d.getObjectId("_id"));
    }
    
    //Busca un cliente en la db
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
                d.getString("telefono"),
                d.getString("correo"),
                d.getString("nombres"),
                d.getString("apellidos"));
        c.setId(d.getObjectId("_id"));
     
        // Devolver el empleado encontrado
        return c;
    }
    
    //retorna todos los clientes
    public List<Cliente> findAllClientes() {
        List<Cliente> clientes = new ArrayList<>();
         //crea un objeto por cada documento y los enlist
        for (Document d : col.find()) {
            Cliente c = new Cliente(
                d.getString("licencia"),
                d.getString("telefono"),
                d.getString("correo"),
                d.getString("nombres"),
                d.getString("apellidos"));
            c.setId(d.getObjectId("_id"));
        
            clientes.add(c);
        }
        //devuelve la lista
        return clientes;
    }
    
    //actualiza clientes
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
         // Buscar el primer documento donde el campo "id" coincida con id
        Document d = col.find(Filters.eq("_id", id)).first();
        // Si no existe ningún cliente con ese id, retornar null
        if (d == null) {
            return false;
        }
        //borra el documento
        col.deleteOne(d);
        return true;
     }
}
