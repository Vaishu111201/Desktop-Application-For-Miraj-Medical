package Pharmacy;

import java.awt.*;
import javax.swing.*;

public class Splash {
    public static void main(String[] args) {
        SplashFrame splash = new SplashFrame("Miraj Medical System");
        splash.setVisible(true);

        int width = 1;
        int height = 1;
        int x = 1;

        // Animation effect
        for (int i = 2; i <= 600; i += 4, x += 1) {
            splash.setLocation((650 - ((i + x) / 2)), 400 - (i / 2));
            splash.setSize(i + x, i);
            try {
                Thread.sleep(10);
            } catch (Exception ignored) {}
        }
    }
}

class SplashFrame extends JFrame implements Runnable {
    Thread t1;

    SplashFrame(String title) {
        super(title);
        setLayout(new FlowLayout());
        setUndecorated(true); // removes title bar

        // 🔹 Show background image
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/splash.png"));
            Image img = icon.getImage().getScaledInstance(730, 550, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(img));
            add(label);
        } catch (Exception e) {
            System.out.println("⚠️ Splash image not found!");
        }

        // 🔹 App icon
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                    getClass().getResource("/images/logo.png")
            ));
        } catch (Exception e) {
            System.out.println("⚠️ Logo not found!");
        }

        t1 = new Thread(this);
        t1.start();
    }

    // 🔹 After 3 seconds, go to LoginForm
    @Override
    public void run() {
        try {
            Thread.sleep(3000); // 3 seconds
            this.dispose(); // close splash
            new LoginForm().setVisible(true); // open login page
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

