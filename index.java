
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class index {

    private static final String START_MARKER = "##START##";
    private static final String END_MARKER = "##END##";

    // Encode the message into the image
    public static void encodeMessage(File file, String message, int key) {
        try {
            BufferedImage image = ImageIO.read(file);
            String fullMessage = START_MARKER + message + END_MARKER;
            BufferedImage stegoImage = hideMessage(image, fullMessage, key);
            File outputfile = new File("stego_image.png");
            ImageIO.write(stegoImage, "png", outputfile);
            JOptionPane.showMessageDialog(null, "Message encoded");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error encoding message");
        }
    }

    // Decode the message from the image
    public static String decodeMessage(File file, int key) {
        try {
            BufferedImage image = ImageIO.read(file);
            return retrieveMessage(image, key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Hide message in the image
    private static BufferedImage hideMessage(BufferedImage image, String message, int key) {
        int width = image.getWidth();
        int height = image.getHeight();
        int messageIndex = 0;
        int messageLength = message.length();

        for (int i = 0; i < width && messageIndex < messageLength; i++) {
            for (int j = 0; j < height && messageIndex < messageLength; j++) {
                int rgb = image.getRGB(i, j);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                char character = (char) (message.charAt(messageIndex++) ^ key);
                blue = character;

                int newRgb = (red << 16) | (green << 8) | blue;
                image.setRGB(i, j, newRgb);
            }
        }

        return image;
    }

    // Retrieve message from the image
    private static String retrieveMessage(BufferedImage image, int key) {
        StringBuilder message = new StringBuilder();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                int blue = rgb & 0xFF;
                char character = (char) (blue ^ key);
                message.append(character);
            }
        }

        String decodedMessage = message.toString();
        if (decodedMessage.contains(START_MARKER) && decodedMessage.contains(END_MARKER)) {
            return decodedMessage.substring(
                    decodedMessage.indexOf(START_MARKER) + START_MARKER.length(),
                    decodedMessage.indexOf(END_MARKER)
            );
        }

        return null;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Image Steganography");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new CardLayout());
        JPanel encodePanel = new JPanel();
        JPanel decodePanel = new JPanel(new BorderLayout());

        // Encoding UI
        encodePanel.setLayout(new GridLayout(3, 1));
        JLabel titleLabel = new JLabel("IMAGE STEGANOGRAPHY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 40));

        JButton encodeButton = new JButton("ENCODE");
        encodeButton.setFont(new Font("Roboto", Font.BOLD, 30));
        encodeButton.setBackground(Color.LIGHT_GRAY);
        encodeButton.addActionListener(e -> {
            JTextField messageField = new JTextField(20);
            JTextField keyField = new JTextField(10);
            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("Message:"));
            panel.add(messageField);
            panel.add(new JLabel("Security Key:"));
            panel.add(keyField);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(null);
            File file = fileChooser.getSelectedFile();

            int option = JOptionPane.showConfirmDialog(frame, panel, "Enter the message and key to encode:", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String message = messageField.getText();
                try {
                    int key = Integer.parseInt(keyField.getText());
                    encodeMessage(file, message, key);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid integer key.", "Invalid Key", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton switchToDecodeButton = new JButton("GO TO DECODE");
        switchToDecodeButton.setFont(new Font("Roboto", Font.BOLD, 30));
        switchToDecodeButton.setBackground(Color.GRAY);
        switchToDecodeButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) (mainPanel.getLayout());
            cl.show(mainPanel, "decodePanel");
        });

        encodePanel.add(titleLabel);
        encodePanel.add(encodeButton);
        encodePanel.add(switchToDecodeButton);

        // Decoding UI
        JPanel decodeControlPanel = new JPanel();
        decodeControlPanel.setLayout(new FlowLayout());

        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(null);
            File file = fileChooser.getSelectedFile();
            String keyStr = JOptionPane.showInputDialog(frame, "Enter the security key to view the image:");
            try {
                int key = Integer.parseInt(keyStr);
                BufferedImage image = ImageIO.read(file);
                String message = decodeMessage(file, key);
                if (message != null) {
                    decodePanel.removeAll();
                    decodePanel.revalidate();
                    decodePanel.repaint();
                    JLabel imageLabel = new JLabel(new ImageIcon(image));
                    JScrollPane imageScrollPane = new JScrollPane(imageLabel);
                    decodePanel.add(imageScrollPane, BorderLayout.CENTER);
                    JTextArea decodedMessageArea = new JTextArea(message);
                    decodedMessageArea.setEditable(false);
                    JScrollPane decodedMessageScrollPane = new JScrollPane(decodedMessageArea);
                    decodePanel.add(decodedMessageScrollPane, BorderLayout.SOUTH);
                    frame.revalidate();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid security key or message not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            decodePanel.removeAll();
            decodePanel.revalidate();
            decodePanel.repaint();
        });

        decodeControlPanel.add(openButton);
        decodeControlPanel.add(resetButton);

        decodePanel.add(decodeControlPanel, BorderLayout.NORTH);

        mainPanel.add(encodePanel, "encodePanel");
        mainPanel.add(decodePanel, "decodePanel");

        frame.add(mainPanel);
        frame.setVisible(true);

        // Set a nicer look and feel for the application
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}
