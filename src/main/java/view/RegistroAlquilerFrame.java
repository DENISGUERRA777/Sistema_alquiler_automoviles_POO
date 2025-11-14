package view;

import controlador.AlquilerControlador;
import dao.AlquilerDAO;
import dao.ClienteDAO;
import dao.VehiculoDAO;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import modelo.Alquiler;
import modelo.Cliente;
import modelo.Vehiculo;

/**
 *
 * @author halfa
 */
public class RegistroAlquilerFrame extends javax.swing.JFrame {

    private AlquilerControlador controlador = new AlquilerControlador();
    private ClienteDAO clDAO;
    private VehiculoDAO vehiculoDAO;
    private AlquilerDAO alqDAO;

    /**
     * Creates new form RegistroAlquilerFrame
     */
    public RegistroAlquilerFrame() {
        clDAO = new ClienteDAO();
        vehiculoDAO = new VehiculoDAO();
        alqDAO = new AlquilerDAO();
        initComponents();
        configurarComponentes();
        //actualizarVehiculosDisponibles();

    }

    //Metodos para componentes en comun
    private void configurarComponentes() {
        //Para que el comboBox de clientes se editable
        slClientes.removeAllItems();
        slClientes.setEditable(true);
        slClientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Cliente) {
                    Cliente c = (Cliente) value;
                    value = c.getId().toString() + " - " + c.getNombre() + " " + c.getApellido() + " - " + c.getCorreo();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        //Para buscar clientes desde el propio combobox
        ((JTextField) slClientes.getEditor().getEditorComponent()).addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = ((JTextField) slClientes.getEditor().getEditorComponent()).getText().toLowerCase();
                slClientes.removeAllItems();
                List<Cliente> listaTodosLosClientes = clDAO.findAllClientes();
                for (Cliente c : listaTodosLosClientes) { // tu lista cargada desde MongoDB
                    if (c.getNombre().toLowerCase().contains(texto)
                            || c.getApellido().toLowerCase().contains(texto)
                            || c.getCorreo().toLowerCase().contains(texto)
                            || c.getLicencia().toLowerCase().contains(texto)
                            || c.getTelefono().toLowerCase().contains(texto)) {
                        String textoAgregar = c.getLicencia() + "-" + c.getNombre() + " " + c.getApellido() + "-" + c.getCorreo() + "-" + c.getTelefono();
                        slClientes.addItem(textoAgregar);
                    }
                }

                slClientes.showPopup();
                ((JTextField) slClientes.getEditor().getEditorComponent()).setText(texto);
            }
        });
        //Para combodeVehiculos
        // === Combo Vehículos ===
        slVehiculosDisp.setEditable(true);

        // === Eventos de fechas ===
        fechaInicioAl.getDateEditor().addPropertyChangeListener("date", e -> actualizarVehiculosDisponibles());
        fechaFinAl.getDateEditor().addPropertyChangeListener("date", e -> actualizarVehiculosDisponibles());

        // Cuando cambie vehículo → recalcular monto
        slVehiculosDisp.addActionListener(e -> calcularMontoTotal());
    }

    //Metodos para actualizar vehiculoso en base a las fechas elegidas 
    //para determinar disponiblidad de los mismos
    private void actualizarVehiculosDisponibles() {
        Date inicio = fechaInicioAl.getDate();
        Date fin = fechaFinAl.getDate();

        slVehiculosDisp.removeAllItems();
        //Verificando que se hallan seleccionado fechas
        if (inicio == null || fin == null) {
            slVehiculosDisp.addItem("Seleccione fechas inicio y fin de alquiler"); // vacío
            slVehiculosDisp.setEnabled(false);
            jLabelInfo.setText("Selecciona ambas fechas");
            return;
        }
        //Verificando que la fecha fin no sea menor a la inicial
        if (fin.before(inicio)) {
            jLabelInfo.setText("La fecha fin no puede ser menor que la inicio");
            slVehiculosDisp.setEnabled(false);
            return;
        }

        slVehiculosDisp.setEnabled(true);
        jLabelInfo.setText("");

        List<Vehiculo> todos = vehiculoDAO.findAllVehiculos();
        List<Vehiculo> disponibles = new ArrayList<>();

        java.sql.Date sqlInicio = new java.sql.Date(inicio.getTime());
        java.sql.Date sqlFin = new java.sql.Date(fin.getTime());

        for (Vehiculo v : todos) {
            if (!v.isDisponible()) {
                continue; // si ya está marcado como no disponible
            }
            boolean ocupado = alqDAO.estaOcupado(v, sqlInicio, sqlFin);
            if (!ocupado) {
                disponibles.add(v);
            }
        }

        if (disponibles.isEmpty()) {
            slVehiculosDisp.addItem("No hay vehículos disponibles en estas fechas");
        } else {
            for (Vehiculo v : disponibles) {
                String textoMostrar = v.getCodigo() + "-" + v.getMarca() + "-" + v.getModelo()
                        + "-" + v.getAño() + "-$" + v.getPrecioDia();
                slVehiculosDisp.addItem(textoMostrar);
            }
        }

        calcularMontoTotal();
    }

    //Calcular precio segun datos escogidos
    private void calcularMontoTotal() {
        Date inicio = fechaInicioAl.getDate();
        Date fin = fechaFinAl.getDate();
        Object seleccionado = slVehiculosDisp.getSelectedItem();

        if (seleccionado == null) {
            //JOptionPane.showMessageDialog(this, "Selecciona un vehículo");
            return;
        }
        String vehiculoSl = (String) seleccionado.toString();
        String[] caracteristicas = vehiculoSl.split("-");
        Vehiculo v = null;
        if (caracteristicas != null) {
            Vehiculo vhBuscar = vehiculoDAO.findByCode(caracteristicas[0]);
            v = vhBuscar;
        }
        //Vehiculo v = (Vehiculo) jComboVehiculos.getSelectedItem();

        if (inicio != null && fin != null && v != null && v.getCodigo() != null) {
            long diff = fin.getTime() - inicio.getTime();
            int dias = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
            double total = dias * v.getPrecioDia();

            txtDiasAlq.setText("Días: " + dias);
            txtTotalPago.setText(String.format("%.2f", total));
        } else {
            txtDiasAlq.setText("Días: -");
            txtTotalPago.setText("0.00");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCalendar1 = new com.toedter.calendar.JCalendar();
        jLFechaInicio = new javax.swing.JLabel();
        jLFechaFin = new javax.swing.JLabel();
        jLTotalPago = new javax.swing.JLabel();
        btnGuardarAlq = new javax.swing.JButton();
        btnCancelarAlq = new javax.swing.JButton();
        txtTotalPago = new javax.swing.JTextField();
        fechaInicioAl = new com.toedter.calendar.JDateChooser();
        fechaFinAl = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        slClientes = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        slVehiculosDisp = new javax.swing.JComboBox<>();
        jLabelInfo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDiasAlq = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Registro de nuevos alquileres");

        jLFechaInicio.setText("Inicio:");

        jLFechaFin.setText("Fin:");

        jLTotalPago.setText("Total pago de alquiler");

        btnGuardarAlq.setText("Guardar");
        btnGuardarAlq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarAlqActionPerformed(evt);
            }
        });

        btnCancelarAlq.setText("Cancelar");
        btnCancelarAlq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarAlqActionPerformed(evt);
            }
        });

        txtTotalPago.setEditable(false);
        txtTotalPago.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTotalPagoKeyTyped(evt);
            }
        });

        fechaInicioAl.setDateFormatString("dd/MM/yyyy");

        fechaFinAl.setDateFormatString("dd/MM/yyyy");

        jLabel1.setText("Cliente:");

        slClientes.setEditable(true);
        slClientes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Vehiculos Disponibles:");

        slVehiculosDisp.setEditable(true);
        slVehiculosDisp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Dias alquilado:");

        txtDiasAlq.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(161, 161, 161)
                        .addComponent(btnGuardarAlq)
                        .addGap(110, 110, 110)
                        .addComponent(btnCancelarAlq))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(108, 108, 108)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLFechaFin)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLTotalPago)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(142, 142, 142)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtTotalPago, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtDiasAlq, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(55, 55, 55)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLFechaInicio)
                                    .addGap(120, 120, 120)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(fechaFinAl, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                                            .addComponent(fechaInicioAl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(slClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(slVehiculosDisp, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLFechaInicio)
                    .addComponent(fechaInicioAl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLFechaFin)
                    .addComponent(fechaFinAl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(slClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(slVehiculosDisp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(txtDiasAlq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTotalPago)
                    .addComponent(txtTotalPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardarAlq)
                    .addComponent(btnCancelarAlq))
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    //crea un objeto alquiler y lo registra en la base de datos
    private void btnGuardarAlqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarAlqActionPerformed
        //Recolectando datos
        Object selCliente = slClientes.getSelectedItem();
        Cliente cliente = null;
        if(selCliente != null){
            String txtCliente = selCliente.toString();
            String licencia = txtCliente.split("-")[0].trim();
            String[] datos = txtCliente.split("-");
            String[] nombre = datos[1].split(" ");
            cliente = new Cliente();
            cliente.setLicencia(licencia);
            cliente.setNombre(nombre[0]);
            cliente.setApellido(nombre[1]);
            cliente.setCorreo(datos[2]);
            cliente.setTelefono(datos[3]);
            
            System.out.println("Todo bien con el cliente");
            
        }else{
            System.out.println("No selecciono ningun cliente valido");
        }
        
        Vehiculo vehiculo = null;
        Object vlItemSel = slVehiculosDisp.getSelectedItem();
        if (vlItemSel != null) {
            String vhSel = vlItemSel.toString();
            String[] datosSel = vhSel.split("-");
            vehiculo = new Vehiculo();
            vehiculo.setCodigo(datosSel[0]);
            vehiculo.setMarca(datosSel[1]);
            vehiculo.setModelo(datosSel[2]);
            vehiculo.setAño(Integer.parseInt(datosSel[3]));
            String quitarSimbolo = datosSel[4].replace("$", "").replace(",", "");
            vehiculo.setPrecioDia(Double.parseDouble(quitarSimbolo));
            System.out.println("Todo blien con el vehiculo");
        }
        Date inicio = fechaInicioAl.getDate();
        Date fin = fechaFinAl.getDate();

        if (cliente == null || vehiculo == null || inicio == null || fin == null) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            if(cliente == null){
                System.out.println("El cliente esta vacio");
            }
            if(vehiculo == null){
                System.out.println("El vehiculo esta vacio");
            }
            return;
        }
        Double totalAPagar = obtenerMontoTotal();
        boolean exito = controlador.register(inicio, fin, totalAPagar, vehiculo, cliente);
        if (exito) {
            JOptionPane.showMessageDialog(this, "Alquiler registrado con éxito!");
            // Limpiar formulario
            fechaInicioAl.setDate(null);
            fechaFinAl.setDate(null);
            slClientes.setSelectedIndex(-1);
            dispose();
            new AlquilerFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Parece que hubo un problema registrando el nuevo alquiler.");
        }

    }//GEN-LAST:event_btnGuardarAlqActionPerformed
    private double obtenerMontoTotal() {
        String texto = txtTotalPago.getText(); // Ej: "Total: $ 1,250.00"

        // Expresión regular que captura solo el número con decimales
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("[0-9]+[.,]?[0-9]*").matcher(texto);

        if (matcher.find()) {
            String numero = matcher.group().replace(",", ".");  // cambia coma por punto
            return Double.parseDouble(numero);
        } else {
            return 0.0; // o lanza excepción si prefieres
        }
    }

    //Cierra el frame y regresa al main
    private void btnCancelarAlqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarAlqActionPerformed
        // TODO add your handling code here:
        new MainMenuFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnCancelarAlqActionPerformed

    //bloquea la entrada de texto cuando se tipean caracteres no deseados
    private void txtTotalPagoKeyTyped(java.awt.event.KeyEvent evt) {
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume(); // No permite escribir si no es número
        }
    }

    /*
    private void txtTotalPagoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotalPagoKeyTyped

    }//GEN-LAST:event_txtTotalPagoKeyTyped
*/
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RegistroAlquilerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroAlquilerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroAlquilerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroAlquilerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroAlquilerFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarAlq;
    private javax.swing.JButton btnGuardarAlq;
    private com.toedter.calendar.JDateChooser fechaFinAl;
    private com.toedter.calendar.JDateChooser fechaInicioAl;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLFechaFin;
    private javax.swing.JLabel jLFechaInicio;
    private javax.swing.JLabel jLTotalPago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JComboBox<String> slClientes;
    private javax.swing.JComboBox<String> slVehiculosDisp;
    private javax.swing.JTextField txtDiasAlq;
    private javax.swing.JTextField txtTotalPago;
    // End of variables declaration//GEN-END:variables
}
