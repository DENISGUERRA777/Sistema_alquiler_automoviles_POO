package utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author malvarado
 */
public class PasswordUtil {

    // Genera un hash seguro de una contraseña en texto plano utilizando el algoritmo BCrypt.
    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    //Verifica si una contraseña en texto plano coincide con su hash almacenado.
    public static boolean verify(String plain, String hash) {
        return BCrypt.checkpw(plain, hash);
    }
}
