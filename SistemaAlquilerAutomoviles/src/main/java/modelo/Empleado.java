/*
 * Clase Empleado que sera utilizado para crear 
alquileres
 */
package modelo;

/**
 *
 * @author vm23024
 */
public class Empleado extends Persona{
    //Atributos adicionales a los de persona;
    private String codigo;
    private String cargo;
    private double salario;
    private Usuario usuario;

    //Constructores para manejar la informacion del empleado

    public Empleado() {
    }

    public Empleado(String codigo, String cargo, double salario, Usuario usuario, String nombre, String apellido) {
        super(nombre, apellido);
        this.codigo = codigo;
        this.cargo = cargo;
        this.salario = salario;
        this.usuario = usuario;
    }

    
    //Getters y setters de los atributos del empleado
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    
    
    @Override
    public void registrar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void modificar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eliminar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
