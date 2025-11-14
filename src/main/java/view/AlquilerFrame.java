package view;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.bson.types.ObjectId;
import modelo.Alquiler;
import dao.AlquilerDAO;
import controlador.AlquilerControlador;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author halfa
 */
public class AlquilerFrame extends javax.swing.JFrame {

    private DefaultTableModel tableModel;
    private AlquilerDAO alquilerDao;
    private AlquilerControlador alquilerControlador;

    /**
     * Creates new form AlquilerFrame
     */
    public AlquilerFrame() {
        initComponents();
        alquilerDao = new AlquilerDAO();
        alquilerControlador = new AlquilerControlador();
        setupTable();
        loadAlquiler();
        btnBorrar.setVisible(false);

    }

    private void setupTable() {
        String[] columnNames = {"ID", "Fecha Inicio", "Fecha Fin", "Total pago"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            Class[] tipos = {
                String.class, // ID
                Date.class, // Fecha Inicio
                Date.class, // Fecha Fin
                Double.class // Total
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return tipos[columnIndex];
            }

            @Override
            public boolean isCellEditable(int fila, int columna) {
                // no permite que se modifique el id
                return columna != 0;
            }
        };
        
        // Obtener el modelo de selección de la tabla
        ListSelectionModel selectionModel = tbAlquiler.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Previene que el listener se ejecute varias veces por el mismo evento
                if (!e.getValueIsAdjusting()) {
                    fillFieldsFromSelectedRow();
                }
            }
        });
    }

    //Metodo para actualizar la fila y columna
    private void handleTableEdit(int row, int column) {
        try {
            // 1. Obtener el ID (columna 0) y el nuevo valor de la fila editada
            Object alquilerId = (Object) tbAlquiler.getValueAt(row, 0);
            if (alquilerId == null) {
                JOptionPane.showMessageDialog(this, "Error: No se pudo obtener el ID de la fila.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Salir del método
            }
            // 2. Crear un objeto Employee con los datos actualizados de toda la fila
            Alquiler updatedAlq = new Alquiler();
            updatedAlq.setId((ObjectId) alquilerId);
            Date inicio = (Date) tbAlquiler.getValueAt(row, 1);
            Date fin = (Date) tbAlquiler.getValueAt(row, 2);
            updatedAlq.setFechaInicio(inicio);
            updatedAlq.setFechaFin(fin);
            // Manejar el salario como double
            try {
                updatedAlq.setTotalPago(Double.parseDouble(tbAlquiler.getValueAt(row, 3).toString()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: El salario debe ser un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
                // Opcional: Recargar la tabla para restaurar el valor anterior si hay error.
                loadAlquiler();
                return;
            }

            // 3. Llamar al DAO para actualizar en MongoDB
            boolean success = alquilerDao.updateAlquiler(updatedAlq);

            if (success) {
                System.out.println("Documento actualizado en MongoDB para ID: " + alquilerId.toString());
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el registro en MongoDB.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ocurrió un error al actualizar el empleado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Recorriendo colecciones
    private void loadAlquiler() {
        tbAlquiler.setModel(tableModel);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("$ #,##0.00");

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tbAlquiler.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tbAlquiler.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tbAlquiler.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        // Añadir un listener para capturar cambios en la tabla
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // Se evalua que el evento sea una actualizacion y no una insercion o una eliminacion
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();

                    // Se llama al metodo que maneja la actualizacion
                    handleTableEdit(row, column);
                }
            }
        });
        tableModel.setRowCount(0);//Limpiando tabla
        List<Alquiler> alquileres = alquilerDao.findAllAlquileres();
        for (Alquiler a : alquileres) {
            Object[] row = new Object[]{
                a.getId(),
                a.getFechaInicio(),
                a.getFechaFin(),
                a.getTotalPago(),};
            tableModel.addRow(row);
        }
        // 4. Formatear celdas al mostrar
        tbAlquiler.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Date) {
                    value = sdf.format((Date) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        tbAlquiler.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Date) {
                    value = sdf.format((Date) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        tbAlquiler.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double) {
                    value = df.format((Double) value);
                }
                setHorizontalAlignment(SwingConstants.RIGHT);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
    }

    private void fillFieldsFromSelectedRow() {
        int selectedRow = tbAlquiler.getSelectedRow();

        // Asegurarse de que realmente hay una fila seleccionada (índice válido)
        if (selectedRow >= 0) {
            try {
                // Obtener los valores de la fila seleccionada usando el tableModel
                ObjectId idValue = (ObjectId) tbAlquiler.getValueAt(selectedRow, 0);
                Alquiler alqSel = new Alquiler();
                alqSel = alquilerDao.findById(idValue);
                txtClienteAlq.setText(alqSel.getCliente().getNombre() + " " + alqSel.getCliente().getApellido());
                txtTelCl.setText(alqSel.getCliente().getCorreo() + " | " + alqSel.getCliente().getTelefono());
                txtVehiculoAlq.setText(alqSel.getVehiculo().getCodigo()
                        + " " + alqSel.getVehiculo().getMarca()
                        + " " + alqSel.getVehiculo().getModelo()
                        + " " + alqSel.getVehiculo().getAño());
                Double totalPago = alqSel.getVehiculo().getPrecioDia();
                txtVehiculo.setText(totalPago.toString());

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar datos de la fila seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Si no hay fila seleccionada, puedes limpiar los campos si lo deseas
            txtClienteAlq.setText("");
            txtTelCl.setText("");
            txtVehiculo.setText("");
            txtVehiculoAlq.setText("");
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tbAlquiler = new javax.swing.JTable();
        btnAgregarAlquiler = new javax.swing.JButton();
        btnAtras = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnBorrar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtClienteAlq = new javax.swing.JTextField();
        txtVehiculoAlq = new javax.swing.JTextField();
        txtTelCl = new javax.swing.JTextField();
        txtVehiculo = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tbAlquiler.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbAlquiler.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAlquilerMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbAlquiler);

        btnAgregarAlquiler.setText("Agregar alquiler");
        btnAgregarAlquiler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarAlquilerActionPerformed(evt);
            }
        });

        btnAtras.setText("Atras");
        btnAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrasActionPerformed(evt);
            }
        });

        jLabel1.setText("Se puede editar una fila al hacer click sobre ella");

        btnBorrar.setText("Borrar");
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        jLabel2.setText("Codigo Cliente:");

        jLabel3.setText("Codigo Vehicuolo:");

        txtClienteAlq.setEditable(false);

        txtVehiculoAlq.setEditable(false);

        txtTelCl.setEditable(false);

        txtVehiculo.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTelCl, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                                    .addComponent(txtClienteAlq))
                                .addGap(0, 3, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtVehiculo)
                                    .addComponent(txtVehiculoAlq)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(135, 135, 135)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnAgregarAlquiler)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnBorrar))
                            .addComponent(btnAtras))))
                .addGap(44, 44, 44))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtClienteAlq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(txtTelCl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtVehiculoAlq)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(txtVehiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarAlquiler)
                    .addComponent(btnBorrar))
                .addGap(18, 18, 18)
                .addComponent(btnAtras)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //abre el frame para registrar un nuevo alquiler
    private void btnAgregarAlquilerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarAlquilerActionPerformed
        // TODO add your handling code here:
        new RegistroAlquilerFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnAgregarAlquilerActionPerformed

    //borra un registro de la db
    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        // TODO add your handling code here:
        //captura los datos del la fila
        int fila = tbAlquiler.getSelectedRow();
        ObjectId id = (ObjectId) tableModel.getValueAt(fila, 0);
        //intenta borrar el registro segun id
        boolean ok = alquilerControlador.delete(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "El alquiler se ha eliminado correctamente.");
            dispose();
            // Retornar al login si solo existe
            new MainMenuFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Ocurrio un error no se puedo eliminar el alquiler.");
        }
        //vuelve a cargar el frame
        new AlquilerFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBorrarActionPerformed

    //regresa al menu main
    private void btnAtrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtrasActionPerformed
        // TODO add your handling code here:
        new MainMenuFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnAtrasActionPerformed

    private void tbAlquilerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAlquilerMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 1) { // un clic
            btnBorrar.setVisible(true);
        }
    }//GEN-LAST:event_tbAlquilerMouseClicked

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
            java.util.logging.Logger.getLogger(AlquilerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AlquilerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AlquilerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AlquilerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AlquilerFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarAlquiler;
    private javax.swing.JButton btnAtras;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbAlquiler;
    private javax.swing.JTextField txtClienteAlq;
    private javax.swing.JTextField txtTelCl;
    private javax.swing.JTextField txtVehiculo;
    private javax.swing.JTextField txtVehiculoAlq;
    // End of variables declaration//GEN-END:variables
}
