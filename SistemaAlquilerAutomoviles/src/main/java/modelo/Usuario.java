/*
 * Gestionar metodos de usuario
 */
package modelo;
import org.bson.types.ObjectId;
/**
 *
 * @author deven
 */
public class Usuario {
    private ObjectId id;
    private String nick;
    private String passwordHash; // almacena un hash, NO en texto plano
    private String rolUsuario;

    // getters y setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }
    
    
}
