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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ViewStock extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private Connection con;

    public ViewStock() {
        setTitle("📦 View Stock");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 245, 255));

        // Connect to database
        connect();

        // 🔹 Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        JLabel titleLabel = new JLabel("View Stock");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
// 🔹 Button Panel
        JPanel buttonPanel = new JPanel();
buttonPanel.setBackground(new Color(230, 245, 255));
add(buttonPanel, BorderLayout.SOUTH);

        JButton btnViewStock = new JButton("Print View Stock");
        btnViewStock.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnViewStock.setBackground(new Color(0, 123, 255));
        btnViewStock.setForeground(Color.WHITE);
        btnViewStock.setFocusPainted(false);
        buttonPanel.add(btnViewStock);
        btnViewStock.addActionListener(e -> printViewStockAsPDF());

        // 🔹 Table
        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Company", "Quantity", "Price per Unit", "Type", "Power"}, 0
        );
        
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        table.getTableHeader().setBackground(new Color(0, 153, 255));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 20));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadStockData();

        setVisible(true);
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

    // 🔹 Load Stock Data
    private void loadStockData() {
        try {
            model.setRowCount(0);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM medicine");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getString("company"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_per_unit"),
                        rs.getString("type_of_medicine"),
                        rs.getString("power")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error Loading Data: " + e.getMessage());
        }
    }
// ===== PRINT All View Stock Details =====
private void printViewStockAsPDF() {
    try {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ No View Stock data available to print.");
            return;
        }

        String filePath = System.getProperty("user.home") + "/Desktop/All_View_Stock.pdf";

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
Paragraph reportTitle = new Paragraph("View Stock", 
        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22));
reportTitle.setAlignment(Element.ALIGN_CENTER);
doc.add(reportTitle);

doc.add(new Paragraph("\n"));

doc.add(new Paragraph("\n"));
        // PDF Table
        PdfPTable pdfTable = new PdfPTable(7);
        pdfTable.setWidthPercentage(100);
        pdfTable.setWidths(new float[]{2f, 3f, 3f, 2f, 3f, 4f, 4f});

        String[] headers = {"ID", "Name", "Company", "Quantity", "PricePerUnit", "Type", "Power"};
        for (String h : headers) {
            pdfTable.addCell(new com.itextpdf.text.Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        }

        // Add all Expiry Tracker rows
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

        JOptionPane.showMessageDialog(this, "✅ All Expiry Tracker PDF saved successfully at Desktop!");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "⚠️ Error printing PDF: " + ex.getMessage());
    }
}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewStock::new);
    }
}
