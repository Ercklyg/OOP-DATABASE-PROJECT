import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// RegisterScreen.java

public class RegisterScreen extends JFrame {

    private JPasswordField passwordField;
    private JPasswordField rePasswordField;
    private JTextField usernameField;
    private JButton nextButton;
    private static HashMap<String, String> userCredentials = new HashMap<>(); // Store username-password pairs

    // Constructor to set up the registration screen
    public RegisterScreen() {
        setTitle("Register");
        setSize(1550, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load the background image
        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImage = new ImageIcon(getClass().getResource("/Registerbackground.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        // Username Label and Text Field
        JLabel userLabel = new JLabel("USERNAME");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        userLabel.setBounds(700, 150, 200, 50);
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        usernameField.setBounds(550, 200, 500, 50);
        backgroundPanel.add(usernameField);

        // Password Label and Text Field
        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        passLabel.setBounds(700, 250, 200, 50);
        passLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        passwordField.setBounds(550, 300, 500, 50);
        backgroundPanel.add(passwordField);

        // Re-Enter Password Label and Text Field
        JLabel rePassLabel = new JLabel("RE-ENTER PASSWORD");
        rePassLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        rePassLabel.setForeground(Color.WHITE);
        rePassLabel.setBounds(550, 350, 500, 50);
        rePassLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(rePassLabel);

        rePasswordField = new JPasswordField();
        rePasswordField.setFont(new Font("Times New Roman", Font.BOLD, 24));
        rePasswordField.setBounds(550, 400, 500, 50);
        backgroundPanel.add(rePasswordField);

        JSeparator separator = new JSeparator();
        separator.setBounds(500, 500, 600, 40);
        separator.setForeground(Color.BLACK);
        backgroundPanel.add(separator);

        // Next Button
        nextButton = new JButton("NEXT ");
        nextButton.setBounds(650, 550, 300, 50);
        nextButton.setBackground(Color.WHITE);
        nextButton.setFont(new Font("Times New Roman", Font.BOLD, 24));
        nextButton.setForeground(Color.BLACK);
        nextButton.setFocusPainted(false);
        nextButton.setEnabled(false);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if passwords match and username is unique before proceeding
                if (checkPasswordMatch()) {
                    String username = usernameField.getText();
                    if (isUsernameUnique(username)) {
                        userCredentials.put(username, new String(passwordField.getPassword()));
                        if (insertUserCredentials(username, new String(passwordField.getPassword()))) {
                            // After successful registration, go to the promo selection screen
                            new PromoSelectionScreen(username).setVisible(true); 
                            dispose(); // Close the RegisterScreen
                        } else {
                            JOptionPane.showMessageDialog(null, "Username already exists!","Error", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Username already exists!", "Error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Passwords do not match!", "Error",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        backgroundPanel.add(nextButton);

        // Add DocumentListener to enable/disable the NEXT button
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                checkFields(); // Check fields when text is inserted
            }

            public void removeUpdate(DocumentEvent e) {
                checkFields(); // Check fields when text is removed
            }

            public void changedUpdate(DocumentEvent e) {
                checkFields(); // Check fields when text changes
            }
        };

        usernameField.getDocument().addDocumentListener(docListener);
        passwordField.getDocument().addDocumentListener(docListener);
        rePasswordField.getDocument().addDocumentListener(docListener);
    }

    // Checks if the username and password fields are filled to enable the NEXT button
    private void checkFields() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        nextButton.setEnabled(!username.isEmpty() && !password.isEmpty());
    }

    // Checks if the password and re-entered password match
    private boolean checkPasswordMatch() {
        String password = new String(passwordField.getPassword());
        String rePassword = new String(rePasswordField.getPassword());
        return password.equals(rePassword);
    }

    // Checks if the username is unique (not already taken)
    private boolean isUsernameUnique(String username) {
        return !userCredentials.containsKey(username);
    }

    // Inserts the user credentials into the database
    private boolean insertUserCredentials(String username, String password) {
        try (Connection connection = DatabaseConnection.getConnection(); 
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, password);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Return true if the insert was successful
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
        return false; // Return false if an error occurs
    }

    // Returns the stored user credentials
    public static HashMap<String, String> getUserCredentials() {
        return userCredentials;
    }

    // Main method to run the registration screen
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegisterScreen registerScreen = new RegisterScreen();
            registerScreen.setVisible(true);
        });
    }
}