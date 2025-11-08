/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author deven
 */
public class Cliente extends Persona{
    //Atributos propios de los clientes
    private int id;
    private String licencia;
    private String telefono;
    private String correo;

    //Constructores para el manejo de informacion de la clase de clientes
    public Cliente() {
    }

    public Cliente(int id, String licencia, String telefono, String correo, String nombre, String apellido) {
        super(nombre, apellido);
        this.id = id;
        this.licencia = licencia;
        this.telefono = telefono;
        this.correo = correo;
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
