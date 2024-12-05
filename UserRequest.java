import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class UserRequest {
    private String username;
    private String promo; // Changed to promo name for clarity
    private String status;
    private LocalDateTime requestTime;

    // Constructor to initialize a UserRequest object with username, promo, and status
    public UserRequest(String username, String promo, String status) {
        this.username = username;
        this.promo = promo;
        this.status = status;
        this.requestTime = LocalDateTime.now(); // Set request time to now
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for promo
    public String getPromo() {
        return promo;
    }

    // Setter for promo
    public void setPromo(String promo) {
        this.promo = promo;
    }

    // Getter for status
    public String getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }

    // Getter for request time
    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    // Setter for request time
    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    // Method to calculate and return the expiration date of the promo based on its duration
    public String getExpirationDate() {
        long durationDays = 0;

        if (promo.contains("1 DAY")) {
            durationDays = 1;
        } else if (promo.contains("7 DAYS")) {
            durationDays = 7;
        } else if (promo.contains("30 DAYS")) {
            durationDays = 30;
        }

        LocalDateTime expirationDate = requestTime.plusDays(durationDays);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return expirationDate.format(formatter);
    }

    // Static method to create a new user request in the database
    public static void createRequest(String username, String promo) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO user_requests (username, promo_id, status, request_time) VALUES (?, ?, ?, NOW())";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                int promoId = getPromoIdByName(promo); // Get promo ID by name
                if (promoId == -1) {
                    System.out.println("Promo ID not found for promo: " + promo);
                    return; // Exit if promo ID is not found
                }
                statement.setInt(2, promoId);
                statement.setString(3, "PENDING"); // Default status
                int rowsInserted = statement.executeUpdate();
                System.out.println("Rows Inserted: " + rowsInserted); // Debug statement
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to save the UserRequest object to the database
    public boolean saveToDatabase() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement userQuery = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
             PreparedStatement promoQuery = connection.prepareStatement("SELECT id FROM promos WHERE promo_name = ?");
             PreparedStatement insertStatement = connection.prepareStatement(
                     "INSERT INTO user_requests (user_id, promo_id, status, request_time) VALUES (?, ?, ?, ?)")) {
    
            // Fetch user ID
            userQuery.setString(1, username);
            ResultSet userResult = userQuery.executeQuery();
            if (!userResult.next()) {
                System.out.println("User  not found.");
                return false;
            }
            int userId = userResult.getInt("id");
    
            // Fetch promo ID
            promoQuery.setString(1, promo);
            ResultSet promoResult = promoQuery.executeQuery();
            if (!promoResult.next()) {
                System.out.println("Promo not found.");
                return false;
            }
            int promoId = promoResult.getInt("id");
    
            // Insert into user_requests table
            insertStatement.setInt(1, userId);
            insertStatement.setInt(2, promoId);
            insertStatement.setString(3, status);
            insertStatement.setTimestamp(4, java.sql.Timestamp.valueOf(requestTime));
            int rowsInserted = insertStatement.executeUpdate();
    
            if (rowsInserted > 0) {
                System.out.println("Request saved to database successfully.");
            } else {
                System.out.println("Failed to save request to database.");
            }
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Static method to retrieve all user requests from the database
    public static List<UserRequest> getAllRequests() {
        List<UserRequest> allRequests = new ArrayList<>();
        String query = "SELECT u.username, p.promo_name, ur.status " +
                       "FROM user_requests ur " +
                       "JOIN users u ON ur.user_id = u.id " +
                       "JOIN promos p ON ur.promo_id = p.id";
    
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
    
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String promo = resultSet.getString("promo_name");
                String status = resultSet.getString("status");
                allRequests.add(new UserRequest(username, promo, status));
                System.out.println("Fetched Request: " + username + ", " + promo + ", " + status); // Debug statement
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        System.out.println("Total Requests Retrieved: " + allRequests.size()); // Debug statement
        return allRequests;
    }

    // Private method to get promo ID by promo name
    private static int getPromoIdByName(String promoName) {
        int promoId = -1; // Default to -1 if not found
        String query = "SELECT id FROM promos WHERE promo_name = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, promoName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                promoId = resultSet.getInt("id");
                System.out.println("Promo ID for " + promoName + ": " + promoId); // Debug statement
            } else {
                System.out.println("Promo not found: " + promoName); // Debug statement
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promoId;
    }

    // Static method to add a pending request for a user
    public static void addPendingRequest(String username, String promo) {
        System.out.println("Adding pending request for user: " + username + " with promo: " + promo);
        UserRequest newRequest = new UserRequest(username, promo, "PENDING");
        if (newRequest.saveToDatabase()) {
            System.out.println("Request added successfully for user: " + username + " with promo: " + promo);
            
            // Debugging: Check all requests in the database
            List<UserRequest> allRequests = getAllRequests();
            System.out.println("All Requests in Database:");
            for (UserRequest request : allRequests) {
                System.out.println("Request: " + request.getUsername() + ", " + request.getPromo() + ", " + request.getStatus());
            }
        } else {
            System.out.println("Failed to add request for user: " + username);
        }
    }
}