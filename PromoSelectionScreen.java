import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.*;

public class PromoSelectionScreen extends JFrame {

    private JButton selectedButton;
    private String username;
    private String selectedPromo = ""; // Stores selected promo

    // Constructor to initialize the promo selection screen with the given username
    public PromoSelectionScreen(String username) {
        this.username = username; // Save the username passed to the constructor
        setTitle("Choose a Promo");
        setSize(1550, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background Panel
        JPanel backgroundPanel = new JPanel() {
            private final Image backgroundImage = new ImageIcon(getClass().getResource("/PromoselectBG.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        // Title Label
        JLabel promoLabel = new JLabel("CHOOSE A PROMO TO AVAIL:");
        promoLabel.setForeground(Color.WHITE);
        promoLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        promoLabel.setBounds(400, 60, 800, 30);
        promoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(promoLabel);

        // Promo Buttons
        JButton oneDayButton = createPromoButton("1 DAY (40 PESOS)", 550, 200, 1);
        JButton sevenDaysButton = createPromoButton("7 DAYS (220 PESOS)", 550, 300, 7);
        JButton thirtyDaysButton = createPromoButton("30 DAYS (950 PESOS)", 550, 400, 30);
        add(oneDayButton);
        add(sevenDaysButton);
        add(thirtyDaysButton);

        // Sign Up Button
        JButton signUpButton = new JButton("SIGN UP");
        signUpButton.setBounds(700, 550, 200, 50);
        signUpButton.setBackground(Color.WHITE);
        signUpButton.setForeground(Color.decode("#8B0000"));
        signUpButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        signUpButton.addActionListener(e -> handleSignUp(signUpButton));
        add(signUpButton);
    }

    // Create promo selection button with specified text, position, and promo ID
    private JButton createPromoButton(String text, int x, int y, int promoId) {
        JButton promoButton = new JButton(text);
        promoButton.setBounds(x, y, 500, 50);
        promoButton.setBackground(Color.WHITE);
        promoButton.setForeground(Color.decode("#8B0000"));
        promoButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        promoButton.addActionListener(e -> {
            // Change the appearance of the previously selected button
            if (selectedButton != null) {
                selectedButton.setBackground(Color.WHITE);
                selectedButton.setForeground(Color.decode("#8B0000"));
            }
            // Highlight the currently selected button
            promoButton.setBackground(Color.decode("#FFFF00"));
            promoButton.setForeground(Color.BLACK);
            selectedButton = promoButton;
            selectedPromo = String.valueOf(promoId); // Store promoId as string
        });
        return promoButton;
    }

    // Handle the sign-up action when the sign-up button is clicked
    private void handleSignUp(JButton signUpButton) {
        if (selectedPromo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a promo!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        signUpButton.setText("Processing...");
        signUpButton.setEnabled(false);
    
        // Create a new user request
        UserRequest.addPendingRequest(username, selectedPromo); // Create the request
        
        // Log all requests for debugging
        List<UserRequest> allRequests = UserRequest.getAllRequests();
        System.out.println("Requests after adding: " + allRequests.size());
        for (UserRequest request : allRequests) {
            System.out.println("Request: " + request.getUsername() + ", " + request.getPromo() + ", " + request.getStatus());
        }
        // Proceed with sign-up logic
        new PromoSignUpTask(username, Integer.parseInt(selectedPromo), signUpButton).execute();
    }

    // Check if the user has an active promo
    private boolean hasActivePromo(String username) {
        String query = "SELECT COUNT(*) FROM user_requests WHERE username = ? AND status = 'ACTIVE'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0; // Return true if there is an active promo
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if there is no active promo
    }

    // Insert a promo request into the database
    private boolean insertPromoRequest(String username, int promoId) {
        if (hasActivePromo(username)) {
            JOptionPane.showMessageDialog(this, "You already have an active promo.", "Error", JOptionPane.WARNING_MESSAGE);
            return false; // Prevent insertion if the user has an active promo
        }

        String checkRequestSQL = "SELECT 1 FROM user_requests WHERE username = ? AND promo_id = ? AND status IN ('PENDING', 'ACTIVE') LIMIT 1";
        String insertRequestSQL = "INSERT INTO user_requests (username, promo_id, status, request_time) VALUES (?, ?, 'PENDING', ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkRequestSQL);
             PreparedStatement insertStatement = connection.prepareStatement(insertRequestSQL)) {

            checkStatement.setString(1, username);
            checkStatement.setInt(2, promoId);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(this, "You already have a pending or active request for this promo.", "Error", JOptionPane.WARNING_MESSAGE);
                return false; // Prevent insertion if there is already a pending or active request
            }

            insertStatement.setString(1, username);
            insertStatement.setInt(2, promoId);
            insertStatement.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            insertStatement.executeUpdate();
            updateUserPromoStatus(username, promoId); // Update user's promo status
            return true; // Return true if the request was successfully inserted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if there was an error
    }

    // Update the user's promo status in the users table
    private void updateUserPromoStatus(String username, int promoId) {
        String updateSQL = "UPDATE users SET promo_status = 'ACTIVE', promo_end_time = ? WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateSQL)) {
            LocalDateTime expirationDate = calculateExpirationDate(promoId);
            statement.setTimestamp(1, java.sql.Timestamp.valueOf(expirationDate));
            statement.setString(2, username);
            statement.executeUpdate(); // Execute the update statement
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Calculate the expiration date based on the promoId
    private LocalDateTime calculateExpirationDate(int promoId) {
        int durationDays = 0;
        switch (promoId) {
            case 1:
                durationDays = 1; // 1 day promo
                break;
            case 7:
                durationDays = 7; // 7 days promo
                break;
            case 30:
                durationDays = 30; // 30 days promo
                break;
        }
        return LocalDateTime.now().plusDays(durationDays); // Return the expiration date
    }
    
    // SwingWorker for handling the promo sign-up process in the background
    private class PromoSignUpTask extends SwingWorker<Boolean, Void> {
        private final String username;
        private final int promoId;
        private final JButton signUpButton;

        // Constructor for the SwingWorker
        PromoSignUpTask(String username, int promoId, JButton signUpButton) {
            this.username = username;
            this.promoId = promoId;
            this.signUpButton = signUpButton;
        }

        @Override
        protected Boolean doInBackground() {
            return insertPromoRequest(username, promoId); // Insert the promo request in the background
        }

        @Override
        protected void done() {
            try {
                boolean success = get(); // Get the result of the background task
                if (success) {
                    JOptionPane.showMessageDialog(null, "Successfully signed up for the promo!");
                    new LoginScreen().setVisible(true); // Redirect to login screen
                    dispose(); // Close the current window
                } else {
                    JOptionPane.showMessageDialog(null , "Sign-up failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An unexpected error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                signUpButton.setText("SIGN UP"); // Reset the button text
                signUpButton.setEnabled(true); // Re-enable the button
            }
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PromoSelectionScreen("exampleUser ").setVisible(true)); // Create and display the promo selection screen
    }
}