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

public class EmployeeDetails extends JFrame {
    private JTextField txtEmpId, txtName, txtAge, txtEmail, txtContact, txtAddress;
    private JComboBox<String> cmbGender;
    private JTable table;
    private DefaultTableModel model;

    public EmployeeDetails() {
        setTitle("Employee Details - Miraj Medical");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 245, 255));
        
// ===== TOP WRAPPER (Header + Form) =====
JPanel topWrapper = new JPanel(new BorderLayout());

        // ===== HEADER =====
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 102, 204));
        JLabel headerLabel = new JLabel("Employee Details");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerPanel.add(headerLabel);
        topWrapper.add(headerPanel, BorderLayout.NORTH);
       

     // ===== FORM PANEL (Vertical) =====
JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
formPanel.setBorder(BorderFactory.createTitledBorder("Enter Employee Details"));
formPanel.setBackground(new Color(230, 240, 255));
formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

// Label Font
Font labelFont = new Font("Segoe UI", Font.BOLD, 18);

txtEmpId = new JTextField();
txtName = new JTextField();
txtAge = new JTextField();
cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
txtEmail = new JTextField();
txtContact = new JTextField();
txtAddress = new JTextField();

// ==== LABELS WITH FONT (FIXED) ====
JLabel lblEmpId = new JLabel("Employee ID:");
lblEmpId.setFont(labelFont);
txtEmpId.setFont(new Font("Segoe UI", Font.PLAIN, 18));
formPanel.add(lblEmpId);  
formPanel.add(txtEmpId);

JLabel lblName = new JLabel("Name:");
lblName.setFont(labelFont);
txtName.setFont(new Font("Segoe UI", Font.PLAIN, 18));
formPanel.add(lblName);
formPanel.add(txtName);

JLabel lblAge = new JLabel("Age:");
lblAge.setFont(labelFont);
txtAge.setFont(new Font("Segoe UI", Font.PLAIN, 18));
formPanel.add(lblAge);
formPanel.add(txtAge);

JLabel lblGender = new JLabel("Gender:");
lblGender.setFont(labelFont);
formPanel.add(lblGender);
cmbGender.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // optional for combo font
formPanel.add(cmbGender);

JLabel lblEmail = new JLabel("Email:");
lblEmail.setFont(labelFont);
txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 18));
formPanel.add(lblEmail);
formPanel.add(txtEmail);

JLabel lblContact = new JLabel("Contact Number:");
lblContact.setFont(labelFont);
txtContact.setFont(new Font("Segoe UI", Font.PLAIN, 18));
formPanel.add(lblContact);
formPanel.add(txtContact);

JLabel lblAddress = new JLabel("Address:");
lblAddress.setFont(labelFont);
txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 18));
formPanel.add(lblAddress);
formPanel.add(txtAddress);



// Add wrapper to frame
add(topWrapper, BorderLayout.NORTH);  
topWrapper.add(formPanel, BorderLayout.CENTER);


        // ===== TABLE PANEL (Horizontal) =====
        String[] cols = {"Emp ID", "Name", "Age", "Gender", "Email", "Contact", "Address"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 20));
// Column width (for better visibility)
table.getColumnModel().getColumn(0).setPreferredWidth(80);
table.getColumnModel().getColumn(1).setPreferredWidth(150);
table.getColumnModel().getColumn(2).setPreferredWidth(60);
table.getColumnModel().getColumn(3).setPreferredWidth(100);
table.getColumnModel().getColumn(4).setPreferredWidth(180);
table.getColumnModel().getColumn(5).setPreferredWidth(120);
table.getColumnModel().getColumn(6).setPreferredWidth(200);
        

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        add(scrollPane, BorderLayout.CENTER);


        // ===== BUTTON PANEL (Horizontal) =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(230, 240, 255));

        JButton btnAdd = createStyledButton("Add");
        JButton btnUpdate = createStyledButton("Update");
        JButton btnDelete = createStyledButton("Delete");
        JButton btnLoad = createStyledButton("Load Data");
        JButton btnPrintEmployeeDetails = createStyledButton("Print Employee Details");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnPrintEmployeeDetails);

        add(buttonPanel, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        btnAdd.addActionListener(e -> addEmployee());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnLoad.addActionListener(e -> loadData());
        btnPrintEmployeeDetails.addActionListener(e -> printEmployeeDetailsAsPDF());
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtEmpId.setText(model.getValueAt(row, 0).toString());
                    txtName.setText(model.getValueAt(row, 1).toString());
                    txtAge.setText(model.getValueAt(row, 2).toString());
                    cmbGender.setSelectedItem(model.getValueAt(row, 3).toString());
                    txtEmail.setText(model.getValueAt(row, 4).toString());
                    txtContact.setText(model.getValueAt(row, 5).toString());
                    txtAddress.setText(model.getValueAt(row, 6).toString());
                }
            }
        });

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(0, 102, 204));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0, 153, 255)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(0, 102, 204)); }
        });
        return btn;
    }

    // ===== DATABASE METHODS =====
    private void addEmployee() {
        try (Connection conn = DBConnect.connectDB()) {
            String sql = "INSERT INTO employee_details (emp_id, name, age, gender, email, contact, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtEmpId.getText());
            pst.setString(2, txtName.getText());
            pst.setString(3, txtAge.getText());
            pst.setString(4, cmbGender.getSelectedItem().toString());
            pst.setString(5, txtEmail.getText());
            pst.setString(6, txtContact.getText());
            pst.setString(7, txtAddress.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Employee Added Successfully!");
            loadData();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateEmployee() {
        try (Connection conn = DBConnect.connectDB()) {
            String sql = "UPDATE employee_details SET name=?, age=?, gender=?, email=?, contact=?, address=? WHERE emp_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtName.getText());
            pst.setString(2, txtAge.getText());
            pst.setString(3, cmbGender.getSelectedItem().toString());
            pst.setString(4, txtEmail.getText());
            pst.setString(5, txtContact.getText());
            pst.setString(6, txtAddress.getText());
            pst.setString(7, txtEmpId.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Employee Updated Successfully!");
            loadData();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        try (Connection conn = DBConnect.connectDB()) {
            String sql = "DELETE FROM employee_details WHERE emp_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtEmpId.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Employee Deleted Successfully!");
            loadData();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtEmpId.setText("");
        txtName.setText("");
        txtAge.setText("");
        cmbGender.setSelectedIndex(0);
        txtEmail.setText("");
        txtContact.setText("");
        txtAddress.setText("");
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnect.connectDB()) {
            String sql = "SELECT * FROM employee_details";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("emp_id"),
                        rs.getString("name"),
                        rs.getString("age"),
                        rs.getString("gender"),
                        rs.getString("email"),
                        rs.getString("contact"),
                        rs.getString("address")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    // ===== PRINT All Employee Details =====
private void printEmployeeDetailsAsPDF() {
    try {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ No Employee data available to print.");
            return;
        }

        String filePath = System.getProperty("user.home") + "/Desktop/All_Employee_Details.pdf";

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
Paragraph reportTitle = new Paragraph("Employee Details", 
        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22));
reportTitle.setAlignment(Element.ALIGN_CENTER);
doc.add(reportTitle);

doc.add(new Paragraph("\n"));

doc.add(new Paragraph("\n"));
        // PDF Table
        PdfPTable pdfTable = new PdfPTable(7);
        pdfTable.setWidthPercentage(100);
        pdfTable.setWidths(new float[]{2f, 3f, 1f, 2f, 3f, 4f,4f});

        String[] headers = {"Emp ID", "Name", "Age", "Gender", "Email", "Contact", "Address"};
        for (String h : headers) {
            pdfTable.addCell(new com.itextpdf.text.Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        }

        // Add all Employee rows
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

        JOptionPane.showMessageDialog(this, "✅ All Employee PDF saved successfully at Desktop!");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "⚠️ Error printing PDF: " + ex.getMessage());
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeeDetails());
    }
}
