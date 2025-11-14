/*
 * Clase para conectar a la base de datos requerida para el sistem
 * autores: VM23024
 */
package modelo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
/**
 *
 * @author deven
 */
public class MongoDB {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final String URI = "mongodb://localhost:27017"; // cambiar si usa Atlas mongodb://localhost:27017/
    private static final String DB_NAME = "proyecto_final_poo";

    public static void init() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(URI);
            database = mongoClient.getDatabase(DB_NAME);
        }
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            init();
        }
        return database;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }
}
