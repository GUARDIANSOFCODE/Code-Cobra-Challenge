// SnakeGame.java
// This is the main class to run your Code Cobra Challenge game.

import javax.swing.JFrame; // Required for the main game window

public class SnakeGame {

    public static void main(String[] args) {
        // Create the main window for the game
        JFrame gameFrame = new JFrame("Code Cobra Challenge");

        // Create an instance of our custom GamePanel, which holds all the game logic and drawing
        GamePanel panel = new GamePanel();

        // Add the game panel to the frame
        gameFrame.add(panel);

        // Configure the frame properties
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensures the application exits when the window is closed
        gameFrame.setResizable(false);                             // Prevents the user from resizing the window
        gameFrame.pack();                                          // Sizes the frame to fit the preferred size of its components (our GamePanel)
        gameFrame.setLocationRelativeTo(null);                     // Centers the window on the screen
        gameFrame.setVisible(true);                                // Makes the window visible to the user

        // Crucial: Request focus for the panel so it can receive keyboard input events
        panel.setFocusable(true);
        panel.requestFocusInWindow();
    }
}
