import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ViewRequestScreen extends JFrame {

    private JPanel requestPanel;
    private List<UserRequest> allRequests;

    public ViewRequestScreen(List<UserRequest> allRequests) {
        this.allRequests = allRequests;

        setTitle("View Requests");
        setSize(1550, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background panel with an image
        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImage = new ImageIcon(getClass().getResource("/ViewrequestBG.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        JLabel titleLabel = new JLabel("All Requests");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel.setBounds(50, 20, 300, 30);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBounds(110, 75, 1300, 600);
        mainPanel.setBackground(Color.black);
        add(mainPanel);

        // Header panel for labels
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(Color.black);
        GridBagConstraints headerGbc = new GridBagConstraints();
        headerGbc.fill = GridBagConstraints.HORIZONTAL;
        headerGbc.insets = new Insets(5, 5, 5, 5);
        headerGbc.weightx = 1.0;

        JLabel usernameHeader = new JLabel("USERNAME", SwingConstants.CENTER);
        usernameHeader.setForeground(Color.WHITE);
        usernameHeader.setFont(new Font("Times New Roman", Font.BOLD, 24));
        headerGbc.gridx = 0;
        headerPanel.add(usernameHeader, headerGbc);

        JLabel promoHeader = new JLabel("PROMO", SwingConstants.CENTER);
        promoHeader.setFont(new Font("Times New Roman", Font.BOLD, 24));
        promoHeader.setForeground(Color.WHITE);
        headerGbc.gridx = 1;
        headerPanel.add(promoHeader, headerGbc);

        JLabel actionHeader = new JLabel("ACCEPT/DENY", SwingConstants.CENTER);
        actionHeader.setFont(new Font("Times New Roman", Font.BOLD, 24));
        actionHeader.setForeground(Color.WHITE);
        headerGbc.gridx = 2;
        headerPanel.add(actionHeader, headerGbc);

        requestPanel = new JPanel(new GridBagLayout());
        requestPanel.setBackground(Color.black);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        populateRequests(gbc);

        // Add the requestPanel to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(requestPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(1300, 600)); // Set preferred size for the scroll pane

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Add the scrollPane to the mainPanel

        JButton closeButton = new JButton("CLOSE");
        closeButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        closeButton.setBounds(600, 700, 300, 50);
        closeButton.setBackground(Color.WHITE);
        closeButton.setForeground(Color.decode("#8B0000"));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });

        add(closeButton);
    }

    private void populateRequests(GridBagConstraints gbc) {
        int row = 0;
        if (allRequests != null && !allRequests.isEmpty()) {
            for (UserRequest request : allRequests) {
                System.out.println("Adding Request to Panel: " + request.getUsername() + ", " + request.getPromo());
                
                // Username
                gbc.gridx = 0;
                gbc.gridy = row;
                gbc.weightx = 0.3;
                JLabel usernameLabel = new JLabel(request.getUsername(), SwingConstants.CENTER);
                usernameLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
                usernameLabel.setForeground(Color.WHITE);
                requestPanel.add(usernameLabel, gbc);
        
                // Promo
                gbc.gridx = 1;
                gbc.gridy = row;
                gbc.weightx = 0.3;
                JLabel promoLabel = new JLabel(request.getPromo(), SwingConstants.CENTER);
                promoLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
                promoLabel.setForeground(Color.WHITE);
                requestPanel.add(promoLabel, gbc);
        
                // Accept/Deny Buttons
                gbc.gridx = 2;
                gbc.gridy = row;
                gbc.weightx = 0.4;
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                buttonPanel.setBackground(Color.black);
    
                JButton acceptButton = new JButton("ACCEPT");
                acceptButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
                acceptButton.setBackground(Color.GREEN);
                acceptButton.setForeground(Color.WHITE);
                acceptButton.setPreferredSize(new Dimension(200, 25));
                acceptButton.setFocusPainted(false);
                acceptButton.addActionListener(e -> acceptRequest(request)); // Accept button handler
                

                JButton denyButton = new JButton("DENY");
                denyButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
                denyButton.setBackground(Color.RED);
                denyButton.setForeground(Color.WHITE);
                denyButton.setPreferredSize(new Dimension(200, 25));
                denyButton.setFocusPainted(false);
                denyButton.addActionListener(e -> denyRequest(request)); // Deny button handler
    
                buttonPanel.add(acceptButton);
                buttonPanel.add(denyButton);
    
                requestPanel.add(buttonPanel, gbc);
                row++;
            }
        } else {
            JLabel noRequestsLabel = new JLabel("No requests available", SwingConstants.CENTER);
            noRequestsLabel.setForeground(Color.WHITE);
            noRequestsLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
            requestPanel.add(noRequestsLabel, gbc);
        }
        requestPanel.revalidate();
        requestPanel.repaint();
    }
    
    private void acceptRequest(UserRequest request) {
        if (allRequests.contains(request)) {
            try (Connection connection = DatabaseConnection.getConnection(); 
                 PreparedStatement statement = connection.prepareStatement(
                    "UPDATE user_requests SET status = 'APPROVED' " +
                    "WHERE username = ? AND promo_id = ?")) {
    
                statement.setString(1, request.getUsername());
                int promoId = getPromoIdByName(request.getPromo());
                statement.setInt(2, promoId); 
    
                int rowsUpdated = statement.executeUpdate();
    
                if (rowsUpdated > 0) {
                    // Remove the request from the list and update its status
                    allRequests.remove(request);
                    request.setStatus("APPROVED");
                    
                    JOptionPane.showMessageDialog(this, "Request from " + request.getUsername() + " has been approved.");
                    refreshPanel();
    
                    // Create a Customer object and add it to CheckCustomerScreen
                    Customer newCustomer = new Customer(request.getUsername(), request.getPromo(), LocalDateTime.now());
                    CheckCustomerScreen.getCustomers().add(newCustomer); // Add to the list of customers
                    
                    // Show the receipt after accepting the request
                    ReceiptScreen receiptScreen = new ReceiptScreen(request.getUsername(), request.getPromo(), "Approved");
                    receiptScreen.setVisible(true);
    
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update request status. No rows affected.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error processing request: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "This request has already been processed.");
        }
    }
    
    private void denyRequest(UserRequest request) {
        if (allRequests.contains(request)) {
            try (Connection connection = DatabaseConnection.getConnection(); 
                 PreparedStatement statement = connection.prepareStatement(
                    "UPDATE user_requests SET status = 'DENIED' " +
                    "WHERE username = ? AND promo_id = ?")) {
    
                statement.setString(1, request.getUsername());
                statement.setInt(2, getPromoIdByName(request.getPromo())); 
                int rowsUpdated = statement.executeUpdate();
    
                if (rowsUpdated > 0) {
                    // Remove the request from the list and update its status
                    allRequests.remove(request);
                    request.setStatus("DENIED");
    
                    JOptionPane.showMessageDialog(this, "Request from " + request.getUsername() + " has been denied.");
                    refreshPanel();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update request status. No rows affected.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error processing request: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "This request has already been processed.");
        }
    }
    
    

    private void refreshPanel() {
        allRequests = UserRequest.getAllRequests(); // Refresh the list from the database
        requestPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        populateRequests(gbc); // Re-populate the panel with the latest requests
        requestPanel.revalidate(); // Ensure the panel is updated
        requestPanel.repaint(); // Refresh the panel to show changes
    }

    private int getPromoIdByName(String promoName) {
        int promoId = -1;
        if (promoName == null || promoName.trim().isEmpty()) {
            System.out.println("Promo name is null or empty.");
            return promoId; // Return -1 if promo name is invalid
        }
    
        String query = "SELECT id FROM promos WHERE promo_name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, promoName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                promoId = resultSet.getInt("id");
            } else {
                System.out.println("No promo found with name: " + promoName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving promo ID: " + e.getMessage());
        }
        return promoId;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<UserRequest> allRequests = UserRequest.getAllRequests(); // Retrieve all requests
            System.out.println("Total Requests: " + allRequests.size()); // Debug statement
            new ViewRequestScreen(allRequests).setVisible(true);
        });
    }
}