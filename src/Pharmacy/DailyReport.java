package Pharmacy;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;



public class DailyReport extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JSpinner dateSpinner;
    private JLabel totalSalesLabel, totalBillsLabel, pendingLabel;

    public DailyReport() {
        setTitle("Daily Report - Miraj Medical");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        // ===== TITLE + TOP PANEL WRAPPER =====
JPanel northPanel = new JPanel();
northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS)); // vertical layout

      // ===== TITLE PANEL =====
JPanel titlePanel = new JPanel(new BorderLayout());
titlePanel.setBackground(new Color(0, 102, 204));
titlePanel.setPreferredSize(new Dimension(0, 60)); // height

JLabel titleLabel = new JLabel("Daily Report", SwingConstants.CENTER);
titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
titleLabel.setForeground(Color.WHITE);

titlePanel.add(titleLabel, BorderLayout.CENTER);
northPanel.add(titlePanel); // add title to wrapper


        // ===== TOP PANEL (DATE + BUTTONS) =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(new Color(230, 247, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel selectDateLabel = new JLabel("Select Date:");
        selectDateLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        topPanel.add(selectDateLabel);

        // Date spinner
        Date today = new Date();
        Date maxDate = null;
        try {
            maxDate = new SimpleDateFormat("yyyy-MM-dd").parse("2025-12-31");
        } catch (Exception ex) { ex.printStackTrace(); }

        dateSpinner = new JSpinner(new SpinnerDateModel(today, null, maxDate, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(new Font("SansSerif", Font.PLAIN, 18));
        topPanel.add(dateSpinner);

        // Load Report Button
        JButton loadBtn = new JButton("Load Report");
        loadBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        loadBtn.setBackground(new Color(0, 123, 255));
        loadBtn.setForeground(Color.WHITE);
        loadBtn.setFocusPainted(false);
        topPanel.add(loadBtn);

        // Print Daily Report Button
        JButton printBtn = new JButton("Print Daily Report");
        printBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        printBtn.setBackground(new Color(255, 153, 0));
        printBtn.setForeground(Color.WHITE);
        printBtn.setFocusPainted(false);
        topPanel.add(printBtn);

        add(topPanel, BorderLayout.PAGE_START);

        // ===== CENTER TABLE =====
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Bill ID", "Customer Name", "Bill Date", "Amount Due", "Status"});

        table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                String status = getValueAt(row, 4).toString();
                if (status.equalsIgnoreCase("Paid")) c.setBackground(new Color(204, 255, 204));
                else if (status.equalsIgnoreCase("Pending")) c.setBackground(new Color(255, 255, 153));
                else c.setBackground(Color.WHITE);
                return c;
            }
        };

        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 18));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 20));
        table.getTableHeader().setBackground(new Color(0, 123, 255));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // ===== BOTTOM PANEL (TOTALS) =====
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 50, 0));
        bottomPanel.setBackground(new Color(230, 247, 255));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalSalesLabel = createTotalLabel("Total Sales: ₹0.00", new Color(204, 229, 255));
        totalBillsLabel = createTotalLabel("Total Bills: 0", new Color(204, 255, 204));
        pendingLabel = createTotalLabel("Pending Payments: ₹0.00", new Color(255, 102, 102));

        bottomPanel.add(totalSalesLabel);
        bottomPanel.add(totalBillsLabel);
        bottomPanel.add(pendingLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        // ===== BUTTON ACTIONS =====
        loadBtn.addActionListener(e -> { if (isValidSelectedDate()) loadDailyReport(); });
        printBtn.addActionListener(e -> { if (isValidSelectedDate()) printDailyReportAsPDF(); });
// Add top panel to wrapper
northPanel.add(topPanel);

// Add the combined north panel to the frame
add(northPanel, BorderLayout.NORTH);
        setVisible(true);
    }


    // ===== CREATE TOTAL LABEL =====
    private JLabel createTotalLabel(String text, Color bgColor) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return label;
    }

    // ===== LOAD DAILY REPORT =====
    private void loadDailyReport() {
        model.setRowCount(0);
        float totalSales = 0, totalPending = 0;
        int totalBills = 0;

        Date selectedDate = (Date) dateSpinner.getValue();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

        try (Connection con = DBConnect.connectDB()) {
            if (con == null) { JOptionPane.showMessageDialog(this, "Database connection failed!"); return; }

            String sql = "SELECT * FROM bills WHERE bill_date >= ? AND bill_date <= ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, dateStr + " 00:00:00");
            pst.setString(2, dateStr + " 23:59:59");

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                float amount = rs.getFloat("amount_due");
                String status = rs.getString("status");
                totalSales += amount;
                if (status.equalsIgnoreCase("Pending")) totalPending += amount;
                totalBills++;

                model.addRow(new Object[]{
                        rs.getString("bill_id"),
                        rs.getString("customer_name"),
                        rs.getDate("bill_date"),
                        "₹" + amount,
                        status
                });
            }

            totalSalesLabel.setText("Total Sales: ₹" + totalSales);
            totalBillsLabel.setText("Total Bills: " + totalBills);
            pendingLabel.setText("Pending Payments: ₹" + totalPending);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    //print all daily report
private void printDailyReportAsPDF() {
    try {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ No daily report data available to print.");
            return;
        }

        String filePath = System.getProperty("user.home") + "/Desktop/All_Daily_Report.pdf";

        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(filePath));
        doc.open();

        try {
    com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance("src/images/logo.png");
    logo.scaleToFit(100, 100);
    logo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
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
Paragraph reportTitle = new Paragraph("Daily Report", 
        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22));
reportTitle.setAlignment(Element.ALIGN_CENTER);
doc.add(reportTitle);

doc.add(new Paragraph("\n"));




doc.add(new Paragraph("\n"));
        // PDF Table
        PdfPTable pdfTable = new PdfPTable(5);
        pdfTable.setWidthPercentage(100);
        pdfTable.setWidths(new float[]{2f, 3f, 1f, 2f, 3f});

        String[] headers = {"Bill ID", " Customer Name", "Bill Date", "Amount Due", "Status"};
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

        JOptionPane.showMessageDialog(this, "✅ All Daily Report PDF saved successfully at Desktop!");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "⚠️ Error printing PDF: " + ex.getMessage());
    }
}

private boolean isValidSelectedDate() {
    try {
        Date selected = (Date) dateSpinner.getValue();
        
        // Convert to YYYY-MM-DD for comparison
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String selectedStr = sdf.format(selected);

        // Define max allowed date (2025-12-31)
        Date maxAllowed = sdf.parse("2025-12-31");

        // Today's date
        Date today = new Date();

        // ❌ Future date limit (max: 2025-12-31)
        if (selected.after(maxAllowed)) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Date cannot be greater than 2025!",
                    "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // ❌ Should not be after today
        if (selected.after(today)) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Future dates are not allowed!",
                    "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;  // Valid

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(DailyReport::new);
    }
}
