package Pharmacy;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;

public class DoctorDetails extends JFrame {
    private JTextField txtId, txtName, txtDegree, txtClinic, txtAddress, txtContact, txtEmail;
    private JTable table;
    private DefaultTableModel model;
    private Connection con;
    
    private JLabel createBlackLabel(String text) {
    JLabel label = new JLabel(text);
    label.setForeground(Color.BLACK);  // Black color
    label.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Bigger font
    return label;
}

    public DoctorDetails() {
        setTitle("👨‍⚕️ Doctor Details");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 245, 255)); // light blue like Medicine page

        // Connect to DB
        connect();

        // 🔹 Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        JLabel titleLabel = new JLabel("Doctor Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 🔹 Form + Table Section
        JPanel centerPanel = new JPanel(new BorderLayout());
centerPanel.setOpaque(false);
centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Form Panel
JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
formPanel.setBorder(BorderFactory.createTitledBorder("Add / Update Doctor"));
formPanel.setBackground(new Color(240, 250, 255));

// Doctor ID
formPanel.add(createBlackLabel("Doctor ID:"));
txtId = new JTextField();
txtId.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtId);

// Doctor Name
formPanel.add(createBlackLabel("Doctor Name:"));
txtName = new JTextField();
txtName.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtName);

// Doctor Degree
formPanel.add(createBlackLabel("Doctor Degree:"));
txtDegree = new JTextField();
txtDegree.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtDegree);

// Clinic Name
formPanel.add(createBlackLabel("Clinic Name:"));
txtClinic = new JTextField();
txtClinic.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtClinic);

// Address
formPanel.add(createBlackLabel("Address:"));
txtAddress = new JTextField();
txtAddress.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtAddress);

// Contact Number
formPanel.add(createBlackLabel("Contact Number:"));
txtContact = new JTextField();
txtContact.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtContact);

// Email
formPanel.add(createBlackLabel("Email:"));
txtEmail = new JTextField();
txtEmail.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtEmail);


        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{
                "Doctor ID", "Name", "Degree", "Clinic", "Address", "Contact", "Email"
        }, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(0, 153, 255));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 🔹 Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 245, 255));

        JButton btnAdd = createButton("Add");
        JButton btnUpdate = createButton("Update");
        JButton btnDelete = createButton("Delete");
        JButton btnClear = createButton("Clear");
        JButton btnLoad = createButton("Load Data");
        JButton btnPrintDoctorDetails = createButton("Print Doctor Details");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnPrintDoctorDetails);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        btnAdd.addActionListener(e -> addDoctor());
        btnUpdate.addActionListener(e -> updateDoctor());
        btnDelete.addActionListener(e -> deleteDoctor());
        btnClear.addActionListener(e -> clearFields());
        btnLoad.addActionListener(e -> loadData());
        btnPrintDoctorDetails.addActionListener(e -> printDoctorDetailsAsPDF());
        // Click Row
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtId.setText(model.getValueAt(row, 0).toString());
                txtName.setText(model.getValueAt(row, 1).toString());
                txtDegree.setText(model.getValueAt(row, 2).toString());
                txtClinic.setText(model.getValueAt(row, 3).toString());
                txtAddress.setText(model.getValueAt(row, 4).toString());
                txtContact.setText(model.getValueAt(row, 5).toString());
                txtEmail.setText(model.getValueAt(row, 6).toString());
            }
        });

       // loadData();
        setVisible(true);
    }

    // 🔹 Styled Button Creator (Same as Medicine Page)
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(0, 153, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 120, 215));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 153, 255));
            }
        });
        return button;
    }

    // 🔹 Database Connection
    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/miraj_medical_db",
                    "root",
                    "leon5cprytv8@"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Database Connection Failed: " + e.getMessage());
        }
    }

    // 🔹 CRUD Operations
    private void addDoctor() {
        try {
            String query = "INSERT INTO doctor (doctor_id, doctor_name, doctor_degree, clinic_name, address, contact_number, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, txtId.getText());
            pst.setString(2, txtName.getText());
            pst.setString(3, txtDegree.getText());
            pst.setString(4, txtClinic.getText());
            pst.setString(5, txtAddress.getText());
            pst.setString(6, txtContact.getText());
            pst.setString(7, txtEmail.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Doctor Added Successfully!");
            loadData();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error Adding Doctor: " + e.getMessage());
        }
    }

    private void updateDoctor() {
        try {
            String query = "UPDATE doctor SET doctor_name=?, doctor_degree=?, clinic_name=?, address=?, contact_number=?, email=? WHERE doctor_id=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, txtName.getText());
            pst.setString(2, txtDegree.getText());
            pst.setString(3, txtClinic.getText());
            pst.setString(4, txtAddress.getText());
            pst.setString(5, txtContact.getText());
            pst.setString(6, txtEmail.getText());
            pst.setString(7, txtId.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Doctor Updated Successfully!");
            loadData();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error Updating Doctor: " + e.getMessage());
        }
    }

    private void deleteDoctor() {
        try {
            String query = "DELETE FROM doctor WHERE doctor_id=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, txtId.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "🗑️ Doctor Deleted Successfully!");
            loadData();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error Deleting Doctor: " + e.getMessage());
        }
    }

   private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnect.connectDB()) {
            String sql = "SELECT * FROM doctor";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getString("doctor_degree"),
                        rs.getString("clinic_name"),
                        rs.getString("address"),
                        rs.getString("contact_number"),
                        rs.getString("email"),
                        
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }


    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtDegree.setText("");
        txtClinic.setText("");
        txtAddress.setText("");
        txtContact.setText("");
        txtEmail.setText("");
    }

     // ===== PRINT All Doctor Details =====
private void printDoctorDetailsAsPDF() {
    try {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ No doctor data available to print.");
            return;
        }

        String filePath = System.getProperty("user.home") + "/Desktop/All_Doctor_Details.pdf";

        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(filePath));
        doc.open();

        // Add logo
        try {
            Image logo = Image.getInstance("src/images/logo.png");
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            doc.add(logo);
        } catch (Exception e) {
            System.out.println("Logo not found: " + e.getMessage());
        }

        doc.add(new Paragraph("\n"));
        // ===== CENTERED HEADER BELOW LOGO =====

Paragraph title = new Paragraph("MIRAJ MEDICAL PAL",
        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
title.setAlignment(Element.ALIGN_CENTER);
doc.add(title);



Paragraph line1 = new Paragraph("At Post-Pal, Tal-Karad, Dist-Satara");
line1.setAlignment(Element.ALIGN_CENTER);
doc.add(line1);

Paragraph line2 = new Paragraph("Owner: Mubarak Sutar");
line2.setAlignment(Element.ALIGN_CENTER);
doc.add(line2);

Paragraph line3 = new Paragraph("Contact: 9960924671, 8208287689\n\n");
line3.setAlignment(Element.ALIGN_CENTER);
doc.add(line3);

// ===== CENTERED REPORT TITLE =====
Paragraph reportTitle = new Paragraph("Doctor Details", 
        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22));
reportTitle.setAlignment(Element.ALIGN_CENTER);
doc.add(reportTitle);

doc.add(new Paragraph("\n"));

doc.add(new Paragraph("\n"));
        // PDF Table
        PdfPTable pdfTable = new PdfPTable(7);
        pdfTable.setWidthPercentage(100);
        pdfTable.setWidths(new float[]{2f, 3f, 1f, 2f, 3f, 4f,4f});

        String[] headers = {"Doctor ID", "Name", "Degree", "Clinic", "Address", "Contact", "Email"};
        for (String h : headers) {
            pdfTable.addCell(new com.itextpdf.text.Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        }

        // Add all Doctor rows
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                pdfTable.addCell(model.getValueAt(row, col).toString());
            }
        }

        doc.add(pdfTable);
        doc.add(new Paragraph("\nWe provide all types of medicines here. Please consult a doctor before taking any medicine. Thank you for visiting Miraj Medical!"));
        doc.close();

        // Open PDF automatically
        try {
            Desktop.getDesktop().open(new java.io.File(filePath));
        } catch (Exception ex) {
            System.out.println("Cannot open PDF automatically: " + ex.getMessage());
        }

        JOptionPane.showMessageDialog(this, "✅ All Doctor PDF saved successfully at Desktop!");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "⚠️ Error printing PDF: " + ex.getMessage());
    }
}



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorDetails());
    }
}





    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

