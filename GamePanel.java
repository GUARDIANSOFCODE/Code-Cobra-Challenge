// GamePanel.java
// This class manages all game logic, drawing, and user input for the Code Cobra Challenge.

import javax.swing.JPanel;
import javax.swing.Timer; // Used to control the game loop (updates at regular intervals)
import java.awt.Graphics; // Provides methods for drawing on the panel
import java.awt.Color;    // Defines colors for drawing elements
import java.awt.Dimension; // Used to set the preferred size of the panel
import java.awt.Font;     // For setting text font properties
import java.awt.FontMetrics; // For measuring text dimensions to center it

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter; // A convenience class for handling keyboard events
import java.awt.event.KeyEvent;   // Represents a keyboard event

import java.util.Random;    // Used for generating random positions for food

public class GamePanel extends JPanel implements ActionListener {

    // --- Game Configuration Constants ---
    static final int SCREEN_WIDTH = 600;  // Width of the game screen in pixels
    static final int SCREEN_HEIGHT = 600; // Height of the game screen in pixels
    static final int UNIT_SIZE = 25;      // Size of each 'unit' (snake segment, food item) in pixels
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE; // Total number of possible units on the screen
    static final int INITIAL_DELAY = 100; // Initial delay in milliseconds between game updates (controls speed)

    // --- Snake State Variables ---
    // x[] and y[] store the coordinates for each segment of the snake's body
    // x[0], y[0] will always be the head
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6; // Initial number of body segments for the snake
    char direction = 'R'; // Current direction of the snake: 'U', 'D', 'L', 'R' (Up, Down, Left, Right)

    // --- Food State Variables ---
    int foodX; // X coordinate of the current food item
    int foodY; // Y coordinate of the current food item

    // --- Game State Variables ---
    int score = 0;         // Player's current score
    boolean running = false; // True if the game is active, false if game over or not started
    Timer gameTimer;       // The Swing Timer that drives the game loop
    Random randomGenerator; // Utility for random number generation

    // --- Constructor ---
    public GamePanel() {
        randomGenerator = new Random(); // Initialize the random number generator
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT)); // Set preferred size for the panel
        this.setBackground(Color.BLACK); // Set the background color of the panel
        this.setFocusable(true);         // Allow this panel to receive keyboard focus
        this.addKeyListener(new MyKeyAdapter()); // Register our custom key listener

        startGame(); // Call the method to set up and start the game
    }

    // --- Game Initialization Method ---
    public void startGame() {
        // Reset game state for a fresh start or restart
        bodyParts = 6;
        score = 0;
        direction = 'R'; // Start moving right

        // Clear snake positions for a new game
        // (Important if restarting, as old positions might remain)
        for (int i = 0; i < GAME_UNITS; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        placeNewFood(); // Place the first food item on the board
        running = true; // Set game state to running

        // Initialize and start the game timer
        // The 'this' refers to the GamePanel itself, which implements ActionListener
        gameTimer = new Timer(INITIAL_DELAY, this);
        gameTimer.start();
    }

    // --- Overrides JPanel's paintComponent method for custom drawing ---
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call superclass method to ensure proper painting (clears previous drawings)
        drawGameElements(g);     // Call our custom drawing method
    }

    // --- Custom Drawing Logic for Game Elements ---
    public void drawGameElements(Graphics g) {
        if (running) {
            // Optional: Draw a grid for better visualization (helps in debugging/understanding)
            g.setColor(new Color(30, 30, 30)); // Darker grey for grid lines
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT); // Vertical lines
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE); // Horizontal lines
            }

            // Draw the Food item
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE); // Draw food as a filled circle

            // Draw the Snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { // Head of the snake
                    g.setColor(Color.GREEN); // Distinct color for the head
                } else { // Body segments of the snake
                    g.setColor(new Color(45, 180, 0)); // Slightly darker green for the body
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE); // Draw each segment as a filled rectangle
            }

            // Draw the Score display
            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.BOLD, 30)); // Set font for the score
            FontMetrics metrics = getFontMetrics(g.getFont()); // Get font metrics to measure text width
            // Center the score text at the top
            g.drawString("Score: " + score, (SCREEN_WIDTH - metrics.stringWidth("Score: " + score)) / 2, g.getFont().getSize() + 10);

        } else {
            // If the game is not running (game over), draw the game over screen
            drawGameOverScreen(g);
        }
    }

    // --- Places a new food item at a random empty location ---
    public void placeNewFood() {
        // Generate random coordinates, ensuring they align with the UNIT_SIZE grid
        foodX = randomGenerator.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        foodY = randomGenerator.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

        // Optional: Ensure food doesn't spawn on the snake's body
        // For simplicity, this basic example doesn't check for that, but it's a good enhancement.
    }

    // --- Updates the snake's position based on its current direction ---
    public void moveSnake() {
        // Shift all body parts: each part takes the position of the part in front of it
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        // Move the head (x[0], y[0]) according to the current direction
        switch (direction) {
            case 'U': // Up
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D': // Down
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L': // Left
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R': // Right
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    // --- Checks if the snake's head has eaten the food ---
    public void checkFoodCollision() {
        if ((x[0] == foodX) && (y[0] == foodY)) { // If head coordinates match food coordinates
            bodyParts++;   // Increase snake's length
            score++;       // Increment score
            placeNewFood(); // Generate a new food item
        }
    }

    // --- Checks for game-ending collisions (walls or self-collision) ---
    public void checkGameCollisions() {
        // Check if head collides with any part of its own body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false; // Game over
                break; // No need to check further collisions
            }
        }
        // Check if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        // Check if head touches right border
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }
        // Check if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // Check if head touches bottom border
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) { // If game over, stop the timer
            gameTimer.stop();
        }
    }

    // --- Draws the "Game Over" screen and final score ---
    public void drawGameOverScreen(Graphics g) {
        // Set font and color for Game Over text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        String gameOverText = "GAME OVER";
        // Center the "GAME OVER" text on the screen
        g.drawString(gameOverText, (SCREEN_WIDTH - metrics1.stringWidth(gameOverText)) / 2, SCREEN_HEIGHT / 2);

        // Set font and color for final score text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String finalScoreText = "Score: " + score;
        // Position final score below "GAME OVER"
        g.drawString(finalScoreText, (SCREEN_WIDTH - metrics2.stringWidth(finalScoreText)) / 2, SCREEN_HEIGHT / 2 + 50);

        // Instructions to restart
        g.setColor(Color.CYAN);
        g.setFont(new Font("Ink Free", Font.PLAIN, 25));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        String restartPrompt = "Press 'R' to Restart";
        g.drawString(restartPrompt, (SCREEN_WIDTH - metrics3.stringWidth(restartPrompt)) / 2, SCREEN_HEIGHT / 2 + 100);
    }

    // --- ActionListener implementation: This method is called by the Timer ---
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            moveSnake();        // Update snake's position
            checkFoodCollision(); // Check if snake ate food
            checkGameCollisions(); // Check for collisions that end the game
        }
        repaint(); // Request Swing to redraw the panel (which calls paintComponent)
    }

    // --- Inner class for handling keyboard input ---
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    // Prevent snake from reversing directly into itself
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_R: // 'R' key to restart the game
                    if (!running) { // Only restart if the game is currently over
                        startGame(); // Call startGame to reset and begin a new round
                    }
                    break;
            }
        }
    }
}
