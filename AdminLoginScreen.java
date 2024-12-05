import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class AdminLoginScreen extends JFrame {
    // Constructor to set up the admin login screen
    public AdminLoginScreen() {
        setTitle("Log-in as Admin"); // Set the title of the window
        setSize(1550, 850); // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit the application when the window is closed
        setLocationRelativeTo(null); // Center the window on the screen
        setLayout(null); // Use no layout manager

        // Create a panel with a background image
        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImage = new ImageIcon(getClass().getResource("/AdminloginBG.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw the background image
            }
        };
        backgroundPanel.setLayout(null); // Use no layout manager for the panel
        setContentPane(backgroundPanel); // Set the panel as the content pane of the frame

        // Create and configure the title label
        JLabel titleLabel = new JLabel("LOG-IN AS ADMIN");
        titleLabel.setForeground(Color.WHITE); // Set text color to white
        titleLabel.setBounds(530, 100, 500, 50); // Set position and size
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 36)); // Set font style and size
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text
        add(titleLabel); // Add the label to the panel

        // Username Field
        JTextField usernameField = new JTextField(); // Create a text field for username
        usernameField.setFont(new Font("Times New Roman", Font.PLAIN, 24)); // Set font style and size
        usernameField.setBounds(530, 200, 500, 40); // Set position and size
        add(usernameField); // Add the text field to the panel

        // Password Field
        JPasswordField passwordField = new JPasswordField(); // Create a password field for password input
        passwordField.setBounds(530, 300, 500, 40); // Set position and size
        passwordField.setFont(new Font("Times New Roman", Font.PLAIN, 24)); // Set font style and size
        add(passwordField); // Add the password field to the panel

        // Login Button
        JButton loginButton = new JButton("LOG-IN"); // Create a button for login
        loginButton.setBounds(630, 400, 300, 50); // Set position and size
        loginButton.setFont(new Font("Times New Roman", Font.PLAIN, 24)); // Set font style and size
        loginButton.setBackground(Color.WHITE); // Set background color
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText(); // Get the username from the text field
                String password = new String(passwordField.getPassword()); // Get the password from the password field

                // Admin login logic with database authentication
                if (authenticateAdmin(username, password)) { // Check if the credentials are valid
                    JOptionPane.showMessageDialog(null, "Login successful!"); // Show success message
                    new AdminDashboard().setVisible(true); // Redirect to Admin Dashboard
                    dispose(); // Close the admin login screen
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid admin credentials"); // Show error message
                }
            }
        });
        add(loginButton); // Add the login button to the panel

        // Back Button positioned in the upper left corner
        JButton backButton = new JButton("BACK"); // Create a button for going back
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 24)); // Set font style and size
        backButton.setBounds(20, 20, 200, 50); // Set position and size
        backButton.setBackground(Color.WHITE); // Set background color
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginScreen().setVisible(true); // Redirect to the login screen
                dispose(); // Close the admin login screen
            }
        });
        add(backButton); // Add the back button to the panel
    }

    // Method to authenticate admin credentials against the database
    private boolean authenticateAdmin(String username, String password) {
        boolean isAuthenticated = false; // Initialize authentication status
        String query = "SELECT * FROM admins WHERE username = ? AND password = ?"; // SQL query to check credentials

        try (Connection connection = DatabaseConnection.getConnection(); // Get database connection
             PreparedStatement statement = connection.prepareStatement(query)) { // Prepare the SQL statement
            statement.setString(1, username); // Set the username parameter
            statement.setString(2, password); // Set the password parameter (should be hashed in production)
            ResultSet resultSet = statement.executeQuery(); // Execute the query

            if (resultSet.next()) { // If a result is found
                isAuthenticated = true; // Set authentication status to true
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print any exceptions that occur
        }

        return isAuthenticated; // Return the authentication status
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminLoginScreen adminLoginScreen = new AdminLoginScreen(); // Create an instance of the login screen
            adminLoginScreen.setVisible(true); // Make the login screen visible
        });
    }
}