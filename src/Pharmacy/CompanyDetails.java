package Pharmacy;

import javax.swing.*;
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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.BaseColor;


public class CompanyDetails extends JFrame {
    private JTextField txtCompanyId, txtCompanyName, txtAddress, txtContactNumber, txtEmail, txtProducts;
    private JTable table;
    private DefaultTableModel model;

    public CompanyDetails() {
        setTitle("Company Details");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 240, 255));
        
        

        // ===== HEADER =====
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 102, 204));
        JLabel headerLabel = new JLabel("Company Details");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // ===== FORM PANEL (Vertical) =====
JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
formPanel.setBackground(new Color(230, 240, 255));
formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));

// Create labels with bigger font
JLabel lblCompanyId = new JLabel("Company ID:");
lblCompanyId.setFont(new Font("Segoe UI", Font.BOLD, 18));

JLabel lblCompanyName = new JLabel("Company Name:");
lblCompanyName.setFont(new Font("Segoe UI", Font.BOLD, 18));

JLabel lblAddress = new JLabel("Address:");
lblAddress.setFont(new Font("Segoe UI", Font.BOLD, 18));

JLabel lblContactNumber = new JLabel("Contact Number:");
lblContactNumber.setFont(new Font("Segoe UI", Font.BOLD, 18));

JLabel lblEmail = new JLabel("Email:");
lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 18));

JLabel lblProducts = new JLabel("Products:");
lblProducts.setFont(new Font("Segoe UI", Font.BOLD, 18));

// Create text fields with bigger font
txtCompanyId = new JTextField();
txtCompanyId.setFont(new Font("Segoe UI", Font.PLAIN, 18));

txtCompanyName = new JTextField();
txtCompanyName.setFont(new Font("Segoe UI", Font.PLAIN, 18));

txtAddress = new JTextField();
txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 18));

txtContactNumber = new JTextField();
txtContactNumber.setFont(new Font("Segoe UI", Font.PLAIN, 18));

txtEmail = new JTextField();
txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 18));

txtProducts = new JTextField();
txtProducts.setFont(new Font("Segoe UI", Font.PLAIN, 18));

// Add components to form panel
formPanel.add(lblCompanyId); formPanel.add(txtCompanyId);
formPanel.add(lblCompanyName); formPanel.add(txtCompanyName);
formPanel.add(lblAddress); formPanel.add(txtAddress);
formPanel.add(lblContactNumber); formPanel.add(txtContactNumber);
formPanel.add(lblEmail); formPanel.add(txtEmail);
formPanel.add(lblProducts); formPanel.add(txtProducts);

add(formPanel, BorderLayout.CENTER);


        // ===== TABLE PANEL =====
        String[] cols = {"Company ID", "Company Name", "Address", "Contact Number", "Email", "Products"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
table.setRowHeight(30); // taller rows
table.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // bigger font for cell data
table.getTableHeader().setBackground(new Color(0, 102, 204));
table.getTableHeader().setForeground(Color.WHITE);
table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 20)); // bigger header font

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        add(scrollPane, BorderLayout.CENTER);
// ===== CENTER WRAPPER =====
JPanel centerWrapper = new JPanel(new BorderLayout());
centerWrapper.add(formPanel, BorderLayout.NORTH);
centerWrapper.add(scrollPane, BorderLayout.CENTER);

add(centerWrapper, BorderLayout.CENTER);

        // ===== BUTTON PANEL (Horizontal) =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(230, 240, 255));

        JButton btnAdd = createStyledButton("Add");
        JButton btnUpdate = createStyledButton("Update");
        JButton btnDelete = createStyledButton("Delete");
        JButton btnLoad = createStyledButton("Load Data");
        JButton btnPrintCompanyDetails = createStyledButton("Print Company Details");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnPrintCompanyDetails);
        add(buttonPanel, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        btnAdd.addActionListener(e -> addCompany());
        btnUpdate.addActionListener(e -> updateCompany());
        btnDelete.addActionListener(e -> deleteCompany());
        btnLoad.addActionListener(e -> loadData());
        btnPrintCompanyDetails.addActionListener(e -> printCompanyDetailsAsPDF());

        // Click table row to fill form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtCompanyId.setText(model.getValueAt(row, 0).toString());
                    txtCompanyName.setText(model.getValueAt(row, 1).toString());
                    txtAddress.setText(model.getValueAt(row, 2).toString());
                    txtContactNumber.setText(model.getValueAt(row, 3).toString());
                    txtEmail.setText(model.getValueAt(row, 4).toString());
                    txtProducts.setText(model.getValueAt(row, 5).toString());
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
   private void addCompany() {
    try (Connection conn = DBConnect.connectDB()) {
        String sql = "INSERT INTO company_details (company_name, address, contact_number, email, products) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, txtCompanyName.getText());
        pst.setString(2, txtAddress.getText());
        pst.setString(3, txtContactNumber.getText());
        pst.setString(4, txtEmail.getText());
        pst.setString(5, txtProducts.getText());
        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "Company Added Successfully!");
        loadData();
        clearFields();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}


    private void updateCompany() {
        try (Connection conn = DBConnect.connectDB()) {
            String sql = "UPDATE company_details SET company_name=?, address=?, contact_number=?, email=?, products=? WHERE company_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtCompanyName.getText());
            pst.setString(2, txtAddress.getText());
            pst.setString(3, txtContactNumber.getText());
            pst.setString(4, txtEmail.getText());
            pst.setString(5, txtProducts.getText());
            pst.setString(6, txtCompanyId.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Company Updated Successfully!");
            loadData();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteCompany() {
        try (Connection conn = DBConnect.connectDB()) {
            String sql = "DELETE FROM company_details WHERE company_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtCompanyId.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Company Deleted Successfully!");
            loadData();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtCompanyId.setText("");
        txtCompanyName.setText("");
        txtAddress.setText("");
        txtContactNumber.setText("");
        txtEmail.setText("");
        txtProducts.setText("");
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnect.connectDB()) {
            String sql = "SELECT * FROM company_details";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("company_id"),
                        rs.getString("company_name"),
                        rs.getString("address"),
                        rs.getString("contact_number"),
                        rs.getString("email"),
                        rs.getString("products")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

     //print all company details
 private void printCompanyDetailsAsPDF() {


try {

    // Check for empty table
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "⚠️ No Company Details data available to print.");
        return;
    }

    // File path
    String filePath = System.getProperty("user.home") + "/Desktop/All_Company_Details.pdf";

    // Create document
    Document doc = new Document();
    PdfWriter.getInstance(doc, new FileOutputStream(filePath));
    doc.open();

    // Add logo if available
    try {
        Image logo = Image.getInstance("src/images/logo.png");
        logo.scaleToFit(80, 80);
        logo.setAlignment(Element.ALIGN_CENTER);
        doc.add(logo);
    } catch (Exception e) {
        System.out.println("Logo missing: " + e.getMessage());
    }

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
Paragraph reportTitle = new Paragraph("Company Details", 
        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22));
reportTitle.setAlignment(Element.ALIGN_CENTER);
doc.add(reportTitle);

doc.add(new Paragraph("\n"));

doc.add(new Paragraph("\n"));

    // Table with 6 columns
    PdfPTable table = new PdfPTable(6);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{2f, 3f, 3f, 2f, 3f, 4f});

    // Table headers
    String[] headers = {"Company ID", "Company Name", "Address", "Contact Number", "Email", "Products"};

    for (String h : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(h, 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

    // Add table data
    for (int row = 0; row < model.getRowCount(); row++) {
        for (int col = 0; col < model.getColumnCount(); col++) {

            Object valueObj = model.getValueAt(row, col);
            String value = (valueObj == null ? "" : valueObj.toString());

            table.addCell(new Phrase(value));
        }
    }

    doc.add(table);

    // Footer
    doc.add(new Paragraph("\nWe provide all types of medicines. Please consult a doctor before use."));
    doc.add(new Paragraph("Thank you for visiting Miraj Medical!"));

    doc.close();

    // Try opening PDF
    try {
        Desktop.getDesktop().open(new java.io.File(filePath));
    } catch (Exception e) {
        System.out.println("Cannot open PDF automatically: " + e.getMessage());
    }

    JOptionPane.showMessageDialog(this, "✅ Company Details PDF saved successfully on Desktop!");

} catch (Exception ex) {
    JOptionPane.showMessageDialog(this, "⚠️ Error printing PDF: " + ex.getMessage());
    ex.printStackTrace();
}


}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CompanyDetails());
    }
}
