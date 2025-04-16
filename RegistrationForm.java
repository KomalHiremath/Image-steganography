
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class RegistrationForm {

    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("REGISTRATION FORM");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Load the background image
        ImageIcon bgImage = new ImageIcon(RegistrationForm.class.getResource("/image1.jpg")); // Ensure path is correct

        // Check if image is loaded properly
        if (bgImage.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("Error: Image not found or failed to load.");
        }

        JLabel infoPanel = new JLabel(bgImage);
        infoPanel.setLayout(new BorderLayout());

        // Transparent overlay for text
        JPanel textOverlay = new JPanel();
        textOverlay.setLayout(new BorderLayout());
        textOverlay.setBackground(new Color(0, 0, 128)); // Navy blue

        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>"
                + "<h1>IMAGE STEGANOGRAPHY</h1>"
                + "<p>Image steganography is the art of hiding information within an image file."
                + "The hidden message is embedded into the image in such a way that it is not visible to the naked eye,"
                + "but can be extracted using specific algorithms.</p>"
                + "</div></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(Color.WHITE);
        textOverlay.add(infoLabel, BorderLayout.CENTER);

        infoPanel.add(textOverlay, BorderLayout.CENTER);

        // Create the registration panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(10);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(10);

        JButton submitButton = new JButton("Submit");

        // Set the default font
        Font font = new Font("Arial", Font.BOLD, 20);
        nameLabel.setFont(font);
        nameField.setFont(font);
        passwordLabel.setFont(font);
        passwordField.setFont(font);
        submitButton.setFont(font);

        nameLabel.setForeground(Color.blue);
        passwordLabel.setForeground(Color.blue);

        // Set GridBagLayout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(submitButton, gbc);

        // Add action listener to the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String password = new String(passwordField.getPassword());

                // For demonstration purposes, printing the values to the console
                System.out.println("Name: " + name);
                System.out.println("Password: " + password);

                // Attempt to open the index.java file
                try {
                    File file = new File("index.java"); // Change this path to your file location
                    if (file.exists()) {
                        // Compile the index.java file
                        ProcessBuilder compileProcess = new ProcessBuilder("javac", file.getAbsolutePath());
                        compileProcess.inheritIO();
                        Process process = compileProcess.start();
                        process.waitFor();

                        // Run the compiled Java class
                        String fileName = file.getName();
                        String className = fileName.substring(0, fileName.lastIndexOf('.'));
                        ProcessBuilder runProcess = new ProcessBuilder("java", className);
                        runProcess.inheritIO();
                        runProcess.directory(file.getParentFile());
                        runProcess.start();
                    } else {
                        JOptionPane.showMessageDialog(frame, "index.java file not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        // Split pane to divide information and form panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, infoPanel, formPanel);
        splitPane.setDividerLocation(400);
        splitPane.setOpaque(false);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerSize(1);

        // Add the split pane to the frame
        frame.add(splitPane, BorderLayout.CENTER);

        // Set frame visibility
        frame.setVisible(true);

        // Set a nicer look and feel for the application
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }
    }
}
