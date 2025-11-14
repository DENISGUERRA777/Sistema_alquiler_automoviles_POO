/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author denis
 */

public class Proveedor {

    //atributos
    private String nombre;
    private TipoProveedor tipo;
    
    //constructor
    public Proveedor(String nombre, TipoProveedor tipo){
        this.nombre = nombre;
        this.tipo = tipo;
    }
    
    //getters

    public String getNombre() {
        return nombre;
    }

    public TipoProveedor getTipo() {
        return tipo;
    }
    
}
