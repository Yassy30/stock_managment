import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {

    private JTextField tfUsername;
    private JPasswordField tfPassword;
    private JButton btnLogin;

    public LoginForm() {
        // Setup the frame
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window initially

        // Use GridBagLayout for flexible layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER; // Center components

        // Create the title label
        JLabel titleLabel = new JLabel("Welcome Admin");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around the label
        add(titleLabel, gbc);

        // Add the username label and text field
        JLabel usernameLabel = new JLabel("Username:");
        tfUsername = new JTextField(20); // Set preferred column width for resizing
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make text fields expand horizontally
        add(usernameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(tfUsername, gbc);

        // Add the password label and text field
        JLabel passwordLabel = new JLabel("Password:");
        tfPassword = new JPasswordField(20); // Set preferred column width for resizing
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(passwordLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(tfPassword, gbc);

        // Add the login button
        btnLogin = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10); // Padding around the button
        add(btnLogin, gbc);

        // Action listener for login button
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyInfosAndDisplayHome();
            }
        });
    }

    private void verifyInfosAndDisplayHome() {
        String username = tfUsername.getText();
        String password = new String(tfPassword.getPassword());

        // Direct check for username and password
        if ("admin".equals(username) && "password".equals(password)) {
           

    // Open the DashboardForm
            new Dashboard().setVisible(true);

            // Close login frame
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}
