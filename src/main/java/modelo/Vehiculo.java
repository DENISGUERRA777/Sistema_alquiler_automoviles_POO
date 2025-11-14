/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

/**
 *
 * @author denis
 */
public class Vehiculo {
    //Atributos de clase
    private String codigo;
    private String marca;
    private String modelo;
    private int año;
    private TipoVehiculo tipo;
    private double precioDia;
    private boolean disponible;
    private ObjectId id;
    private List<Proveedor> proveedores = new ArrayList<>();

    //constructor
    public Vehiculo(String codigo, String marca, String modelo, int año, TipoVehiculo tipo, double precioDia, List<Proveedor> proveedores){
        //valida q tenga un proveedor de mantemiminto y por lo menos 1 de repuestos
        if(proveedores.size()< 2){
            throw new IllegalArgumentException("EL vehiculo debe de tener almenos un proveedor de servicios(mantenimiento y repuestos)");
        }
        //valida la entrada de año 
        if(año < 1970 || año> LocalDate.now().getYear()){
            throw new IllegalArgumentException("El año del vehiculo no puede ser menor que 1970 ni mayor que el año actual");
        }
        //Valida la entrada de precioDia
        if(precioDia<0){
            throw new IllegalArgumentException("El precio del vahiculo nu puede ser negativo");
        }
        this.codigo = codigo;
        this.marca = marca;
        this.modelo = modelo;
        this.año = año;
        this.tipo = tipo;
        this.precioDia = precioDia;
        disponible = true;//por defecto el vehiculo estara disponible 
        this.proveedores = proveedores;
    }

    public Vehiculo() {
        
    }
    
    //Getters

    public String getCodigo() {
        return codigo;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getAño() {
        return año;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public double getPrecioDia() {
        return precioDia;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public ObjectId getId() {
        return id;
    }

    public List<Proveedor> getProveedores() {
        return proveedores;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setAño(int año) {
        this.año = año;
    }

    //Setter
    public void setTipo(TipoVehiculo tipo) {
        this.tipo = tipo;
    }

    public void setPrecioDia(double precioDia) {
        this.precioDia = precioDia;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setProveedores(List<Proveedor> proveedores) {
        this.proveedores = proveedores;
    }
    
    //agrega proveedores
    public void addProveedor(Proveedor p){
        proveedores.add(p);
    }
    
    //eliminar proveedor
    public void deleteroveedor(Proveedor prov){
        for(Proveedor p: proveedores){
            if(p.getNombre().equalsIgnoreCase(prov.getNombre())){
                proveedores.remove(p);
            }
        }
    }
}
