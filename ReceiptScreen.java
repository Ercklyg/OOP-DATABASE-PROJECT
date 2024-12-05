
import java.awt.*;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class ReceiptScreen extends JFrame implements Printable {

    public final String username;
    public String promo;
    public String status;

    public ReceiptScreen(String username, String promo, String status) {
        this.username = username;
        this.promo = promo;
        this.status = status;

    
        setTitle("Receipt");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

       
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        add(panel);

    
        JSeparator separator1 = new JSeparator();
        separator1.setForeground(Color.BLACK);
        separator1.setMaximumSize(new Dimension(380, 5));

       
        JLabel titleLabel1 = new JLabel("SHAPE-UP GYM");
        titleLabel1.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel1.setForeground(Color.BLACK);
        titleLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel2 = new JLabel("AND FITNESS CENTER");
        titleLabel2.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titleLabel2.setForeground(Color.BLACK);
        titleLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

    
        JSeparator separator2 = new JSeparator();
        separator2.setForeground(Color.BLACK);
        separator2.setMaximumSize(new Dimension(380, 5));

        
        panel.add(Box.createVerticalStrut(15)); 

       
        JLabel receiptLabel = new JLabel("RECEIPT");
        receiptLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        receiptLabel.setForeground(Color.BLACK);
        receiptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

     
        JSeparator separator3 = new JSeparator();
        separator3.setForeground(Color.BLACK);
        separator3.setMaximumSize(new Dimension(380, 2));

        
        panel.add(Box.createVerticalStrut(50));

       
        JLabel helloLabel = new JLabel("Hello, " + username + "!");
        helloLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        helloLabel.setForeground(Color.BLACK);
        helloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

      
        panel.add(Box.createVerticalStrut(10));

        JLabel promoLabel = new JLabel("Promo Availed: " + promo);
        promoLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        promoLabel.setForeground(Color.BLACK);
        promoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        panel.add(Box.createVerticalStrut(10));

        JLabel statusLabel = new JLabel("Status: " + status);
        statusLabel.setFont(new Font("Times New ROman", Font.BOLD, 18));
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        panel.add(Box.createVerticalStrut(10));

        
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String receiptNumber = "No. " + (int) (Math.random() * 1000000);  

        JLabel dateLabel = new JLabel("Date: " + date);
        dateLabel.setFont(new Font("Times New Roman", Font.BOLD, 14));
        dateLabel.setForeground(Color.BLACK);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel receiptNoLabel = new JLabel("Receipt " + receiptNumber);
        receiptNoLabel.setFont(new Font("Times New Roman", Font.BOLD, 14));
        receiptNoLabel.setForeground(Color.BLACK);
        receiptNoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        panel.add(Box.createVerticalStrut(10));

       
        JSeparator separator4 = new JSeparator();
        separator4.setForeground(Color.BLACK);
        separator4.setMaximumSize(new Dimension(380, 2));

       
        panel.add(Box.createVerticalStrut(10));

       
        JLabel thankYouLabel = new JLabel("THANK YOU FOR CHOOSING US!");
        thankYouLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        thankYouLabel.setForeground(Color.BLACK);
        thankYouLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        panel.add(separator1); 
        panel.add(titleLabel1);
        panel.add(titleLabel2);
        panel.add(separator2);  
        panel.add(Box.createVerticalStrut(15));
        panel.add(receiptLabel); 
        panel.add(separator3);
        panel.add(Box.createVerticalStrut(10));
        panel.add(helloLabel);
        panel.add(promoLabel);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(10)); 
        panel.add(dateLabel);
        panel.add(receiptNoLabel);
        panel.add(Box.createVerticalStrut(10)); 
        panel.add(separator4);  
        panel.add(Box.createVerticalStrut(10));
        panel.add(thankYouLabel);  
        panel.add(Box.createVerticalStrut(5)); 

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); 
        JButton closeButton = new JButton("CLOSE");
        closeButton.setBackground(Color.WHITE);
        closeButton.setForeground(Color.black);
        closeButton.addActionListener(e -> {
            
            dispose();
             
        });

        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH); 
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex == 0) {
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            paint(g2d);
            return PAGE_EXISTS;
        }
        return NO_SUCH_PAGE;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Sample data for testing
            ReceiptScreen receiptScreen = new ReceiptScreen("JohnDoe", "7 DAYS (220 PESOS)", "Approved");
            receiptScreen.setVisible(true);
        });
    }
}
