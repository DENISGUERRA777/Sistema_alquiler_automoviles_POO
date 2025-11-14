
package controlador;

import dao.AlquilerDAO;
import java.util.Date;
import org.bson.types.ObjectId;

import modelo.Alquiler;
import modelo.Cliente;
import modelo.Vehiculo;

/**
 *
 * @author halfa
 */
public class AlquilerControlador {
    //Objeto de acceso a datos (DAO) para manejar operaciones relacionadas con la entidad
    private AlquilerDAO alquilerDao = new AlquilerDAO();
    
    public boolean register(Date inicio, Date fin, Double totalPago, Vehiculo vh, Cliente cl){
        Alquiler nuevoAlquiler = new Alquiler();
        nuevoAlquiler.setCliente(cl);
        nuevoAlquiler.setVehiculo(vh);
        nuevoAlquiler.setFechaInicio(inicio);
        nuevoAlquiler.setFechaFin(fin);
        nuevoAlquiler.setTotalPago(totalPago);
        alquilerDao.insert(nuevoAlquiler);
        return true;
    }
    
    //Elimina un alquiler de la BD
    public boolean delete(ObjectId id){
        return alquilerDao.deleteAlquiler(id);
    }
    
    //Actualiza un documento
    public boolean update(ObjectId id, Date fechaInicio, Date fechaFin, Double totalPago, Vehiculo vh, Cliente cl){
        //Verifica si el alquiler ya esta en la base de datos
         if(alquilerDao.findByCode(totalPago) != null){
            return false;
        }
        
        //Crea un nuevo objeto alquiler
        Alquiler alquiler = new Alquiler(fechaInicio, fechaFin, totalPago, vh, cl);
        alquiler.setId(id);
        //actualiza los datos en la db
        return alquilerDao.updateAlquiler(alquiler);
    }
}
