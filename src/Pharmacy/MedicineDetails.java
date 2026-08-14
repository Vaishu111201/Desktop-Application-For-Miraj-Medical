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

public class MedicineDetails extends JFrame {
    private JTextField txtId, txtName, txtCompany, txtQuantity, txtPrice, txtMfg, txtExpiry, txtType, txtPower;
    private Connection con;
    private JTable table;
private DefaultTableModel model;

private JLabel createBlackLabel(String text) {
    JLabel label = new JLabel(text);
    label.setForeground(Color.BLACK);  // Black color
    label.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Bigger font
    return label;
}




    public MedicineDetails() {
        setTitle("💊 Medicine Details");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 245, 255));
        
// Outer panel with left & right spacing
JPanel outerPanel = new JPanel(new BorderLayout());
outerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));  
outerPanel.setBackground(new Color(230, 245, 255));
add(outerPanel, BorderLayout.CENTER);

        
        // Connect to Database
        connect();

        // 🔹 Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        JLabel titleLabel = new JLabel("Medicine Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 🔹 Form Panel
JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
formPanel.setBorder(BorderFactory.createTitledBorder("Add / Update Medicine"));
formPanel.setBackground(new Color(240, 250, 255));

// Medicine ID
formPanel.add(createBlackLabel("Medicine ID:"));
txtId = new JTextField();
txtId.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtId);
txtId.addKeyListener(new KeyAdapter() {
    @Override
    public void keyReleased(KeyEvent e) {
        fetchMedicineById();
    }
});

// Name
formPanel.add(createBlackLabel("Name:"));
txtName = new JTextField();
txtName.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtName);

// Company
formPanel.add(createBlackLabel("Company:"));
txtCompany = new JTextField();
txtCompany.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtCompany);

// Quantity
formPanel.add(createBlackLabel("Quantity:"));
txtQuantity = new JTextField();
txtQuantity.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtQuantity);

// Price per Unit
formPanel.add(createBlackLabel("Price per Unit:"));
txtPrice = new JTextField();
txtPrice.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtPrice);

// MFG Date
formPanel.add(createBlackLabel("MFG Date (YYYY-MM-DD):"));
txtMfg = new JTextField();
txtMfg.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtMfg);

// Expiry Date
formPanel.add(createBlackLabel("Expiry Date (YYYY-MM-DD):"));
txtExpiry = new JTextField();
txtExpiry.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtExpiry);

// Type of Medicine
formPanel.add(createBlackLabel("Type of Medicine:"));
txtType = new JTextField();
txtType.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtType);

// Power
formPanel.add(createBlackLabel("Power (mg/ml):"));
txtPower = new JTextField();
txtPower.setFont(new Font("Segoe UI", Font.BOLD, 18));
formPanel.add(txtPower);

// Add form panel to outer panel
outerPanel.add(formPanel, BorderLayout.CENTER);


        // 🔹 Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 245, 255));

        JButton btnAdd = createButton("Add");
        JButton btnUpdate = createButton("Update");
        JButton btnDelete = createButton("Delete");
        JButton btnClear = createButton("Clear");
        
        
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
       
      
// ---- Add to JFrame ----
    add(titlePanel, BorderLayout.NORTH);
    add(outerPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);   // <- PUT IT HERE
    
   

  

        // 🔹 Button Actions
        btnAdd.addActionListener(e -> addMedicine());
        btnUpdate.addActionListener(e -> updateMedicine());
        btnDelete.addActionListener(e -> deleteMedicine());
        btnClear.addActionListener(e -> clearFields());
        
        

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
            public void mouseEntered(MouseEvent e) { button.setBackground(new Color(0, 120, 215)); }
            public void mouseExited(MouseEvent e) { button.setBackground(new Color(0, 153, 255)); }
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

    // 🔹 Add Medicine
    private void addMedicine() {
        try {
            String query = "INSERT INTO medicine (medicine_id, name, company, quantity, price_per_unit, mfg_date, expiry_date, type_of_medicine, power) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, txtId.getText());
            pst.setString(2, txtName.getText());
            pst.setString(3, txtCompany.getText());
            pst.setString(4, txtQuantity.getText());
            pst.setString(5, txtPrice.getText());
            pst.setString(6, txtMfg.getText());
            pst.setString(7, txtExpiry.getText());
            pst.setString(8, txtType.getText());
            pst.setString(9, txtPower.getText());
            pst.executeUpdate();
            showMedicinePopup("✅ Medicine Added Successfully!", txtId.getText());

            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error Adding Medicine: " + e.getMessage());
        }
    }

    // 🔹 Update Medicine
    private void updateMedicine() {
    try {
        String query = "UPDATE medicine SET name=?, company=?, quantity=?, price_per_unit=?, mfg_date=?, expiry_date=?, type_of_medicine=?, power=? WHERE medicine_id=?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, txtName.getText());
        pst.setString(2, txtCompany.getText());
        pst.setString(3, txtQuantity.getText());
        pst.setString(4, txtPrice.getText());
        pst.setString(5, txtMfg.getText());
        pst.setString(6, txtExpiry.getText());
        pst.setString(7, txtType.getText());
        pst.setString(8, txtPower.getText());
        pst.setString(9, txtId.getText());
        
        int updatedRows = pst.executeUpdate();
        if (updatedRows > 0) {
            showMedicinePopup("✅ Medicine Updated Successfully!", txtId.getText());
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "⚠️ No record found with the given Medicine ID!");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "⚠️ Error Updating Medicine: " + e.getMessage());
    }
}



    // 🔹 Delete Medicine
    private void deleteMedicine() {
    try {
        String query = "DELETE FROM medicine WHERE medicine_id=?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, txtId.getText());
        
        int deletedRows = pst.executeUpdate();
        if (deletedRows > 0) {
            JOptionPane.showMessageDialog(this,
        "🗑️ Medicine Deleted Successfully!\nDeleted ID: " + txtId.getText());

            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "⚠️ No record found with the given Medicine ID!");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "⚠️ Error Deleting Medicine: " + e.getMessage());
    }
}


    // 🔹 Clear Fields
    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtCompany.setText("");
        txtQuantity.setText("");
        txtPrice.setText("");
        txtMfg.setText("");
        txtExpiry.setText("");
        txtType.setText("");
        txtPower.setText("");
    }
    

// 🔹 Show popup with selected medicine information
private void showMedicinePopup(String title, String id) {
    try {
        if (id == null || id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Medicine ID is empty!");
            return;
        }

        String sql = "SELECT * FROM medicine WHERE medicine_id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String info =
                    "Medicine ID : " + rs.getString("medicine_id") + "\n" +
                    "Name        : " + rs.getString("name") + "\n" +
                    "Company     : " + rs.getString("company") + "\n" +
                    "Quantity    : " + rs.getString("quantity") + "\n" +
                    "Price       : " + rs.getString("price_per_unit") + "\n" +
                    "Type        : " + rs.getString("type_of_medicine") + "\n" +
                    "MFG Date    : " + rs.getString("mfg_date") + "\n" +
                    "Expiry Date : " + rs.getString("expiry_date") + "\n" +
                    "Power       : " + rs.getString("power");

            JOptionPane.showMessageDialog(this, info, title, JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "❌ No data found for ID: " + id);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error Showing Popup: " + e.getMessage());
    }
}
private void fetchMedicineById() {
    try {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            return;
        }

        String sql = "SELECT * FROM medicine WHERE medicine_id = ?";
        PreparedStatement pst = con.prepareStatement(sql);

        // Check if medicine_id is INT
        pst.setInt(1, Integer.parseInt(id)); 
        // If VARCHAR, use pst.setString(1, id);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            txtName.setText(rs.getString("name"));
            txtCompany.setText(rs.getString("company"));
            txtQuantity.setText(rs.getString("quantity"));
            txtPrice.setText(rs.getString("price_per_unit"));
            txtMfg.setText(rs.getString("mfg_date"));
            txtExpiry.setText(rs.getString("expiry_date"));
            txtType.setText(rs.getString("type_of_medicine"));
            txtPower.setText(rs.getString("power"));
        } else {
            clearFields();
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "❌ Error: " + e.getMessage());
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MedicineDetails());
    }
}
