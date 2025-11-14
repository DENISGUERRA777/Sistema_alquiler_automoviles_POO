
package modelo;


import java.util.Date;
import org.bson.types.ObjectId;
/**
 *
 * @author halfa
 */
public class Alquiler {
    //Atributos
    private Date fechaInicio;
    private Date fechaFin;
    private Double totalPago;
    private ObjectId id;
    private Vehiculo vehiculo;
    private Cliente cliente;
    
   
    //Constructor para manejar la información del alquiler
    public Alquiler() {
    }

    public Alquiler(Date fechaInicio, Date fechaFin, Double totalPago, Vehiculo vehiculo, Cliente cliente) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        //valida que la entrada del pago total sea positiva
        if(totalPago < 0){
            throw new IllegalArgumentException("El pago total no puede ser negativo");
        }  
        this.totalPago = totalPago;
        this.vehiculo = vehiculo;
        this.cliente = cliente;
    }
    
    
   
    //Getters y setters de los atributos del alquiler
    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
       this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Double getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(Double totalPago) {
        this.totalPago = totalPago;
    }
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    
}
