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
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class MonthlyReport extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> monthDropdown, yearDropdown;
    private JLabel totalSalesLabel, totalBillsLabel, pendingLabel;

    public MonthlyReport() {
        setTitle("Monthly Report - Miraj Medical");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
       // ===== TITLE + TOP PANEL WRAPPER =====
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        // ===== TITLE PANEL =====
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 102, 204));
        titlePanel.setPreferredSize(new Dimension(0, 60));

        JLabel titleLabel = new JLabel("Monthly Report", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        northPanel.add(titlePanel);

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        topPanel.setBackground(new Color(230, 247, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(monthLabel);

        monthDropdown = new JComboBox<>();
        monthDropdown.setFont(new Font("SansSerif", Font.PLAIN, 16));
        for (int m = 1; m <= 12; m++) {
            String monthName = YearMonth.of(2025, m)
                    .getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            monthDropdown.addItem(monthName);
        }
        topPanel.add(monthDropdown);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(yearLabel);

        yearDropdown = new JComboBox<>();
        yearDropdown.setFont(new Font("SansSerif", Font.PLAIN, 16));
        for (int y = 2025; y <= 2030; y++) {
            yearDropdown.addItem(String.valueOf(y));
        }
        topPanel.add(yearDropdown);

        JButton loadBtn = new JButton("Load Report");
        loadBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        loadBtn.setBackground(new Color(0, 123, 255));
        loadBtn.setForeground(Color.WHITE);
        loadBtn.setFocusPainted(false);
        topPanel.add(loadBtn);

        JButton printBtn = new JButton("Print Monthly Report");
        printBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        printBtn.setBackground(new Color(0, 123, 255));
        printBtn.setForeground(Color.WHITE);
        printBtn.setFocusPainted(false);
        topPanel.add(printBtn);

        northPanel.add(topPanel);
        add(northPanel, BorderLayout.NORTH);

        // ===== TABLE =====
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
        table.setFont(new Font("SansSerif", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(0, 123, 255));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // ===== BOTTOM PANEL =====
        totalSalesLabel = createTotalLabel("Total Sales: ₹0.00", new Color(204, 229, 255), 20);
        totalBillsLabel = createTotalLabel("Total Bills: 0", new Color(204, 255, 204), 20);
        pendingLabel = createTotalLabel("Pending Payments: ₹0.00", new Color(255, 102, 102), 20);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 50, 0));
        bottomPanel.setBackground(new Color(230, 247, 255));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(totalSalesLabel);
        bottomPanel.add(totalBillsLabel);
        bottomPanel.add(pendingLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        loadBtn.addActionListener(e -> loadMonthlyReport());
        printBtn.addActionListener(e -> printMonthlyReportAsPDF());

        setVisible(true);
    }

    private JLabel createTotalLabel(String text, Color bgColor, int fontSize) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return label;
    }

    private void loadMonthlyReport() {
        model.setRowCount(0);
        float totalSales = 0;
        float totalPending = 0;
        int totalBills = 0;

        String year = (String) yearDropdown.getSelectedItem();
        int month = monthDropdown.getSelectedIndex() + 1;

        try (Connection con = DBConnect.connectDB()) {
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed!");
                return;
            }

            String sql = "SELECT * FROM bills WHERE MONTH(bill_date)=? AND YEAR(bill_date)=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, month);
            pst.setInt(2, Integer.parseInt(year));
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
//print all monthly report
private void printMonthlyReportAsPDF() {
    try {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ No Monthly report data available to print.");
            return;
        }

        String filePath = System.getProperty("user.home") + "/Desktop/All_Monthly_Report.pdf";

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
Paragraph reportTitle = new Paragraph("Monthly Report", 
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


    public static void main(String[] args) {
        SwingUtilities.invokeLater(MonthlyReport::new);
    }
}
