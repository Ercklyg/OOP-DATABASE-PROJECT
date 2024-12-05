import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class CheckCustomerScreen extends JFrame {

    private static List<Customer> customers = new ArrayList<>(); // List of customers

    // Returns the list of customers
    public static List<Customer> getCustomers() {
        return customers; // Return the list of customers
    }

    // Constructor to set up the CheckCustomerScreen UI
    public CheckCustomerScreen() {
        setTitle("Check Customers");
        setSize(1550, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.black); // Dark red background

        // Title label for the screen
        JLabel titleLabel = new JLabel("CUSTOMER EXPIRATION DATE:");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 30, 500, 30);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        // Main panel to hold the customer details
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBounds(110, 75, 1300, 600);
        mainPanel.setBackground(Color.black);
        add(mainPanel);

        // Header panel for column labels
        JPanel headerPanel = new JPanel(new GridBagLayout()); // Two columns for username and expiration date
        headerPanel.setBackground(Color.black);

        // Column headers for username and expiration date
        JLabel usernameHeader = new JLabel("USERNAME", SwingConstants.CENTER);
        usernameHeader.setForeground(Color.WHITE);
        usernameHeader.setFont(new Font("Times New Roman", Font.BOLD, 24));
        JLabel expirationDateHeader = new JLabel("EXPIRATION DATE", SwingConstants.CENTER);
        expirationDateHeader.setFont(new Font("Times New Roman", Font.BOLD, 24));
        expirationDateHeader.setForeground(Color.WHITE);

        // Add headers to the header panel
        headerPanel.add(usernameHeader);
        headerPanel.add(expirationDateHeader);

        // Scrollable panel for customer details
        JPanel customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBackground(Color.black);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Load customers from the database
        loadCustomersFromDatabase();

        // Populate customers into the panel
        populateCustomers(customerPanel, gbc);

        // Add the header and customer details to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(customerPanel), BorderLayout.CENTER);

        // Close button to return to the AdminDashboard
        JButton closeButton = new JButton("CLOSE");
        closeButton.setBounds(1200, 700, 200, 50);
        closeButton.setFont(new Font("Times New Roman", Font.BOLD, 24));
        closeButton.setBackground(Color.WHITE);
        closeButton.setForeground(Color.black);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            new AdminDashboard().setVisible(true); // Go back to AdminDashboard
            dispose(); // Close CheckCustomerScreen
        });
        add(closeButton);
    }

    // Load customers from the database and populate the customers list
    private void loadCustomersFromDatabase() {
        try (Connection connection = DatabaseConnection.getConnection(); 
             PreparedStatement statement = connection.prepareStatement("SELECT username, promo, start_time FROM users WHERE is_admin = FALSE"); 
             ResultSet resultSet = statement.executeQuery()) {

            // Iterate through the result set and create Customer objects
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String promo = resultSet.getString("promo");
                LocalDateTime startTime = resultSet.getTimestamp("start_time").toLocalDateTime();
                customers.add(new Customer(username, promo, startTime)); // Add customer to the list
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    // Populate customer details into the panel
    private void populateCustomers(JPanel customerPanel, GridBagConstraints gbc) {
        int row = 0; // Initialize row counter
        for (Customer customer : customers) {
            // Username label
            gbc.gridx = 0; // Set grid position for username
            gbc.gridy = row; // Set row position
            gbc.weightx = 0.5; // 50% width for username
            JLabel usernameLabel = new JLabel(customer.getUsername(), SwingConstants.CENTER);
            usernameLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
            usernameLabel.setForeground(Color.WHITE);
            customerPanel.add(usernameLabel, gbc); // Add username label to the panel

            // Expiration date label
            gbc.gridx = 1; // Set grid position for expiration date
            gbc.gridy = row; // Set row position
            gbc.weightx = 0.5; // 50% width for expiration date
            JLabel expirationDateLabel = new JLabel(customer.getExpirationDate(), SwingConstants.CENTER);
            expirationDateLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
            expirationDateLabel.setForeground(Color.WHITE);
            customerPanel.add(expirationDateLabel, gbc); // Add expiration date label to the panel

            row++; // Move to the next row
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CheckCustomerScreen().setVisible(true); // Create and display the CheckCustomerScreen
        });
    }
}

// Customer class to store details
class Customer {

    private String username; // Customer's username
    private String promo; // Customer's promo type
    private LocalDateTime startTime; // Start time of the promo

    // Constructor to initialize Customer object
    public Customer(String username, String promo, LocalDateTime startTime) {
        this.username = username;
        this.promo = promo;
        this.startTime = startTime;
    }

    // Get the username of the customer
    public String getUsername() {
        return username;
    }

    // Get the expiration date of the customer's promo
    public String getExpirationDate() {
        LocalDateTime expirationTime = startTime.plusDays(getPromoDays()); // Calculate expiration time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Define date format
        return expirationTime.format(formatter); // Return formatted expiration date
    }

    // Determine the number of days based on the promo type
    private int getPromoDays() {
        switch (promo) {
            case "1 DAY (40 PESOS)":
                return 1; // 1 day promo
            case "7 DAYS (220 PESOS)":
                return 7; // 7 days promo
            case "30 DAYS (950 PESOS)":
                return 30; // 30 days promo
            default:
                return 0; // No valid promo
        }
    }

    // Check if the customer promo has expired
    public boolean isExpired(LocalDateTime now) {
        LocalDateTime expirationTime = startTime.plusDays(getPromoDays()); // Calculate expiration time
        return now.isAfter(expirationTime); // Return true if current time is after expiration time
    }
}