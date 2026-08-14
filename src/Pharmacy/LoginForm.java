package Pharmacy;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginForm() {
        setTitle("Login - Miraj Medical");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(true);

        // 🔹 Background Image
        JLabel background = new JLabel();
        background.setBounds(0, 0, getWidth(), getHeight());
        background.setLayout(null);
        try {
            ImageIcon bgIcon = new ImageIcon("src/images/background.jpg");
            Image bgImg = bgIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
            background.setIcon(new ImageIcon(bgImg));
        } catch (Exception e) {
            background.setBackground(new Color(230, 247, 255));
            background.setOpaque(true);
            System.out.println("⚠️ Background image not found.");
        }
        add(background);

        // 🏥 Logo
        JLabel lblLogo = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/logo.png"));
            Image logoImg = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(logoImg));
        } catch (Exception e) {
            lblLogo.setText("🏥");
            lblLogo.setFont(new Font("Serif", Font.BOLD, 40));
            lblLogo.setForeground(Color.BLUE);
            System.out.println("⚠️ Logo image not found.");
        }
        lblLogo.setBounds(50, 30, 150, 150);
        background.add(lblLogo);

        // 🧾 Heading
        JLabel heading = new JLabel("Miraj Medical - Login");
        heading.setFont(new Font("SansSerif", Font.BOLD, 40));
        heading.setForeground(new Color(0, 102, 204));
        heading.setBounds(250, 70, 600, 50);
        background.add(heading);

        // 👤 Username
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblUsername.setBounds(300, 200, 150, 30);
        background.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 20));
        txtUsername.setBounds(460, 200, 250, 30);
        txtUsername.setHorizontalAlignment(JTextField.CENTER);
        background.add(txtUsername);

        // 🔒 Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblPassword.setBounds(300, 260, 150, 30);
        background.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN,20));
        txtPassword.setBounds(460, 260, 250, 30);
        txtPassword.setHorizontalAlignment(JPasswordField.CENTER);
        background.add(txtPassword);

        // 🔘 Login Button
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnLogin.setBounds(400, 330, 140, 40);
        btnLogin.setBackground(new Color(0, 123, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        background.add(btnLogin);

        // ❌ Exit Button
        JButton btnExit = new JButton("Exit");
        btnExit.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnExit.setBounds(560, 330, 140, 40);
        btnExit.setBackground(new Color(220, 53, 69));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);
        background.add(btnExit);

        // Enter key triggers login
        getRootPane().setDefaultButton(btnLogin);

        // 🧠 Actions
        btnLogin.addActionListener(e -> performLogin());
        btnExit.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password!");
            return;
        }

        try (Connection con = DBConnect.connectDB()) {
            String sql = "SELECT * FROM login WHERE username=? AND password=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "✅ Login Successful!");
                dispose();
                Dashboard dash = new Dashboard(username);
                dash.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid Username or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::new);
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
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

