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


public class PatientDetails extends JFrame {
    private JTextField txtPatientID, txtName, txtAge, txtContact, txtSearch;
    private JTextArea txtAddress;
    private JComboBox<String> genderCombo;
    private JTable table;
    private DefaultTableModel model;
    private Connection con;

    public PatientDetails() {
        setTitle("🩺 Patient Details - Miraj Medical");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 245, 255)); // light blue background

        connect(); // connect to database
        
        
        // 🔹 Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        JLabel titleLabel = new JLabel("Patient Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 🔹 Center Section (Form + Table)
        JPanel centerPanel = new JPanel(new BorderLayout());
centerPanel.setOpaque(false);
centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // top, left, bottom, right padding

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Update Patient"));
        formPanel.setBackground(new Color(240, 250, 255));

        JLabel lblID = new JLabel("Patient ID:");
        txtPatientID = new JTextField();
        lblID.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtPatientID.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.add(lblID);
        formPanel.add(txtPatientID);

        JLabel lblName = new JLabel("Name:");
        txtName = new JTextField();
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.add(lblName);
        formPanel.add(txtName);

        JLabel lblAge = new JLabel("Age:");
        txtAge = new JTextField();
        lblAge.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtAge.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.add(lblAge);
        formPanel.add(txtAge);

        JLabel lblGender = new JLabel("Gender:");
        genderCombo = new JComboBox<>(new String[]{"Select", "Male", "Female", "Other"});
        // Set fonts
lblGender.setFont(new Font("Segoe UI", Font.BOLD, 18));
genderCombo.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Correct

formPanel.add(lblGender);
formPanel.add(genderCombo);

        JLabel lblContact = new JLabel("Contact:");
        txtContact = new JTextField();
        lblContact.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtContact.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.add(lblContact);
        formPanel.add(txtContact);

        JLabel lblAddress = new JLabel("Address:");
        txtAddress = new JTextArea(2, 15);
        txtAddress.setLineWrap(true);
        lblAddress.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtAddress.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.add(lblAddress);
        formPanel.add(new JScrollPane(txtAddress));

        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Table
model = new DefaultTableModel(
        new String[]{"Patient ID", "Name", "Age", "Gender", "Contact", "Address"},
        0
);
table = new JTable(model);

// =================== INCREASE TABLE DATA FONT ===================
table.setFont(new Font("Segoe UI", Font.PLAIN, 18));  // <-- FONT SIZE
table.setRowHeight(30);                               // <-- ROW HEIGHT

DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
cellRenderer.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // <-- CELL FONT SIZE

for (int i = 0; i < table.getColumnCount(); i++) {
    table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
}
// ==================================================================

// === SCROLL PANEL ===
JScrollPane scrollPane = new JScrollPane(table);

// Header font
table.getTableHeader().setBackground(new Color(0, 153, 255));
table.getTableHeader().setForeground(Color.WHITE);
table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 22));

        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.getTableHeader().setBackground(new Color(0, 153, 255));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 22));

        
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 🔹 Button + Search Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(230, 245, 255));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 245, 255));

        JButton btnAdd = createButton("Add");
        JButton btnUpdate = createButton("Update");
        JButton btnDelete = createButton("Delete");
        JButton btnClear = createButton("Clear");
        JButton btnLoad = createButton("Load");
        JButton btnPrintPatientDetails = createButton("Print Patient Details");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnPrintPatientDetails);

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(230, 245, 255));
        searchPanel.add(new JLabel("Search:"));
        txtSearch = new JTextField(15);
        JButton btnSearch = createButton("Search");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
  
        bottomPanel.add(searchPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button Actions
        btnAdd.addActionListener(e -> addPatient());
        btnUpdate.addActionListener(e -> updatePatient());
        btnDelete.addActionListener(e -> deletePatient());
        btnClear.addActionListener(e -> clearFields());
        btnLoad.addActionListener(e -> loadData());
        btnPrintPatientDetails.addActionListener(e -> printPatientDetailsAsPDF());
        //btnSearch.addActionListener(e -> loadPatients(txtSearch.getText().trim()));
        //btnLoad.addActionListener(e -> loadPatients("")); // loads all rows

         setVisible(true);
   

        // Row click to fill fields
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtPatientID.setText(model.getValueAt(row, 0).toString());
                txtName.setText(model.getValueAt(row, 1).toString());
                txtAge.setText(model.getValueAt(row, 2).toString());
                genderCombo.setSelectedItem(model.getValueAt(row, 3).toString());
                txtContact.setText(model.getValueAt(row, 4).toString());
                txtAddress.setText(model.getValueAt(row, 5).toString());
            }
        });

        //loadPatients("");
        setVisible(true);
    }

    // 🔹 Styled Button Creator
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
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
                    "leon5cprytv8@" // ← replace with your MySQL password
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Database Connection Failed: " + e.getMessage());
        }
    }
    
   // 🔹 Add Patient
private void addPatient() {
    try {
        String sql = "INSERT INTO patients (name, age, gender, contact, address) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, txtName.getText());
        pst.setString(2, txtAge.getText());
        pst.setString(3, genderCombo.getSelectedItem().toString());
        pst.setString(4, txtContact.getText());
        pst.setString(5, txtAddress.getText());
        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "✅ Patient Added Successfully!");
        //loadPatients("");
        clearFields();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "⚠️ Error Adding Patient: " + e.getMessage());
    }
}

// 🔹 Update Patient
private void updatePatient() {
    try {
        String sql = "UPDATE patients SET name=?, age=?, gender=?, contact=?, address=? WHERE patient_id=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, txtName.getText());
        pst.setString(2, txtAge.getText());
        pst.setString(3, genderCombo.getSelectedItem().toString());
        pst.setString(4, txtContact.getText());
        pst.setString(5, txtAddress.getText());
        pst.setString(6, txtPatientID.getText());
        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "✅ Patient Updated Successfully!");
        //loadPatients("");
        clearFields();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "⚠️ Error Updating Patient: " + e.getMessage());
    }
}

// 🔹 Delete Patient
private void deletePatient() {
    try {
        String sql = "DELETE FROM patients WHERE patient_id=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, txtPatientID.getText());
        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "🗑️ Patient Deleted Successfully!");
        //loadPatients("");
        clearFields();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "⚠️ Error Deleting Patient: " + e.getMessage());
    }
}



    private void clearFields() {
        txtPatientID.setText("");
        txtName.setText("");
        txtAge.setText("");
        txtContact.setText("");
        txtAddress.setText("");
        genderCombo.setSelectedIndex(0);
    }

     private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnect.connectDB()) {
            String sql = "SELECT * FROM patients";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("patient_id"),
                        rs.getString("name"),
                        rs.getString("age"),
                         rs.getString("gender"),
                        rs.getString("contact"),
                        rs.getString("address"),
                        
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    // ===== PRINT All Patient Details =====
private void printPatientDetailsAsPDF() {
    try {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ No patient data available to print.");
            return;
        }

        String filePath = System.getProperty("user.home") + "/Desktop/All_Patient_Details.pdf";

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
Paragraph reportTitle = new Paragraph("Patient Details", 
        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22));
reportTitle.setAlignment(Element.ALIGN_CENTER);
doc.add(reportTitle);

doc.add(new Paragraph("\n"));

doc.add(new Paragraph("\n"));
        // PDF Table
        PdfPTable pdfTable = new PdfPTable(6);
        pdfTable.setWidthPercentage(100);
        pdfTable.setWidths(new float[]{2f, 3f, 1f, 2f, 3f, 4f});

        String[] headers = {"Patient ID", "Name", "Age", "Gender", "Contact", "Address"};
        for (String h : headers) {
            pdfTable.addCell(new com.itextpdf.text.Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        }

        // Add all patient rows
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

        JOptionPane.showMessageDialog(this, "✅ All Patient PDF saved successfully at Desktop!");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "⚠️ Error printing PDF: " + ex.getMessage());
    }
}



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientDetails());
    }
}



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

