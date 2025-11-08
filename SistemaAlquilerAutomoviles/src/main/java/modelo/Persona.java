/*
 *Clase abstracta para las personas cliente o empleados
 */
package modelo;

/**
 *
 * @author vm23024
 */
abstract public class Persona {
    //Atributos en comun para los empleados y clientes
    private String nombre;
    private String apellido;
    //Constructores para crear o registrar al empleado o cliente segun convenga
    public Persona(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public Persona() {
    }
    //getter y setter para accesar o modificar los atributos desde las subclases
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    //Metodos a implementar por cada una de las subclases
    public abstract void registrar();
    public abstract void modificar();
    public abstract void eliminar();
    
}
