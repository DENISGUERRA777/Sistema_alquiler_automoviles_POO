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
import modelo.Vehiculo;
import modelo.MongoDB;
import modelo.Proveedor;
import modelo.TipoVehiculo;
import modelo.TipoProveedor;
import org.bson.Document;
import org.bson.types.ObjectId;
/**
 *
 * @author denis
 */
public class VehiculoDAO {
    //Colección de MongoDB donde se almacenan los documentos de empleados y usuarios.
    private MongoCollection<Document> col;
    
    //constructor
    public VehiculoDAO() {
        // Obtiene la colección "cliente" desde la conexión a MongoDB
        col = MongoDB.getDatabase().getCollection("vehiculos");
    }
    
    //Inserta un objeto vehiculo en la Db
    public void insert(Vehiculo v){
        
        //lista los documentos proveedor para anidarlos en la coleccion
        List<Document>proveedores = new ArrayList<>();
        for(Proveedor p : v.getProveedores()){
            proveedores.add(new Document()
                    .append("nombre", p.getNombre())
                    .append("tipo", p.getTipo()));
        }
        
        // Crear un documento BSON con los campos del vehiculo
        Document d = new Document()
                .append("codigo", v.getCodigo())
                .append("marca", v.getMarca())
                .append("modelo", v.getModelo())
                .append("año", v.getAño())
                .append("tipo", v.getTipo())
                .append("precioDia", v.getPrecioDia())
                .append("disponible", v.isDisponible())
                .append("proveedores", proveedores);
        
        //insertar documento a la coleccion de vehiculos
        col.insertOne(d);
        // Obtener el ID generado por MongoDB y asignarlo al objeto vehiculo
        v.setId(d.getObjectId("_id"));
    }
    
    //Busca un Vehiculo en la db
    public Vehiculo findByCode(String codigo){
        // Buscar el primer documento donde el campo "codigo" coincida con el codigo de vehiculo 
        Document d = col.find(Filters.eq("codigo", codigo)).first();
        // Si no existe ningún vehiculo con ese codigo, retornar null
        if (d == null) {
            return null;
        }
        //obtiene los documntos de los proveedores
        List<Document> proveedoresDocs = (List<Document>) d.get("proveedores");
        List<Proveedor> proveedoresObjects = new ArrayList<>();
        for(Document doc : proveedoresDocs){
            Proveedor p = new Proveedor(doc.getString("nombre"),
            TipoProveedor.valueOf(doc.getString("tipo")));
            proveedoresObjects.add(p);
        }
        // Crear un nuevo objeto vehiculo y mapear los campos desde el documento BSON
        Vehiculo v = new Vehiculo(
                d.getString("codigo"),
                d.getString("marca"),
                d.getString("modelo"),
                d.getInteger("año"),
                TipoVehiculo.valueOf(d.getString("tipo")),
                d.getDouble("precioDia"),
                proveedoresObjects);
        v.setDisponible(d.getBoolean("disponible"));        
        v.setId(d.getObjectId("_id"));
     
        // Devolver el empleado encontrado
        return v;
    }
    
    //retorna todos los vehiculos
    public List<Vehiculo> findAllVehiculos() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        //crea un objeto por cada documento y los enlista
        for (Document d : col.find()) {
            //obtiene los documntos de los proveedores
            List<Document> proveedoresDocs = (List<Document>) d.get("proveedores");
            List<Proveedor> proveedoresObjects = new ArrayList<>();
            for(Document doc : proveedoresDocs){
                Proveedor p = new Proveedor(doc.getString("nombre"),
                TipoProveedor.valueOf(doc.getString("tipo")));
                proveedoresObjects.add(p);
            }
            //crea el objeto vehiculo
            Vehiculo v = new Vehiculo(
                d.getString("codigo"),
                d.getString("marca"),
                d.getString("modelo"),
                d.getInteger("año"),
                TipoVehiculo.valueOf(d.getString("tipo")),
                d.getDouble("precioDia"),
                proveedoresObjects);
            v.setDisponible(d.getBoolean("disponible"));        
            v.setId(d.getObjectId("_id"));
        
            vehiculos.add(v);
        }
        //retorna la lista
        return vehiculos;
    }
    
    //actualiza vehiculos
    public boolean updateVehiculo(Vehiculo v) {
       //lista los documentos proveedor para anidarlos en la coleccion
        List<Document>proveedores = new ArrayList<>();
        for(Proveedor p : v.getProveedores()){
            proveedores.add(new Document()
                    .append("nombre", p.getNombre())
                    .append("tipo", p.getTipo()));
        }
        // Usamos set para actualizar campos específicos
        UpdateResult result = col.updateOne(
                Filters.eq("_id", v.getId()),
                Updates.combine(
                        Updates.set("codigo", v.getCodigo()),
                        Updates.set("marca", v.getMarca()),
                        Updates.set("modelo", v.getModelo()),
                        Updates.set("año", v.getAño()),
                        Updates.set("tipo", v.getTipo()),
                        Updates.set("precioDia", v.getPrecioDia()),
                        Updates.set("disponible", v.isDisponible()),
                        Updates.set("proveedores", proveedores)
                )
        );
        return result.getModifiedCount() > 0;
    }
    
    //elimina Vhiculo
     public boolean deleteVehiculo(ObjectId id){
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

