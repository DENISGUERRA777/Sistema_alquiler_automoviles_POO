
package modelo;


import org.bson.types.ObjectId;
/**
 *
 * @author halfa
 */
public class Alquiler {
    //Atributos
    private String fechaInicio;
    private String fechaFin;
    private double totalPago;
    private ObjectId id;
    
    
    //Constructor para manejar la información del alquiler
    public Alquiler() {
    }
    
    public Alquiler (String fechaInicio, String fechaFin, double totalPago){
        //valida que la entrada del pago total sea positiva
        if(totalPago < 0){
            throw new IllegalArgumentException("El pago total no puede ser negativo");
        }
        this.fechaFin = fechaFin;
        this.fechaInicio = fechaInicio;
        this.totalPago = totalPago;
    }
    
    //Getters y setters de los atributos del alquiler
    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
       this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(double totalPago) {
        this.totalPago = totalPago;
    }
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}
