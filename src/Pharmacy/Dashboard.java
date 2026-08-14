package Pharmacy;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private String username; // store the logged-in username

    // 🔹 Constructor with username (called from LoginForm)
    public Dashboard(String username) {
        this.username = username;
        initializeDashboard();
    }

    // 🔹 Default constructor (optional, for testing)
    public Dashboard() {
        this.username = "Owner";
        initializeDashboard();
    }

    // 🔹 Common dashboard setup code
    private void initializeDashboard() {
        setTitle("Miraj Medical - Dashboard (" + username + ")");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBackground(new Color(0, 102, 204));

        // Logo in header
        try {
            ImageIcon logoIcon = new ImageIcon("src/images/logo.png");
            Image logoImg = logoIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
            logoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            headerPanel.add(logoLabel, BorderLayout.WEST);
        } catch (Exception e) {
            JLabel logoLabel = new JLabel("🏥");
            logoLabel.setFont(new Font("Serif", Font.BOLD, 36));
            logoLabel.setForeground(Color.WHITE);
            headerPanel.add(logoLabel, BorderLayout.WEST);
        }

        // Title
        JLabel titleLabel = new JLabel("Miraj Medical - Welcome, " + username, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 34));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // ===== MAIN PANEL WITH BACKGROUND =====
        JPanel mainPanel = new JPanel() {
            private Image bg = new ImageIcon("src/images/background.jpg").getImage();

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridLayout(3, 4, 30, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // ===== BUTTONS WITH COLORS AND ICONS =====
        JButton patientBtn = createButton("Patient Details", new Color(0, 123, 255), "src/images/patient.jpg");
        JButton doctorBtn = createButton("Doctor Details", new Color(255, 99, 132), "src/images/doctor.jpg");
        JButton medicineBtn = createButton("Medicine Details", new Color(255, 159, 64), "src/images/medicine.jpg");
        JButton companyBtn = createButton("Company Details", new Color(255, 205, 86), "src/images/company.jpg");
        JButton employeeBtn = createButton("Employee Details", new Color(75, 192, 192), "src/images/employee.jpg");
        JButton stockBtn = createButton("View Stock", new Color(54, 162, 235), "src/images/stock.png");
        JButton expiryBtn = createButton("Expiry Tracker", new Color(153, 102, 255), "src/images/expiry.png");
        JButton pendingBtn = createButton("Pending Payments", new Color(255, 69, 0), "src/images/payment.jpg");
        JButton dailyBtn = createButton("Daily Report", new Color(255, 99, 132), "src/images/report.png");
        JButton monthlyBtn = createButton("Monthly Report", new Color(54, 162, 235), "src/images/report.png");
        JButton generateBillBtn = createButton("Generate Bill", new Color(75, 192, 192), "src/images/bill.png");
        JButton logoutBtn = createButton("Logout", new Color(220, 53, 69), "src/images/logout.jpg");

        // Add buttons to main panel
        JButton[] buttons = {patientBtn, doctorBtn, medicineBtn, companyBtn, employeeBtn,
                stockBtn, expiryBtn, pendingBtn, dailyBtn, monthlyBtn, generateBillBtn, logoutBtn};
        for (JButton btn : buttons) mainPanel.add(btn);

        add(mainPanel, BorderLayout.CENTER);

        // ===== BUTTON ACTIONS =====
        patientBtn.addActionListener(e -> new PatientDetails().setVisible(true));
        doctorBtn.addActionListener(e -> new DoctorDetails().setVisible(true));
        medicineBtn.addActionListener(e -> new MedicineDetails().setVisible(true));
        companyBtn.addActionListener(e -> new CompanyDetails().setVisible(true));
        employeeBtn.addActionListener(e -> new EmployeeDetails().setVisible(true));
        stockBtn.addActionListener(e -> new ViewStock().setVisible(true));
        expiryBtn.addActionListener(e -> new ExpiryTracker().setVisible(true));
        pendingBtn.addActionListener(e -> new PendingPayments().setVisible(true));
        dailyBtn.addActionListener(e -> new DailyReport().setVisible(true));
        monthlyBtn.addActionListener(e -> new MonthlyReport().setVisible(true));
        generateBillBtn.addActionListener(e -> new GenerateBill().setVisible(true));

        // ===== LOGOUT BUTTON =====
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });

        setVisible(true);
    }

    // ===== CREATE BUTTON METHOD =====
    private JButton createButton(String text, Color color, String iconPath) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            ImageIcon icon = new ImageIcon(iconPath);
            Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // ignore missing icon
        }

        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(15);
        return btn;
    }

    // ===== MAIN METHOD (for testing) =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard("Owner"));
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
            .addGap(0, 306, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables


