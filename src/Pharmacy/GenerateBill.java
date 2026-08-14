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
// remove or comment this import — we’ll use full path for iText Font
// import com.itextpdf.text.Font;

public class GenerateBill extends JFrame {


// ===== Database =====
private static final String DB_URL = "jdbc:mysql://localhost:3306/miraj_medical_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "leon5cprytv8@";

// ===== Components =====
private JTextField txtPatientName, txtPatientAge, txtSearchMedicine, txtQuantity;
private JSpinner dateSpinner;
private JTable table;
private DefaultTableModel model;
private JLabel lblTotalAmount, lblGST;
private JButton btnAddToBill, btnRemoveSelected, btnSaveBill, btnPrintBill;

// ===== Variables =====
private double grandTotal = 0.0;
private String currentBillId = null;

public GenerateBill() {
    setTitle("Generate Bill - Miraj Medical Pal");
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout());

    // ===== Header =====
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(230, 247, 255));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JLabel lblLogo = new JLabel(new ImageIcon("src/images/logo.png"));
    headerPanel.add(lblLogo, BorderLayout.WEST);

    JTextArea clinicInfo = new JTextArea(
        "Miraj Medical Pal\n" +
        "Address: Near Satara District Cooperative Bank, Pal, Tal-Karad\n" +
        "Owner: Mubarak Sutar\n" +
        "Contact: 9960924671, 8208287689"
    );
    clinicInfo.setEditable(false);
    clinicInfo.setFont(new Font("SansSerif", Font.BOLD, 16));
    clinicInfo.setBackground(new Color(230, 247, 255));
    clinicInfo.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
    headerPanel.add(clinicInfo, BorderLayout.CENTER);
    add(headerPanel, BorderLayout.NORTH);

    // ===== Center =====
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // --- Patient Info ---
    JPanel patientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
    patientPanel.setBackground(new Color(230, 247, 255));

    JLabel lblName = new JLabel("Patient Name:");
    lblName.setFont(new Font("SansSerif", Font.BOLD, 16));
    patientPanel.add(lblName);

    txtPatientName = new JTextField(20);
    txtPatientName.setFont(new Font("SansSerif", Font.PLAIN, 16));
    patientPanel.add(txtPatientName);

    JLabel lblAge = new JLabel("Age:");
    lblAge.setFont(new Font("SansSerif", Font.BOLD, 16));
    patientPanel.add(lblAge);

    txtPatientAge = new JTextField(5);
    txtPatientAge.setFont(new Font("SansSerif", Font.PLAIN, 16));
    patientPanel.add(txtPatientAge);

    JLabel lblDate = new JLabel("Date:");
    lblDate.setFont(new Font("SansSerif", Font.BOLD, 16));
    patientPanel.add(lblDate);

    SpinnerDateModel dateModel = new SpinnerDateModel();
    dateSpinner = new JSpinner(dateModel);
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
    dateSpinner.setEditor(dateEditor);
    dateSpinner.setValue(new java.util.Date());
    dateSpinner.setFont(new Font("SansSerif", Font.PLAIN, 16));
    patientPanel.add(dateSpinner);

    centerPanel.add(patientPanel, BorderLayout.NORTH);

    // --- Table ---
    model = new DefaultTableModel();
    model.setColumnIdentifiers(new String[]{"Bill ID", "Medicine Name", "Type", "Power", "Price/unit", "Qty", "Expiry Date", "Total"});
    table = new JTable(model);
    table.setRowHeight(28);
    table.setFont(new Font("SansSerif", Font.PLAIN, 16));
    table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 18));
    table.getTableHeader().setBackground(new Color(0, 123, 255));
    table.getTableHeader().setForeground(Color.WHITE);

    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    for (int i : new int[]{4, 5, 7}) {
        table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    JScrollPane scroll = new JScrollPane(table);
    centerPanel.add(scroll, BorderLayout.CENTER);

    // --- Action Panel ---
    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
    actionPanel.setBackground(new Color(230, 247, 255));

    JLabel lblMedicine = new JLabel("Medicine Name:");
    lblMedicine.setFont(new Font("SansSerif", Font.BOLD, 16));
    actionPanel.add(lblMedicine);

    txtSearchMedicine = new JTextField(20);
    txtSearchMedicine.setFont(new Font("SansSerif", Font.PLAIN, 16));
    actionPanel.add(txtSearchMedicine);

    JLabel lblQty = new JLabel("Qty:");
    lblQty.setFont(new Font("SansSerif", Font.BOLD, 16));
    actionPanel.add(lblQty);

    txtQuantity = new JTextField("1", 4);
    txtQuantity.setFont(new Font("SansSerif", Font.PLAIN, 16));
    actionPanel.add(txtQuantity);

    // --- Buttons ---
    btnAddToBill = new JButton("Add to Bill");
    btnAddToBill.setFont(new Font("SansSerif", Font.BOLD, 16));
    actionPanel.add(btnAddToBill);

    btnRemoveSelected = new JButton("Remove Selected");
    btnRemoveSelected.setFont(new Font("SansSerif", Font.BOLD, 16));
    actionPanel.add(btnRemoveSelected);

    btnSaveBill = new JButton("Save Bill");
    btnSaveBill.setFont(new Font("SansSerif", Font.BOLD, 16));
    actionPanel.add(btnSaveBill);

    btnPrintBill = new JButton("Print Bill");
    btnPrintBill.setFont(new Font("SansSerif", Font.BOLD, 16));
    actionPanel.add(btnPrintBill);

    lblTotalAmount = new JLabel("Total: ₹0.00");
    lblTotalAmount.setFont(new Font("SansSerif", Font.BOLD, 18));
    actionPanel.add(lblTotalAmount);

    lblGST = new JLabel("GST (0%): ₹0.00");
    lblGST.setFont(new Font("SansSerif", Font.BOLD, 18));
    actionPanel.add(lblGST);

    centerPanel.add(actionPanel, BorderLayout.SOUTH);
    add(centerPanel, BorderLayout.CENTER);

    // ===== Footer =====
    JPanel footer = new JPanel();
    footer.setBackground(new Color(230, 247, 255));
    JTextArea footerText = new JTextArea("We provide all types of medicines here. Please consult a doctor before taking any medicine.");
    footerText.setEditable(false);
    footerText.setFont(new Font("SansSerif", Font.BOLD, 16));
    footerText.setBackground(new Color(230, 247, 255));
    footer.add(footerText);
    add(footer, BorderLayout.PAGE_END);

    // ===== Events =====
    setupAutoComplete();
    setupButtonActions();

    setVisible(true);
}

// ===== Auto-complete for medicine search =====
private void setupAutoComplete() {
    txtSearchMedicine.addKeyListener(new KeyAdapter() {
        private final JPopupMenu popup = new JPopupMenu();
        @Override
        public void keyReleased(KeyEvent e) {
            String text = txtSearchMedicine.getText().trim();
            popup.setVisible(false);
            popup.removeAll();
            if (text.isEmpty()) return;

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pst = conn.prepareStatement(
                     "SELECT name FROM medicine WHERE name LIKE ? ORDER BY name ASC LIMIT 6")) {

                pst.setString(1, "%" + text + "%");
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    String name = rs.getString("name");
                    JMenuItem item = new JMenuItem(name);
                    item.setFont(new Font("SansSerif", Font.PLAIN, 16));
                    item.addActionListener(ae -> {
                        txtSearchMedicine.setText(name);
                        popup.setVisible(false);
                    });
                    popup.add(item);
                }

                if (popup.getComponentCount() > 0)
                    popup.show(txtSearchMedicine, 0, txtSearchMedicine.getHeight());

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
            }
        }
    });
}

// ===== Button Actions =====
private void setupButtonActions() {
    // Initial state: Save and Print disabled
    btnSaveBill.setEnabled(false);
    btnPrintBill.setEnabled(false);

    btnAddToBill.addActionListener(e -> {
        addMedicineToBill();
        if (table.getRowCount() > 0) {
            btnSaveBill.setEnabled(true);
            btnPrintBill.setEnabled(true);
        }
    });

    btnRemoveSelected.addActionListener(e -> {
        removeSelectedRow();
        if (table.getRowCount() == 0) {
            btnSaveBill.setEnabled(false);
            btnPrintBill.setEnabled(false);
        }
    });

    btnSaveBill.addActionListener(e -> {
        saveBillToDatabase();
        btnSaveBill.setEnabled(false);
    });

    btnPrintBill.addActionListener(e -> printBillAsPDF());
}

// ===== Generate Bill ID =====
private String generateBillID() {
    String billID = "";
    try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         Statement st = con.createStatement();
         ResultSet rs = st.executeQuery("SELECT bill_id FROM bills ORDER BY bill_id DESC LIMIT 1")) {

        if (rs.next()) {
            String lastId = rs.getString("bill_id");
            int num = Integer.parseInt(lastId.replaceAll("[^0-9]", "")) + 1;
            billID = "BILL" + String.format("%03d", num);
        } else {
            billID = "BILL001";
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return billID;
}

// ===== Add Medicine =====
private void addMedicineToBill() {
    String medText = txtSearchMedicine.getText().trim();
    if (medText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Enter medicine name.");
        return;
    }

    int qty;
    try {
        qty = Integer.parseInt(txtQuantity.getText().trim());
        if (qty <= 0) throw new NumberFormatException();
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Enter valid quantity.");
        return;
    }

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        String sql = "SELECT name, type_of_medicine, power, price_per_unit, expiry_date FROM medicine WHERE name = ? LIMIT 1";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, medText);
            ResultSet rs = pst.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Medicine not found.");
                return;
            }

            String name = rs.getString("name");
            String type = rs.getString("type_of_medicine");
            String power = rs.getString("power");
            double price = rs.getDouble("price_per_unit");
            Date expiry = rs.getDate("expiry_date");

            if (currentBillId == null) currentBillId = generateBillID();

            double total = price * qty;
            model.addRow(new Object[]{currentBillId, name, type, power, price, qty, expiry, total});
            grandTotal += total;
            updateTotalLabels();

            txtSearchMedicine.setText("");
            txtQuantity.setText("1");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
    }
}




    private void removeSelectedRow() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to remove.");
            return;
        }
        double lineTotal = Double.parseDouble(table.getValueAt(sel, 7).toString());
        grandTotal -= lineTotal;
        model.removeRow(sel);
        updateTotalLabels();
    }

    private void updateTotalLabels() {
        lblTotalAmount.setText(String.format("Total: ₹%.2f", grandTotal));
        lblGST.setText("GST (0%): ₹0.00");
    }

    // ===== SAVE BILL =====
    private void saveBillToDatabase() {
    String customerName = txtPatientName.getText().trim();
    if (customerName.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Enter patient name before saving!");
        return;
    }

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        String sql = "INSERT INTO bills (customer_name, amount_due, bill_date, status) VALUES (?, ?, ?, 'Pending')";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, customerName);
        ps.setDouble(2, grandTotal);
        ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));

        ps.executeUpdate();

        // ✅ Get auto-generated bill ID from MySQL
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int billId = rs.getInt(1);
            String formattedBillId = String.format("BILL%03d", billId); // Example: BILL001, BILL002
            JOptionPane.showMessageDialog(this, "Bill saved successfully! Bill ID: " + formattedBillId);
        }
// ✅ Ask user if they want to print the bill now
int printChoice = JOptionPane.showConfirmDialog(this, "Bill saved successfully! Do you want to print the bill?", "Print Bill", JOptionPane.YES_NO_OPTION);
if (printChoice == JOptionPane.YES_OPTION) {
    printBillAsPDF();
}
        // ✅ Reset for next bill entry (only after successful save)
        currentBillId = null;
        model.setRowCount(0); // clears table rows
        grandTotal = 0;
        updateTotalLabels();
        txtPatientName.setText("");
        txtPatientAge.setText("");

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
    }
}



    // ===== PRINT BILL =====
    private void printBillAsPDF() {
        try {
            String customerName = txtPatientName.getText().trim();
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String filePath = System.getProperty("user.home") + "/Desktop/" + customerName + "_Bill.pdf";

            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(filePath));
            doc.open();
try {
    Image logo = Image.getInstance("src/images/logo.png"); // path to your logo
    logo.scaleToFit(100, 100); // size of logo (width, height)
    logo.setAlignment(Element.ALIGN_CENTER); // center it on top
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
Paragraph reportTitle = new Paragraph("Patient Bill", 
        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22));
reportTitle.setAlignment(Element.ALIGN_CENTER);
doc.add(reportTitle);

doc.add(new Paragraph("\n"));

            doc.add(new Paragraph("Patient: " + txtPatientName.getText() + " | Age: " + txtPatientAge.getText()));
            String billIdToPrint = (currentBillId != null) ? currentBillId : generateBillID();
            doc.add(new Paragraph("Bill ID: " + billIdToPrint));

            doc.add(new Paragraph("Date: " + date + "\n\n"));

            PdfPTable pdfTable = new PdfPTable(6);
            pdfTable.addCell("Medicine Name");
            pdfTable.addCell("Type");
            pdfTable.addCell("Power");
            pdfTable.addCell("Price");
            pdfTable.addCell("Quantity");
            pdfTable.addCell("Expiry");
            

            for (int i = 0; i < model.getRowCount(); i++) {
                pdfTable.addCell(model.getValueAt(i, 1).toString());
                pdfTable.addCell(model.getValueAt(i, 2).toString());
                pdfTable.addCell(model.getValueAt(i, 3).toString());
                pdfTable.addCell(model.getValueAt(i, 4).toString());
                pdfTable.addCell(model.getValueAt(i, 5).toString());
                pdfTable.addCell(model.getValueAt(i, 6).toString());
            }

            doc.add(pdfTable);
            doc.add(new Paragraph("\nGST (0%): ₹0.00",FontFactory.getFont(FontFactory.HELVETICA, 12)));
            doc.add(new Paragraph("\nTotal Amount: ₹" + String.format("%.2f", grandTotal), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            doc.add(new Paragraph("\n\nWe provide all types of medicines here. Please consult a doctor before taking any medicine. Thank you for visiting Miraj Medical!"));
            doc.close();
            // --- Open PDF automatically ---
        try {
            Desktop.getDesktop().open(new java.io.File(filePath));
        } catch (Exception ex) {
            System.out.println("Cannot open PDF automatically: " + ex.getMessage());
        }

            JOptionPane.showMessageDialog(this, "Bill PDF saved successfully at Desktop!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error printing bill: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GenerateBill::new);
    }
}
