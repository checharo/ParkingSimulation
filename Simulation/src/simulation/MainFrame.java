
package simulation;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JOptionPane;


/**
 * @author César Martínez D'Granda (cesarm@student.unimelb.edu.au)
 */
public class MainFrame extends javax.swing.JFrame {

    private Sensor[][] matrix;
    /* The canvas for rendering the parking lot */
    private ParkingCanvas canvas;
    /* The central logic for controlling the Sensor Network */
    private Central central;
    
    /* The selected Sensor by the user, null at the beginning */
    private Sensor selectedSensor;
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        initApplication();
        
    }
    
    /**
     * Initializes the application by reading the Matrix from a file and 
     * rendering the canvas for the first time. If it can't read the file
     * it quits the application.
     */
    private void initApplication() {
        String file = "";
        try {    
            file = JOptionPane.showInputDialog(this, "Plase provide a parking "
                    + "lot file to read (csv). If left empty parking.csv will "
                    + "be read.", "Parking Lot Simulator", 
                    JOptionPane.QUESTION_MESSAGE);
            if (file.equals("")) file = "parking.csv";
            matrix = MatrixUtil.readMatrix(file);
        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(this, 
                    "The file " + file + " could not be found", "Error", 
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, 
                    "There was an I/O error while reading file: " + ioe.getMessage(), 
                    "Error" + ioe.getMessage(), JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        /* Create the parkign lot canvas and add it */
        canvas = new ParkingCanvas(matrix);
        canvas.setBackground(new Color(102, 102, 102));
        canvas.modifySize(pnlParking.getWidth() - 5, pnlParking.getHeight() - 5);
        canvas.setLayout(null);
        canvas.setLocation(2, 2);
        pnlParking.add(canvas);
        
        /* Initiate the central logic of the Sensor Netork and pass the reference */
        central = new Central(matrix, this);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] instanceof VirtualSensor) {
                    VirtualSensor vsensor = (VirtualSensor) matrix[i][j];
                    vsensor.setCentral(central);
                }
            }
        }
        
        /* Disable certain GUI elements until appropiate */
        reloadSensorProperties();
        /* Set the value for available spaces and cars circulating */
        refreshState();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlOptions = new javax.swing.JPanel();
        lblSensor = new javax.swing.JLabel();
        lblSensorValue = new javax.swing.JLabel();
        lblLight = new javax.swing.JLabel();
        txtLight = new javax.swing.JTextField();
        btnLight = new javax.swing.JButton();
        lblAvailableSpaces = new javax.swing.JLabel();
        lblAvailableSpacesValue = new javax.swing.JLabel();
        lblCarsCirculating = new javax.swing.JLabel();
        lblCarsCirculatingValue = new javax.swing.JLabel();
        pnlParking = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Virtual Parking Lot");
        setBackground(new java.awt.Color(102, 102, 102));
        setBounds(new java.awt.Rectangle(200, 100, 0, 0));
        setLocation(new java.awt.Point(200, 50));
        setName("mainFrame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(720, 720));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resized(evt);
            }
        });

        pnlOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

        lblSensor.setText("Sensor:");

        lblSensorValue.setText("<none>");

        lblLight.setText("Light:");

        txtLight.setColumns(5);
        txtLight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setLight(evt);
            }
        });

        btnLight.setText("set light");
        btnLight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setLight(evt);
            }
        });

        lblAvailableSpaces.setText("Available Spaces");

        lblAvailableSpacesValue.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        lblAvailableSpacesValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAvailableSpacesValue.setText("0");
        lblAvailableSpacesValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblAvailableSpacesValue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblCarsCirculating.setText("Cars Circulating");

        lblCarsCirculatingValue.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        lblCarsCirculatingValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCarsCirculatingValue.setText("0");
        lblCarsCirculatingValue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        org.jdesktop.layout.GroupLayout pnlOptionsLayout = new org.jdesktop.layout.GroupLayout(pnlOptions);
        pnlOptions.setLayout(pnlOptionsLayout);
        pnlOptionsLayout.setHorizontalGroup(
            pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlOptionsLayout.createSequentialGroup()
                        .add(lblSensor)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblSensorValue))
                    .add(pnlOptionsLayout.createSequentialGroup()
                        .add(lblLight)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtLight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnLight)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 362, Short.MAX_VALUE)
                .add(pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblAvailableSpaces, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblAvailableSpacesValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblCarsCirculating, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblCarsCirculatingValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlOptionsLayout.setVerticalGroup(
            pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlOptionsLayout.createSequentialGroup()
                .add(pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSensor)
                    .add(lblSensorValue)
                    .add(lblAvailableSpaces))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lblLight)
                        .add(txtLight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(btnLight))
                    .add(lblAvailableSpacesValue))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblCarsCirculating)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblCarsCirculatingValue)
                .add(0, 37, Short.MAX_VALUE))
        );

        pnlParking.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlParking.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                canvasClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlParkingLayout = new org.jdesktop.layout.GroupLayout(pnlParking);
        pnlParking.setLayout(pnlParkingLayout);
        pnlParkingLayout.setHorizontalGroup(
            pnlParkingLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        pnlParkingLayout.setVerticalGroup(
            pnlParkingLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 523, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlParking, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlOptions, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pnlParking, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlOptions, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_resized
        /* If they are still not initialized do nothing*/
        if (canvas != null && pnlParking != null) {
            canvas.modifySize(pnlParking.getWidth() - 5, pnlParking.getHeight() - 5);
            repaintCanvas();
        }
    }//GEN-LAST:event_resized

    private void canvasClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvasClicked
        
        /* Deselect previous sensor */
        if (selectedSensor != null) selectedSensor.setSelected(false);
        /* Select new sensor */
        selectedSensor = canvas.selectSensor(evt.getX(), evt.getY());
        if (selectedSensor != null) selectedSensor.setSelected(true);
        repaintCanvas();
        reloadSensorProperties();
    }//GEN-LAST:event_canvasClicked

    private void setLight(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setLight
        
        if (selectedSensor == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a Virtual Sensor.", 
                "Action Not Allowed", JOptionPane.ERROR_MESSAGE);
        }
        
        int light;
        try {
            light = Integer.parseInt(txtLight.getText());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, 
                    "Please set a valid integer for the light value", 
                    "Number Format Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        txtLight.setText("");
        
        if (selectedSensor instanceof VirtualSensor) {
            VirtualSensor vsensor = (VirtualSensor) selectedSensor;
            vsensor.setCurrentLight(light);
            if (vsensor.getEventThread() != null 
                    && vsensor.getEventThread().isAlive()) {
                vsensor.notifyEvent();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "The Virtual Sensor is not running. Please restart application.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                    "Cannot set the light value for a Real Sensor.", 
                    "Action Not Allowed", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_setLight

    /**
     * Refreshes the label for available spaces and cars circulating
     */
    public synchronized void refreshState() {
        lblAvailableSpacesValue.setText(central.getSpaces() + "");
        lblCarsCirculatingValue.setText(central.getCars() + "");
        repaintCanvas();
    }
    
    public synchronized void repaintCanvas() {
        canvas.repaint();
    }
    
    /* Reloads the controls with the current Sensor selected */
    private void reloadSensorProperties() {
        
        if (selectedSensor == null) {
            lblSensorValue.setText("<none>");
            txtLight.setText("");
            txtLight.setEnabled(false);
            btnLight.setEnabled(false);
        } else {
            int[] coordinates = canvas.determineCoordinates(selectedSensor);
            if (coordinates == null) {
                lblSensorValue.setText("<error>");
                txtLight.setText("");
                txtLight.setEnabled(false);
                btnLight.setEnabled(false);
            } else {
                lblSensorValue.setText(selectedSensor.toString());
                if (selectedSensor instanceof VirtualSensor) {
                    VirtualSensor vsensor = (VirtualSensor) selectedSensor;
                    txtLight.setText(vsensor.getCurrentLight() + "");
                    txtLight.setEnabled(true);
                    btnLight.setEnabled(true);
                } else {
                    txtLight.setText("");
                    txtLight.setEnabled(false);
                    btnLight.setEnabled(false);
                }
            }
        }
    }
    
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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLight;
    private javax.swing.JLabel lblAvailableSpaces;
    private javax.swing.JLabel lblAvailableSpacesValue;
    private javax.swing.JLabel lblCarsCirculating;
    private javax.swing.JLabel lblCarsCirculatingValue;
    private javax.swing.JLabel lblLight;
    private javax.swing.JLabel lblSensor;
    private javax.swing.JLabel lblSensorValue;
    private javax.swing.JPanel pnlOptions;
    private javax.swing.JPanel pnlParking;
    private javax.swing.JTextField txtLight;
    // End of variables declaration//GEN-END:variables

}

