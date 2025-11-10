/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import dao.ClienteDAO;
import modelo.Cliente;
import org.bson.types.ObjectId;

/**
 *
 * @author denis
 */
public class ClienteControlador {
    //Objeto de acceso a datos (DAO) para manejar operaciones relacionadas con la entidad
    private ClienteDAO clienteDao = new ClienteDAO();
    
    public boolean register(String nombres, String apellidos, String licencia, String telefono, String correo){
        //verifica si el cliente ya esta registrado
        if(clienteDao.findByLicence(licencia) != null){
            return false;
        }
        
        //crea un nuevo objeto cliente
        Cliente cliente = new Cliente( licencia, telefono, correo, nombres, apellidos);
        //lo introduce en la db
        clienteDao.insert(cliente);
        return true;
    }
    
    //elimina un cliente de la db
    public boolean delete(ObjectId id){
        return clienteDao.deleteCliente(id);
    }
    
    //actualiza un documento
    public boolean update(ObjectId id, String nombres, String apellidos, String licencia, String telefono, String correo){
        //verifica si el cliente ya esta en la db
        if(clienteDao.findByLicence(licencia) == null){
            return false;
        }
        
        //crea un nuevo objeto cliente
        Cliente cliente = new Cliente( licencia, telefono, correo, nombres, apellidos); 
        cliente.setId(id);
        //actualiza los datos en la db
        return clienteDao.updateCliente(cliente);
         
    }
}
