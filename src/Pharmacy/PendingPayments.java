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

public class PendingPayments extends JFrame {
    private JTable table;
    private JTextField searchField;
    private DefaultTableModel model;

    public PendingPayments() {
        setTitle("Pending Payments - Miraj Medical");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // maximize frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 🎨 Background
        getContentPane().setBackground(new Color(230, 247, 255));
        setLayout(new BorderLayout());
        
         // ===== TABLE SETUP =====
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Bill ID", "Customer Name", "Amount Due", "Bill Date"});

        table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(255, 255, 204) : Color.WHITE);
                }
                return c;
            }
        };

        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 20));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 22));
        table.getTableHeader().setBackground(new Color(0, 123, 255));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ===== NORTH WRAPPER PANEL (TITLE + TOP PANEL) =====
        JPanel northWrapper = new JPanel(new BorderLayout());

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        JLabel titleLabel = new JLabel("Pending Payments");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        northWrapper.add(titlePanel, BorderLayout.NORTH);

        // Top panel (search + buttons)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(new Color(230, 247, 255));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        topPanel.add(searchLabel);

        searchField = new JTextField(25);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 20));
        topPanel.add(searchField);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        refreshBtn.setBackground(new Color(0, 123, 255));
        refreshBtn.setForeground(Color.WHITE);
        topPanel.add(refreshBtn);

        JButton markPaidBtn = new JButton("Mark as Paid");
        markPaidBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        markPaidBtn.setBackground(new Color(40, 167, 69));
        markPaidBtn.setForeground(Color.WHITE);
        topPanel.add(markPaidBtn);

        JButton printBtn = new JButton("Print Pending Payments");
        printBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        printBtn.setBackground(new Color(255, 153, 0));
        printBtn.setForeground(Color.WHITE);
        topPanel.add(printBtn);

        northWrapper.add(topPanel, BorderLayout.SOUTH);
        add(northWrapper, BorderLayout.NORTH);

        // ===== ACTIONS =====
        loadPendingPayments("");

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                loadPendingPayments(searchField.getText().trim());
            }
        });

        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadPendingPayments("");
        });

        // ✅ MARK AS PAID BUTTON FUNCTIONALITY
        markPaidBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a bill to mark as Paid!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to mark this bill as PAID?",
                    "Confirm", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                int billId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
                updatePaymentStatus(billId, "Paid");
                loadPendingPayments("");
            }
        });
// ✅ PRINT BUTTON ACTION
printBtn.addActionListener(e -> printPendingPaymentsDetailsAsPDF());
        setVisible(true);
    }

    // 🧾 Load pending payments from Database
    private void loadPendingPayments(String keyword) {
        model.setRowCount(0); // Clear previous rows
        try (Connection con = DBConnect.connectDB()) {
            String sql = "SELECT bill_id, customer_name, amount_due, bill_date " +
                         "FROM bills WHERE status='Pending' AND customer_name LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("bill_id"),
                        rs.getString("customer_name"),
                        String.format("%.2f", rs.getDouble("amount_due")),
                        rs.getDate("bill_date")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    // ✅ UPDATE PAYMENT STATUS METHOD
    private void updatePaymentStatus(int billId, String newStatus) {
        try (Connection con = DBConnect.connectDB()) {
            String sql = "UPDATE bills SET status = ? WHERE bill_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, newStatus);
            pst.setInt(2, billId);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Bill ID " + billId + " marked as " + newStatus + " successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Bill not found!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }
    //print all pendingpayments details
 private void printPendingPaymentsDetailsAsPDF() {
    try {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ No pending payment data available to print.");
            return;
        }

        String filePath = System.getProperty("user.home") + "/Desktop/All_PendingPayments_Details.pdf";

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
Paragraph reportTitle = new Paragraph("Pending Payments", 
        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22));
reportTitle.setAlignment(Element.ALIGN_CENTER);
doc.add(reportTitle);

doc.add(new Paragraph("\n"));


doc.add(new Paragraph("\n"));

        // PDF Table
        PdfPTable pdfTable = new PdfPTable(4);
        pdfTable.setWidthPercentage(100);
        pdfTable.setWidths(new float[]{2f, 3f, 2f, 3f});

        String[] headers = {"Bill ID", "Customer Name", "Amount Due", "Bill Date"};
        for (String h : headers) {
            pdfTable.addCell(new com.itextpdf.text.Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        }

        double totalAmount = 0;

        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                String value = model.getValueAt(row, col).toString();

                // Format bill date
                if (col == 3) {
                    java.util.Date date = java.sql.Date.valueOf(value);
                    value = new SimpleDateFormat("dd-MM-yyyy").format(date);
                }

                pdfTable.addCell(value);
            }

            // Sum amount due (column 2)
            totalAmount += Double.parseDouble(model.getValueAt(row, 2).toString());
        }

        // Add table to document
        doc.add(pdfTable);

        // Total pending amount
        doc.add(new Paragraph("\nTotal Pending Amount: ₹ " + String.format("%.2f", totalAmount),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));

        doc.add(new Paragraph("\nWe provide all types of medicines here. Please consult a doctor before taking any medicine. Thank you for visiting Miraj Medical!"));
        doc.close();

        // Open PDF automatically
        try {
            Desktop.getDesktop().open(new java.io.File(filePath));
        } catch (Exception ex) {
            System.out.println("Cannot open PDF automatically: " + ex.getMessage());
        }

        JOptionPane.showMessageDialog(this, "✅ All Pending Payments PDF saved successfully at Desktop!");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "⚠️ Error printing PDF: " + ex.getMessage());
    }
}



    public static void main(String[] args) {
        SwingUtilities.invokeLater(PendingPayments::new);
    }
}
