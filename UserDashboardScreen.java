import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class UserDashboardScreen extends JFrame {

    private JLabel expirationDateLabel; // Label to display the expiration date of the promo
    private Date promoEndTime; // Variable to hold the end time of the promo

    public UserDashboardScreen(String username) {
        // Set the frame title and size
        setTitle("User   Dashboard");
        setSize(1550, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
        setLayout(null);

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 36));
        welcomeLabel.setBounds(530, 100, 500, 60);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(welcomeLabel);

        // Promo Label
        JLabel promoLabel = new JLabel("Promo: ");
        promoLabel.setForeground(Color.WHITE);
        promoLabel.setFont(new Font("Times New Roman", Font.BOLD, 36));
        promoLabel.setBounds(530, 200, 500, 60);
        promoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(promoLabel);

        // Expiration Date Label
        expirationDateLabel = new JLabel("Expiration Date: ");
        expirationDateLabel.setForeground(Color.WHITE);
        expirationDateLabel.setFont(new Font("Times New Roman", Font.BOLD, 36));
        expirationDateLabel.setBounds(330, 300, 900, 60);
        expirationDateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(expirationDateLabel);

        // Fetch user details (promo and promo end time) from the database
        String promo = fetchUserPromo(username);
        promoLabel.setText("Promo: " + promo);

        // Update the expiration date label
        updateExpirationDateLabel(username);

        // Exit Button
        JButton exitButton = new JButton("LOG OUT");
        exitButton.setBounds(530, 700, 500, 40);
        exitButton.setBackground(Color.WHITE);
        exitButton.setFont(new Font("Times New Roman", Font.BOLD, 24));
        exitButton.setForeground(Color.decode("#8B0000"));
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(e -> {
            dispose(); // Close the current window
            new LoginScreen().setVisible(true); // Open the login screen
        });
        add(exitButton);

        // Cancel Promo Button
        JButton cancelPromoButton = new JButton("CANCEL PROMO");
        cancelPromoButton.setBounds(530, 600, 500, 40);
        cancelPromoButton.setBackground(Color.WHITE);
        cancelPromoButton.setFont(new Font("Times New Roman", Font.BOLD, 24));
        cancelPromoButton.setForeground(Color.decode("#8B0000"));
        cancelPromoButton.setFocusPainted(false);
        cancelPromoButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to cancel your promo?",
                    "Cancel Promo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (response == JOptionPane.YES_OPTION) {
                // Update promo status in the database
                if (updatePromoStatus(username, "Cancelled")) {
                    promoEndTime = new Date(); // Effectively cancel the promo
                    expirationDateLabel.setText("Promo Cancelled");
                    JOptionPane.showMessageDialog(this, "Promo cancelled successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update promo status.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(cancelPromoButton);
    }

    // Fetches the user's promo status and end time from the database
    private String fetchUserPromo(String username) {
        String promo = "Not Active"; // Default promo status
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT promo_status, promo_end_time FROM users WHERE username = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                promo = resultSet.getString("promo_status");
                promoEndTime = resultSet.getTimestamp("promo_end_time");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return promo; // Return the promo status
    }

    // Updates the expiration date label based on the current date and promo end time
    private void updateExpirationDateLabel(String username) {
        if (promoEndTime == null) {
            expirationDateLabel.setText("No active promo"); // No promo active
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedExpirationDate = dateFormat.format(promoEndTime);
        Date currentDate = new Date();

        if (currentDate.after(promoEndTime)) {
            expirationDateLabel.setText("Promo expired"); // Promo has expired
            updatePromoStatus(username, "Expired"); // Update promo status in the database
        } else {
            expirationDateLabel.setText("Expiration Date: " + formattedExpirationDate); // Show expiration date
        }
    }

    // Updates the promo status in the database for the given username
    private boolean updatePromoStatus(String username, String status) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET promo_status = ?, promo_end_time = null WHERE username = ?")) {
            statement.setString(1, status);
            statement.setString(2, username);
            int rowsAffected = statement.executeUpdate(); // Execute the update
            return rowsAffected > 0; // Return true if update was successful
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Return false if there was an error
    }

    // Main method to launch the UserDashboardScreen
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UserDashboardScreen("user1").setVisible(true); // Create and show the dashboard for user1
        });
    }
}