
package dao;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
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
    //Metodo para retornar todos los empleados
    public List<Empleado> findAllEmpleados() {
        List<Empleado> empleados = new ArrayList<>();
        for (Document doc : col.find()) {
            Empleado emp = new Empleado();
            emp.setId(doc.getObjectId("_id"));
            emp.setNombre(doc.getString("nombres"));
            emp.setApellido(doc.getString("apellidos"));
            emp.setCargo(doc.getString("cargo"));
            emp.setSalario(doc.getDouble("salario"));
            //para el objeto incrustado usuario lo convertimos a clase y lo instanciamos manualmente 
            Document usuarioDoc = doc.get("usuario", Document.class);
            if(usuarioDoc != null){
                Usuario usuarioEmp = new Usuario();
                usuarioEmp.setNick(usuarioDoc.getString("username"));
                usuarioEmp.setRolUsuario(usuarioDoc.getString("rolUsuario"));
                usuarioEmp.setPasswordHash(usuarioDoc.getString("password"));
                emp.setUsuario(usuarioEmp);
            }
            empleados.add(emp);
        }
        return empleados;
    }
    
    //Metodo para actualizar empleado
    public boolean updateEmpleado(Empleado emp) {
        // Usamos set para actualizar campos específicos
        UpdateResult result = col.updateOne(
                Filters.eq("_id", emp.getId()),
                Updates.combine(
                        Updates.set("nombres", emp.getNombre()),
                        Updates.set("apellidos", emp.getApellido()),
                        Updates.set("cargo", emp.getCargo()),
                        Updates.set("salario", emp.getSalario())
                )
        );
        return result.getModifiedCount() > 0;
    }
    
}
