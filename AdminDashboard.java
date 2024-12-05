import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class AdminDashboard extends JFrame {

    // Constructor for the AdminDashboard class
    public AdminDashboard() {
        setTitle("Admin Dashboard"); // Set the title of the window
        setSize(1550, 850); // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit the application when the window is closed
        setLocationRelativeTo(null); // Center the window on the screen
        setLayout(null); // Use no layout manager

        // Create a background panel with an image
        JPanel backgroundPanel = new JPanel() {
            private final Image backgroundImage = new ImageIcon(getClass().getResource("/AdmindashboardBG.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw the background image
            }
        };
        backgroundPanel.setLayout(null); // Use no layout manager for the background panel
        setContentPane(backgroundPanel); // Set the background panel as the content pane

        // Create and configure the title label
        JLabel titleLabel = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE); // Set text color to white
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 36)); // Set font style and size
        titleLabel.setBounds(530, 100, 500, 50); // Set position and size of the label
        add(titleLabel); // Add the title label to the frame

        // Populate pending requests from the database
        List<UserRequest> pendingRequests = loadPendingRequests(); // Load pending requests

        // Create and configure the "View Requests" button
        JButton viewRequestsButton = new JButton("VIEW REQUESTS");
        viewRequestsButton.setFont(new Font("Times New Roman", Font.BOLD, 24)); // Set font style and size
        viewRequestsButton.setBounds(530, 200, 500, 40); // Set position and size of the button
        viewRequestsButton.setBackground(Color.WHITE); // Set background color
        viewRequestsButton.setForeground(Color.black); // Set text color
        viewRequestsButton.setFocusPainted(false); // Disable focus painting
        viewRequestsButton.addActionListener((ActionEvent e) -> {
            new ViewRequestScreen(pendingRequests).setVisible(true); // Open the ViewRequestScreen with pending requests
            dispose(); // Close the AdminDashboard
        });
        add(viewRequestsButton); // Add the button to the frame

        // Create and configure the "Check Customers" button
        JButton checkCustomersButton = new JButton("CHECK CUSTOMERS");
        checkCustomersButton.setFont(new Font("Times New Roman", Font.BOLD, 24)); // Set font style and size
        checkCustomersButton.setBounds(530, 300, 500, 40); // Set position and size of the button
        checkCustomersButton.setBackground(Color.WHITE); // Set background color
        checkCustomersButton.setForeground(Color.black); // Set text color
        checkCustomersButton.setFocusPainted(false); // Disable focus painting
        checkCustomersButton.addActionListener((ActionEvent e) -> {
            // Open the CheckCustomerScreen
            new CheckCustomerScreen().setVisible(true); // Show the CheckCustomerScreen
        });
        add(checkCustomersButton); // Add the button to the frame

        // Create and configure the "Log Out" button
        JButton logoutButton = new JButton("LOG OUT");
        logoutButton.setFont(new Font("Times New Roman", Font.BOLD, 24)); // Set font style and size
        logoutButton.setBounds(530, 400, 500, 40); // Set position and size of the button
        logoutButton.setBackground(Color.WHITE); // Set background color
        logoutButton.setForeground(Color.black); // Set text color
        logoutButton.setFocusPainted(false); // Disable focus painting
        logoutButton.addActionListener((ActionEvent e) -> {
            // Close the AdminDashboard and open the LoginScreen
            new LoginScreen().setVisible(true); // Show the LoginScreen
            dispose(); // Close AdminDashboard
        });
        add(logoutButton); // Add the button to the frame

    }

    // Method to load pending requests from the database
    private List<UserRequest> loadPendingRequests() {
        List<UserRequest> pendingRequests = new ArrayList<>(); // Initialize the list to hold pending requests
        String query = "SELECT u.username, p.promo_name, ur.status " +
                       "FROM user_requests ur " +
                       "JOIN users u ON ur.user_id = u.id " +
                       "JOIN promos p ON ur.promo_id = p.id " +
                       "WHERE ur.status = 'PENDING'"; // SQL query to fetch pending requests
        try (Connection connection = DatabaseConnection.getConnection(); // Establish database connection
             PreparedStatement statement = connection.prepareStatement(query); // Prepare the SQL statement
             ResultSet resultSet = statement.executeQuery()) { // Execute the query and get results
    
            while (resultSet.next()) { // Iterate through the result set
                String username = resultSet.getString("username"); // Get username
                String promo = resultSet.getString("promo_name"); // Get promo name
                String status = resultSet.getString("status"); // Get request status
                pendingRequests.add(new UserRequest(username, promo, status)); // Add the request to the list
                System.out.println("Loaded Pending Request: " + username + ", " + promo + ", " + status); // Debug statement
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print stack trace in case of SQL exception
        }
        return pendingRequests; // Return the list of pending requests
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true)); // Create and show the AdminDashboard
    }
}