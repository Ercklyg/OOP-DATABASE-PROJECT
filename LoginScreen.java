import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signUpButton;
    private JButton adminButton;

    public LoginScreen() {
        // Set the frame title and size
        setTitle("Gym Management System");
        setSize(1550, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Create a custom JPanel for the background image
        JPanel backgroundPanel = new JPanel() {
            private final Image backgroundImage = new ImageIcon(getClass().getResource("/LoginBG.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        // Title Label
        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'>SHAPE-UP GYM AND<br>FITNESS MANAGEMENT</div></html>");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 48));
        titleLabel.setBounds(0, 50, 1550, 100);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(titleLabel);

        // Username Label and Field
        JLabel userLabel = new JLabel("USERNAME");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        userLabel.setBounds(710, 200, 150, 30);
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        usernameField.setBounds(550, 240, 450, 40);
        backgroundPanel.add(usernameField);

        // Password Label and Field
        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        passLabel.setBounds(710, 300, 150, 30);
        passLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        passwordField.setBounds(550, 340, 450, 40);
        backgroundPanel.add(passwordField);

        // Login Button
        JButton loginButton = new JButton("LOG IN");
        loginButton.setFont(new Font("Times New Roman", Font.BOLD, 24));
        loginButton.setBounds(700, 450, 170, 60);
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        // Add action listener to handle login when button is clicked
        loginButton.addActionListener(e -> handleLogin());
        backgroundPanel.add(loginButton);

        // Separator
        JSeparator separator = new JSeparator();
        separator.setBounds(550, 525, 450, 2);
        separator.setForeground(Color.BLACK);
        backgroundPanel.add(separator);

        // Sign Up Button
        signUpButton = new JButton("SIGN UP");
        signUpButton.setFont(new Font("Times New Roman", Font.BOLD, 24));
        signUpButton.setBounds(700, 550, 170, 60);
        signUpButton.setBackground(Color.WHITE);
        signUpButton.setForeground(Color.BLACK);
        signUpButton.setFocusPainted(false);
        // Add action listener to handle sign up when button is clicked
        signUpButton.addActionListener(e -> handleSignUp());
        backgroundPanel.add(signUpButton);

        // Admin Button
        adminButton = new JButton("ADMIN");
        adminButton.setFont(new Font("Times New Roman", Font.BOLD, 24));
        adminButton.setBounds(200, 650, 170, 75);
        adminButton.setBackground(Color.WHITE);
        adminButton.setForeground(Color.BLACK);
        adminButton.setFocusPainted(false);
        // Add action listener to handle admin login when button is clicked
        adminButton.addActionListener(e -> handleAdminLogin());
        backgroundPanel.add(adminButton);
    }

    // Handle Login Action
    private void handleLogin() {
        // Retrieve username and password from input fields
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Authenticate user credentials
        if (authenticateUser (username, password)) {
            // If authentication is successful, open the user dashboard and close the login screen
            SwingUtilities.invokeLater(() -> {
                new UserDashboardScreen(username).setVisible(true);
                dispose();
            });
        } else {
            // Show an error message if authentication fails
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Handle Sign Up Action
    private void handleSignUp() {
        // Open the registration screen and close the login screen
        SwingUtilities.invokeLater(() -> {
            new RegisterScreen().setVisible(true);
            dispose();
        });
    }

    // Handle Admin Login Action
    private void handleAdminLogin() {
        // Open the admin login screen and close the login screen
        SwingUtilities.invokeLater(() -> {
            new AdminLoginScreen().setVisible(true);
            dispose();
        });
    }

    // Authenticate User
    private boolean authenticateUser (String username, String password) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            // Set the username parameter in the SQL query
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            // Check if the user exists and compare the stored password with the provided password
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                return storedPassword.equals(password);
            }
        } catch (Exception e) {
            // Show an error message if there is an issue during authentication
            JOptionPane.showMessageDialog(this, "Error during authentication: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false; // Return false if authentication fails
    }

    public static void main(String[] args) {
        // Launch the login screen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}