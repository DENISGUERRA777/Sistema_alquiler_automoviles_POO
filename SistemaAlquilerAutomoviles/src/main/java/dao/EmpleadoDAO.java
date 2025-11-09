
package dao;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import modelo.MongoDB;
import modelo.Empleado;
import modelo.Usuario;
import org.bson.Document;
import org.bson.types.ObjectId;
/**
 * Clase para manejo de la coleccion empleado junto con la base de datos
 * @author vm23024
 */
public class EmpleadoDAO {
    //Colección de MongoDB donde se almacenan los documentos de empleados y usuarios.
    private MongoCollection<Document> col;
    
    public EmpleadoDAO() {
        // Obtiene la colección "empleados" desde la conexión a MongoDB
        col = MongoDB.getDatabase().getCollection("empleados");
    }
    
    public void insert(Empleado e){
        //Creando usuario del empleado
        Document usuarioDoc = new Document()
                .append("username", e.getUsuario().getNick())
                .append("password", e.getUsuario().getPasswordHash())
                .append("rolUsuario", e.getUsuario().getRolUsuario());
        // Crear un documento BSON con los campos del empleado
        Document d = new Document()
                .append("nombres", e.getNombre())
                .append("apellidos", e.getApellido())
                .append("codigo", e.getCodigo())
                .append("cargo", e.getCargo())
                .append("salario", e.getSalario())
                .append("usuario", usuarioDoc);
        //insertar documento a la coleccion de empleado
        col.insertOne(d);
        // Obtener el ID generado por MongoDB y asignarlo al objeto Usuario
        
        e.setId(d.getObjectId("_id"));
    }
    
    public Empleado findByCodigo(String codigo){
        // Buscar el primer documento donde el campo "codigo" coincida con el codigo
        Document d = col.find(Filters.eq("codigo", codigo)).first();
        // Si no existe ningún usuario con ese nombre, retornar null
        if (d == null) {
            return null;
        }
        // Crear un nuevo objeto Usuario y mapear los campos desde el documento BSON
        Empleado e = new Empleado();
        e.setId(d.getObjectId("_id"));
        e.setCodigo(d.getString("codigo"));
        e.setCargo(d.getString("cargo"));
        e.setSalario(d.getDouble("salario"));
        //e.setUsuario(d.getUsuario);
        // Devolver el empleado encontrado
        return e;
    }
}
