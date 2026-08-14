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

public class ExpiryTracker extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private Connection con;

    public ExpiryTracker() {
        setTitle("⚠️ Expiry Tracker");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(255, 235, 235));

        // Connect to database
        connect();

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(204, 0, 0));
        JLabel titleLabel = new JLabel("Expiry Alert - Medicines Near or Past Expiry");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // 🔹 Button Panel
JPanel buttonPanel = new JPanel();
buttonPanel.setBackground(new Color(230, 245, 255));
add(buttonPanel, BorderLayout.SOUTH);

JButton btnExpiryTracker = new JButton("Print Expiry Tracker");
btnExpiryTracker.setBackground(new Color(0, 123, 255));
btnExpiryTracker.setForeground(Color.WHITE);
btnExpiryTracker.setFocusPainted(false);
btnExpiryTracker.setFont(new Font("Segoe UI", Font.BOLD, 18)); // ⬅ Increased Button Font
buttonPanel.add(btnExpiryTracker);
btnExpiryTracker.addActionListener(e -> printExpiryTrackerAsPDF());

// ===== TABLE =====
model = new DefaultTableModel(new String[]{"Name", "Expiry Date", "Days Left", "Status"}, 0);
table = new JTable(model);

table.setRowHeight(32); // Slightly bigger rows
table.setFont(new Font("Segoe UI", Font.PLAIN, 20));  // ⬅ Increased table row font

// Increase table header font
table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 22)); // ⬅ Header Font Increase
table.getTableHeader().setBackground(new Color(204, 0, 0));
table.getTableHeader().setForeground(Color.WHITE);

JScrollPane scrollPane = new JScrollPane(table);
scrollPane.setBorder(BorderFactory.createLineBorder(new Color(204, 0, 0), 2));
add(scrollPane, BorderLayout.CENTER);


        // Load alert data
        loadExpiryTrackerData();

        setVisible(true);
    }

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

    private void loadExpiryTrackerData() {
        try {
            model.setRowCount(0);
            String query = "SELECT name, expiry_date FROM medicine";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            LocalDate today = LocalDate.now();

            while (rs.next()) {
                LocalDate expiry = rs.getDate("expiry_date").toLocalDate();
                long daysLeft = ChronoUnit.DAYS.between(today, expiry);
                if (daysLeft <= 30) { // Near expiry (1 month) or expired
                    String status = (daysLeft < 0) ? "Expired" : "Near Expiry";
                    model.addRow(new Object[]{
                            rs.getString("name"),
                            expiry,
                            Math.max(daysLeft, 0),
                            status
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error Loading Data: " + e.getMessage());
        }
    }
// ===== PRINT All Expiry Tracker Details =====
private void printExpiryTrackerAsPDF() {
    try {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ No Expiry Tracker data available to print.");
            return;
        }

        String filePath = System.getProperty("user.home") + "/Desktop/All_Expiry_Tracker.pdf";

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
Paragraph reportTitle = new Paragraph("Expiry Tracker", 
        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22));
reportTitle.setAlignment(Element.ALIGN_CENTER);
doc.add(reportTitle);

doc.add(new Paragraph("\n"));

doc.add(new Paragraph("\n"));
        // PDF Table
        PdfPTable pdfTable = new PdfPTable(4);
        pdfTable.setWidthPercentage(100);
        pdfTable.setWidths(new float[]{2f, 3f, 1f, 2f});

        String[] headers = {"Name", "Expiry Date", "Day Left", "Status"};
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
        SwingUtilities.invokeLater(() -> new ExpiryTracker());
    }
}
