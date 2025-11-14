package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import modelo.MongoDB;
import modelo.Alquiler;
import modelo.Cliente;
import modelo.Vehiculo;
import org.bson.conversions.Bson;

/**
 * Clase para manejo de la coleccion empleado junto con la base de datos
 *
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
    public void insert(Alquiler a) {
        //Creando objeto incrustado cliente
        Document clienteDoc = new Document()
                .append("licencia", a.getCliente().getLicencia())
                .append("nombres", a.getCliente().getNombre())
                .append("apellidos", a.getCliente().getApellido())
                .append("correo", a.getCliente().getCorreo())
                .append("telefono", a.getCliente().getTelefono());
        //Creando objeto incrustado vehicul
        Document vehiculoDoc = new Document()
                .append("codigo", a.getVehiculo().getCodigo())
                .append("marca", a.getVehiculo().getMarca())
                .append("modelo", a.getVehiculo().getModelo())
                .append("año", a.getVehiculo().getAño())
                .append("precioDia", a.getVehiculo().getPrecioDia());
        // Crear un documento BSON con los campos del alquiler
        Document d = new Document()
                .append("fechaInicio", a.getFechaInicio())
                .append("fechaFin", a.getFechaFin())
                .append("totalPago", a.getTotalPago())
                .append("cliente", clienteDoc)
                .append("vehiculo", vehiculoDoc);
        //insertar documento a la coleccion de alquileres
        col.insertOne(d);
        // Obtener el ID generado por MongoDB y asignarlo al objeto vehiculo
        a.setId(d.getObjectId("_id"));
    }
    //Buscar cuando se selecciona una fila 
    public Alquiler findById(ObjectId id){
        // Buscar el primer documento donde el campo "codigo" coincida con el codigo
        Document d = col.find(Filters.eq("_id", id)).first();
        // Si no existe ningún usuario con ese nombre, retornar null
        if (d == null) {
            return null;
        }
        // Crear un nuevo objeto Usuario y mapear los campos desde el documento BSON
        Alquiler alq = new Alquiler();
        alq.setId(d.getObjectId("_id"));
        alq.setFechaInicio(d.getDate("fechaInicio"));
        alq.setFechaFin(d.getDate("fechaFin"));
        alq.setTotalPago(d.getDouble("totalPago"));
        //Seteando el objeto cliente incrustado como objeto
        Document clienteDoc = d.get("cliente", Document.class);
        if(clienteDoc != null){
            Cliente clienteAl = new Cliente();
            clienteAl.setLicencia(clienteDoc.getString("licencia"));
            clienteAl.setNombre(clienteDoc.getString("nombres"));
            clienteAl.setApellido(clienteDoc.getString("apellidos"));
            clienteAl.setCorreo(clienteDoc.getString("correo"));
            clienteAl.setTelefono(clienteDoc.getString("telefono"));
            alq.setCliente(clienteAl);
        }
        //Seteando el objeto vehiculo incrustado como objeto
        Document vehiculoDoc = d.get("vehiculo", Document.class);
        if(vehiculoDoc != null){
            Vehiculo vehiculoAl = new Vehiculo();
            vehiculoAl.setCodigo(vehiculoDoc.getString("codigo"));
            vehiculoAl.setMarca(vehiculoDoc.getString("marca"));
            vehiculoAl.setModelo(vehiculoDoc.getString("modelo"));
            vehiculoAl.setAño(vehiculoDoc.getInteger("año"));
            vehiculoAl.setPrecioDia(vehiculoDoc.getDouble("precioDia"));
            alq.setVehiculo(vehiculoAl);
        }
        //e.setUsuario(d.getUsuario);
        // Devolver el empleado encontrado
        return alq;
    }
    //Buscar un alquiler en la BD
    public Alquiler findByCode(double totalPago) {
        // Buscar el primer documento donde el campo "total pago" coincida con el pago total del alquiler
        Document d = col.find(Filters.eq("totalPago", totalPago)).first();
        // Si no existe ningún cliente con ese alquiler, retornar null
        if (d == null) {
            return null;
        }
        //Obteniendo cliente 
        Document cliente = d.get("cliente", Document.class);
        Cliente clAl = new Cliente();
        if (cliente != null) {

            clAl.setNombre(cliente.getString("nombres"));
            clAl.setApellido(cliente.getString("apellidos"));
            clAl.setLicencia(cliente.getString("licencia"));
            clAl.setTelefono(cliente.getString("telefono"));
            clAl.setCorreo(cliente.getString("correo"));
        }
        Document vehiculo = d.get("vehiculo", Document.class);
        Vehiculo vh = new Vehiculo();
        if (vehiculo != null) {

            vh.setCodigo(vehiculo.getString("codigo"));
            vh.setAño(vehiculo.getInteger("año"));
            vh.setMarca(vehiculo.getString("marca"));
            vh.setModelo(vehiculo.getString("modelo"));
        }
        // Crear un nuevo objeto alquiler y mapear los campos desde el documento BSON
        Alquiler a = new Alquiler(
                d.getDate("fechaInicio"),
                d.getDate("fechaFin"),
                d.getDouble("totalPago"),
                vh,
                clAl);
        a.setId(d.getObjectId("_id"));
        // Devolver el alquiler encontrado
        return a;

    }

    //Verifica si el vehiculo esta ocupado en esas fechas
    public boolean estaOcupado(Vehiculo vehiculo, Date inicio, Date fin) {
        Bson query = and(
                eq("codigo", vehiculo.getCodigo()),
                or(
                        and(lte("fechaInicio", fin), gte("fechaFin", inicio))
                )
        );
        return col.countDocuments(query) > 0;
    }

    //retornar todos los alquileres
    public List<Alquiler> findAllAlquileres() {
        List<Alquiler> alquileres = new ArrayList<>();
        //crea un objeto por cada documento y los enlista
        for (Document d : col.find()) {
            Document cliente = d.get("cliente", Document.class);
            Cliente clAl = new Cliente();
            if (cliente != null) {

                clAl.setNombre(cliente.getString("nombres"));
                clAl.setApellido(cliente.getString("apellidos"));
                clAl.setLicencia(cliente.getString("licencia"));
                clAl.setTelefono(cliente.getString("telefono"));
                clAl.setCorreo(cliente.getString("correo"));
            }
            Document vehiculo = d.get("vehiculo", Document.class);
            Vehiculo vh = new Vehiculo();
            if (vehiculo != null) {

                vh.setCodigo(vehiculo.getString("codigo"));
                vh.setAño(vehiculo.getInteger("año"));
                vh.setMarca(vehiculo.getString("marca"));
                vh.setModelo(vehiculo.getString("modelo"));
            }
            //System.out.println("TotalPago: "+d.getDouble("totalPago").toString());
            Alquiler a = new Alquiler(
                    d.getDate("fechaInicio"),
                    d.getDate("fechaFin"),
                    d.getDouble("totalPago"),
                    vh,
                    clAl);
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
    public boolean deleteAlquiler(ObjectId id) {
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
