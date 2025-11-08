package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;
import utils.PasswordUtil;

/**
 *
 * @author malvarado
 */
public class AutenticacionControlador {

    //Objeto de acceso a datos (DAO) para manejar operaciones relacionadas con la entidad
    private UsuarioDAO userDAO = new UsuarioDAO();

    public boolean register(String nick, String plainPassword, String rolUsuario) {
        // Verificar si ya existe un usuario con el mismo nombre
        if (userDAO.findByUsername(nick) != null) {
            return false;
        }
        // Generar el hash de la contraseña
        String hash = PasswordUtil.hash(plainPassword);

        // Crear un nuevo objeto Usuario
        Usuario u = new Usuario();
        u.setNick(nick);
        u.setPasswordHash(hash);
        u.setRolUsuario(rolUsuario);
        // Insertar el nuevo usuario en la base de datos
        userDAO.insert(u);

        return true;
    }

    public boolean login(String username, String plainPassword) {
        // Buscar el usuario en la base de datos
        Usuario u = userDAO.findByUsername(username);
        if (u == null) {
            return false;
        }
        // Verificar la contraseña
        return PasswordUtil.verify(plainPassword, u.getPasswordHash());
    }
}
