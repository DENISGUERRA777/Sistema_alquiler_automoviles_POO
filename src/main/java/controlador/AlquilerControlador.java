
package controlador;

import dao.AlquilerDAO;
import org.bson.types.ObjectId;
import java.util.List;
import modelo.Alquiler;

/**
 *
 * @author halfa
 */
public class AlquilerControlador {
    //Objeto de acceso a datos (DAO) para manejar operaciones relacionadas con la entidad
    private AlquilerDAO alquilerDao = new AlquilerDAO();
    
    public boolean register(String fechaInicio, String fechaFin, double totalPago){
        //Verifica si el alquiler ya esta registrado
        if(alquilerDao.findByCode(totalPago) != null){
            return false;
        }
        
        //crear un nuevo objeto alquiler
        Alquiler alquiler = new Alquiler( fechaInicio, fechaFin, totalPago);
        //lo introduce en la db
        alquilerDao.insert(alquiler);
        return true;
    }
    
    //Elimina un alquiler de la BD
    public boolean delete(ObjectId id){
        return alquilerDao.deleteAlquiler(id);
    }
    
    //Actualiza un documento
    public boolean update(ObjectId id, String fechaInicio, String fechaFin, double totalPago){
        //Verifica si el alquiler ya esta en la base de datos
         if(alquilerDao.findByCode(totalPago) != null){
            return false;
        }
        
        //Crea un nuevo objeto alquiler
        Alquiler alquiler = new Alquiler(fechaInicio, fechaFin, totalPago);
        alquiler.setId(id);
        //actualiza los datos en la db
        return alquilerDao.updateAlquiler(alquiler);
    }
}
