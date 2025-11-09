package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;
import utils.PasswordUtil;
import dao.EmpleadoDAO;
import modelo.Empleado;

/**
 *
 * @author vm23024
 */
public class AutenticacionControlador {

    //Objeto de acceso a datos (DAO) para manejar operaciones relacionadas con la entidad
    private UsuarioDAO userDAO = new UsuarioDAO();
    private EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    public boolean register(String nick, String plainPassword, String rolUsuario, String codigo, String nombres, String apellidos, String cargo, Double salario) {
        // Verificar si ya existe un usuario con el mismo nombre
        if (userDAO.findByUsername(nick) != null) {
            //verificar si no existe codigo de empleado
            if(empleadoDAO.findByCodigo(codigo) != null){
                return false;
            }
            
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
        //Inserta empleado
        Empleado e = new Empleado();
        e.setCodigo(codigo);
        e.setNombre(nombres);
        e.setApellido(apellidos);
        e.setCargo(cargo);
        e.setSalario(salario);
        e.setUsuario(u);
        empleadoDAO.insert(e);

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
    //Metodo que nos indica el rol del usuario que se acaba de logear para poder ocultar opcion de creacion de usuarios.
    public String rolUsuario(String usuarioLogeado){
        Usuario u = userDAO.findByUsername(usuarioLogeado);
        
        return u.getRolUsuario();
    }
    
    public Object[] admins(String campo, String valor){
        Object[] buscar = userDAO.findByOther(valor, campo);
        return buscar;
    }
}
