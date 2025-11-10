/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.EmpleadoDAO;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import modelo.Empleado;
import org.bson.types.ObjectId;

/**
 *
 * @author vm23024
 */
public class EmpleadosFrame extends javax.swing.JFrame {

    //Atributos
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(EmpleadosFrame.class.getName());
    private EmpleadoDAO empDAO;
    private DefaultTableModel tableModel;

    /**
     * Creates new form EmpleadosFrame
     */
    public EmpleadosFrame() {
        initComponents();
        empDAO = new EmpleadoDAO();
        setupTable();
        loadEmpleados();
    }

    //Creando las columnas para la tabla
    private void setupTable() {
        String[] columnNames = {"ID", "Codido", "Nombres", "Apellidos", "Cargo", "Salario"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //La columna 0 no es editable ya que es la llave del documento de empleado
                return column != 0;
            }
        };
        tbEmpleados.setModel(tableModel);
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
        // Obtener el modelo de selección de la tabla
        ListSelectionModel selectionModel = tbEmpleados.getSelectionModel();
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

    //Recorriendo colecciones
    private void loadEmpleados() {
        tableModel.setRowCount(0);//Limpiando tabla
        List<Empleado> empleados = empDAO.findAllEmpleados();
        for (Empleado emp : empleados) {
            Object[] row = new Object[]{
                emp.getId(),
                emp.getCodigo(),
                emp.getNombre(),
                emp.getApellido(),
                emp.getCargo(),
                emp.getSalario()
            };
            tableModel.addRow(row);
        }
    }

    //Metodo para actualizar la fila y columna
    private void handleTableEdit(int row, int column) {
        try {
            // 1. Obtener el ID (columna 0) y el nuevo valor de la fila editada
            Object empleadoId = (Object) tableModel.getValueAt(row, 0);
            if (empleadoId == null) {
                JOptionPane.showMessageDialog(this, "Error: No se pudo obtener el ID de la fila.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Salir del método
            }
            // 2. Crear un objeto Employee con los datos actualizados de toda la fila
            Empleado updatedEmp = new Empleado();
            updatedEmp.setId((ObjectId) empleadoId);
            updatedEmp.setCodigo(getValueOrDefault(tableModel.getValueAt(row, 1), "").toString());
            updatedEmp.setNombre(getValueOrDefault(tableModel.getValueAt(row, 2), "").toString());
            updatedEmp.setApellido(getValueOrDefault(tableModel.getValueAt(row, 3), "").toString());
            updatedEmp.setCargo(getValueOrDefault(tableModel.getValueAt(row, 4), "").toString());
            // Manejar el salario como double
            try {
                updatedEmp.setSalario(Double.parseDouble(tableModel.getValueAt(row, 5).toString()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: El salario debe ser un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
                // Opcional: Recargar la tabla para restaurar el valor anterior si hay error.
                loadEmpleados();
                return;
            }

            // 3. Llamar al DAO para actualizar en MongoDB
            boolean success = empDAO.updateEmpleado(updatedEmp);

            if (success) {
                System.out.println("Documento actualizado en MongoDB para ID: " + empleadoId);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el registro en MongoDB.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ocurrió un error al actualizar el empleado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Método auxiliar para manejar valores nulos de forma segura

    private Object getValueOrDefault(Object value, Object defaultValue) {
        return (value == null) ? defaultValue : value;
    }

    private void fillFieldsFromSelectedRow() {
        int selectedRow = tbEmpleados.getSelectedRow();

        // Asegurarse de que realmente hay una fila seleccionada (índice válido)
        if (selectedRow >= 0) {
            try {
                // Obtener los valores de la fila seleccionada usando el tableModel
                ObjectId idValue = (ObjectId) tableModel.getValueAt(selectedRow, 0);
                Empleado empSel = new Empleado();
                empSel = empDAO.findById(idValue);
                txtUserEmpleado.setText(empSel.getUsuario().getNick());
                txtRolUsuarioEmpleado.setText(empSel.getUsuario().getRolUsuario());
                

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar datos de la fila seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Si no hay fila seleccionada, puedes limpiar los campos si lo deseas
            txtUserEmpleado.setText("");
            txtRolUsuarioEmpleado.setText("");
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
        tbEmpleados = new javax.swing.JTable();
        btnNuevoEmpleado = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtUserEmpleado = new javax.swing.JTextField();
        txtRolUsuarioEmpleado = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tbEmpleados.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tbEmpleados);

        btnNuevoEmpleado.setText("Nuevo Empleado");
        btnNuevoEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoEmpleadoActionPerformed(evt);
            }
        });

        jLabel1.setText("Usuario:");

        jLabel2.setText("Rol de usuario:");

        txtUserEmpleado.setEditable(false);

        txtRolUsuarioEmpleado.setEditable(false);

        jButton1.setText("Eliminar Empleado");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("<< Pantalla Principal");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnNuevoEmpleado)
                        .addGap(26, 26, 26)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(38, 38, 38)
                                .addComponent(txtUserEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtRolUsuarioEmpleado)))))
                .addContainerGap(58, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtUserEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(txtRolUsuarioEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevoEmpleado)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap(93, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevoEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoEmpleadoActionPerformed
        // TODO add your handling code here:
        new RegistroEmpleadoSecFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNuevoEmpleadoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int selectedRowIndex = tbEmpleados.getSelectedRow();
        //Si no ha seleccionado ninguna fila
        if (selectedRowIndex == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una fila para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmación del usuario
        int confirmResult = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar este empleado?", "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmResult == JOptionPane.YES_OPTION) {
            try {
                // Obtener el ID de la fila seleccionada (columna 0)
                Object idValue = tableModel.getValueAt(selectedRowIndex, 0);
                if (idValue == null) {
                    JOptionPane.showMessageDialog(this, "Error: No se pudo obtener el ID del empleado.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String employeeId = idValue.toString();

                // Llamar al DAO para eliminar
                boolean success = empDAO.deleteEmpleado(employeeId); // Usando el método del DAO

                if (success) {
                    JOptionPane.showMessageDialog(this, "Empleado eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loadEmpleados(); // Recargar la tabla para reflejar los cambios
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el empleado de MongoDB.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ocurrió un error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //regresar a la pantalla principal
        new MainMenuFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new EmpleadosFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNuevoEmpleado;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbEmpleados;
    private javax.swing.JTextField txtRolUsuarioEmpleado;
    private javax.swing.JTextField txtUserEmpleado;
    // End of variables declaration//GEN-END:variables
}
