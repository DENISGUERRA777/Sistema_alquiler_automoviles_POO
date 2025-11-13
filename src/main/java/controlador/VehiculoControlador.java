/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import dao.VehiculoDAO;
import java.util.List;
import modelo.Proveedor;
import modelo.TipoVehiculo;
import modelo.Vehiculo;
import org.bson.types.ObjectId;

/**
 *
 * @author denis
 */
public class VehiculoControlador {
    //Objeto de acceso a datos (DAO) para manejar operaciones relacionadas con la entidad
    private VehiculoDAO vehiculoDao = new VehiculoDAO();
    
    public boolean register(String codigo, String marca, String modelo, int año, TipoVehiculo tipo, double precio, List<Proveedor> proveedores){
        //verifica si el vehiculo ya esta registrado
        if(vehiculoDao.findByCode(codigo) != null){
            return false;
        }
        
        //crea un nuevo objeto vehiculo
        Vehiculo vehiculo = new Vehiculo( codigo, marca, modelo, año, tipo, precio, proveedores);
        //lo introduce en la db
        vehiculoDao.insert(vehiculo);
        return true;
    }
    
    //elimina un vehiculo de la db
    public boolean delete(ObjectId id){
        return vehiculoDao.deleteVehiculo(id);
    }
    
    //actualiza un documento
    public boolean update(ObjectId id, String codigo, String marca, String modelo, int año, TipoVehiculo tipo, double precio, List<Proveedor> proveedores, boolean disponible){
        //verifica si el vehiculo ya esta en la db
        if(vehiculoDao.findByCode(codigo) == null){
            return false;
        }
        
        //crea un nuevo objeto cliente
        Vehiculo vehiculo = new Vehiculo( codigo, marca, modelo, año, tipo, precio, proveedores); 
        vehiculo.setId(id);
        vehiculo.setDisponible(disponible);
        //actualiza los datos en la db
        return vehiculoDao.updateVehiculo(vehiculo);
         
    }
}
