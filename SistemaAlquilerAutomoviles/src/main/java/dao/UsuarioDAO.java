/*
 * Metodos para manipular la informacion de la coleccion de usuarios
 */
package dao;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.awt.List;
import java.util.ArrayList;
import modelo.MongoDB;
import org.bson.Document;
import modelo.Usuario;
import org.bson.conversions.Bson;
/**
 *
 * @author deven
 */
public class UsuarioDAO {
    //Colección de MongoDB donde se almacenan los documentos de usuarios.
    private MongoCollection<Document> col;

    public UsuarioDAO() {
        // Obtiene la colección "usuarios" desde la conexión a MongoDB
        col = MongoDB.getDatabase().getCollection("usuarios");
    }

    public void insert(Usuario u) {
        // Crear un documento BSON con los campos del usuario
        Document d = new Document()
                .append("username", u.getNick())
                .append("passwordHash", u.getPasswordHash())
                .append("rolUsuario", u.getRolUsuario());
        // Insertar el documento en la colección
        col.insertOne(d);
        // Obtener el ID generado por MongoDB y asignarlo al objeto Usuario
        u.setId(d.getObjectId("_id"));
    }

    public Usuario findByUsername(String nick) {
        // Buscar el primer documento donde el campo "username" coincida con el nick
        Document d = col.find(Filters.eq("username", nick)).first();
        // Si no existe ningún usuario con ese nombre, retornar null
        if (d == null) {
            return null;
        }
        // Crear un nuevo objeto Usuario y mapear los campos desde el documento BSON
        Usuario u = new Usuario();
        u.setId(d.getObjectId("_id"));
        u.setNick(d.getString("username"));
        u.setPasswordHash(d.getString("passwordHash"));
        u.setRolUsuario(d.getString("rolUsuario"));
        // Devolver el usuario encontrado
        return u;
    }
    
    public Object[] findByOther(String value, String fieldName){
        //creamos filtro
        Bson filtro = Filters.eq(fieldName, value);
        //Contar resultados encontrados
        long totalFilas = col.countDocuments(filtro);
        //Resultados encontrados para iterar
        //List<Document> resultados = col.find(filtro).into(new ArrayList<>());
        return new Object[]{col.find(filtro).into(new ArrayList<>()), (int) totalFilas};
    }
}
