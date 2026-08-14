package Pharmacy;

import javax.swing.*;
import java.awt.*;

public class LogoutPage extends JFrame {

    public LogoutPage() {
        setTitle("Logout - MirajMeds");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Background
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/images/background.jpg")));
        background.setLayout(new GridBagLayout()); // Center panel
        add(background);

        // Semi-transparent panel
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(400, 230));
        panel.setBackground(new Color(255, 255, 255, 200)); // white transparent
        panel.setLayout(null);

        // Logo
        JLabel logo = new JLabel(new ImageIcon(getClass().getResource("/images/logo.png")));
        logo.setBounds(160, 10, 80, 80);
        panel.add(logo);

        // Message
        JLabel msg = new JLabel("You have been logged out successfully!", SwingConstants.CENTER);
        msg.setFont(new Font("Serif", Font.BOLD, 18));
        msg.setForeground(Color.BLUE);
        msg.setBounds(20, 100, 360, 30);
        panel.add(msg);

        // Sub message
        JLabel subMsg = new JLabel("See you again soon!", SwingConstants.CENTER);
        subMsg.setFont(new Font("SansSerif", Font.ITALIC, 14));
        subMsg.setForeground(Color.DARK_GRAY);
        subMsg.setBounds(20, 130, 360, 25);
        panel.add(subMsg);

        // Exit Button
        JButton exitBtn = new JButton("Exit");
        exitBtn.setBounds(140, 170, 120, 30);
        exitBtn.setBackground(new Color(220, 53, 69)); // red
        exitBtn.setForeground(Color.WHITE);
        panel.add(exitBtn);

        // Add panel to background
        background.add(panel, new GridBagConstraints());

        // Exit action
        exitBtn.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        new LogoutPage().setVisible(true);
    }
}
