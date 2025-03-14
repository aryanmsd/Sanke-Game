import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private final int WIDTH = 600, HEIGHT = 400, GRID_SIZE = 20;
    private final Color GREEN = new Color(34, 139, 34);
    private final Color RED = Color.RED, WHITE = Color.WHITE, BLACK = Color.BLACK;
    private LinkedList<Point> snake;
    private Point food;
    private int directionX = GRID_SIZE, directionY = 0;
    private boolean running = true;
    private Timer timer;
    private int score = 0;
    private JLabel scoreLabel;
    
    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(BLACK);
        setFocusable(true);
        setLayout(new BorderLayout());
        
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(scoreLabel, BorderLayout.NORTH);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP && directionY == 0) {
                    directionX = 0; directionY = -GRID_SIZE;
                } else if (key == KeyEvent.VK_DOWN && directionY == 0) {
                    directionX = 0; directionY = GRID_SIZE;
                } else if (key == KeyEvent.VK_LEFT && directionX == 0) {
                    directionX = -GRID_SIZE; directionY = 0;
                } else if (key == KeyEvent.VK_RIGHT && directionX == 0) {
                    directionX = GRID_SIZE; directionY = 0;
                }
            }
        });
        
        snake = new LinkedList<>();
        snake.add(new Point(100, 100));
        snake.add(new Point(90, 100));
        snake.add(new Point(80, 100));
        
        spawnFood();
        
        timer = new Timer(100, this);
        timer.start();
    }
    
    private void spawnFood() {
        Random rand = new Random();
        int x = rand.nextInt(WIDTH / GRID_SIZE) * GRID_SIZE;
        int y = rand.nextInt(HEIGHT / GRID_SIZE) * GRID_SIZE;
        food = new Point(x, y);
    }
    
    private void moveSnake() {
        Point newHead = new Point(snake.getFirst().x + directionX, snake.getFirst().y + directionY);
        
        if (snake.contains(newHead) || newHead.x < 0 || newHead.x >= WIDTH || newHead.y < 0 || newHead.y >= HEIGHT) {
            running = false;
            timer.stop();
            saveScore();
            JOptionPane.showMessageDialog(this, "Game Over! Your Score: " + score);
            return;
        }
        
        snake.addFirst(newHead);
        
        if (newHead.equals(food)) {
            score++;
            scoreLabel.setText("Score: " + score);
            spawnFood();
        } else {
            snake.removeLast();
        }
    }
    
    private void saveScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scoreboard.txt", true))) {
            writer.write("Score: " + score + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (running) {
            g.setColor(RED);
            g.fillOval(food.x, food.y, GRID_SIZE, GRID_SIZE);
            
            for (int i = 0; i < snake.size(); i++) {
                g.setColor(GREEN);
                g.fillOval(snake.get(i).x, snake.get(i).y, GRID_SIZE, GRID_SIZE);
                
                if (i == 0) { // Draw eyes
                    g.setColor(WHITE);
                    g.fillOval(snake.get(i).x + 5, snake.get(i).y + 5, 3, 3);
                    g.fillOval(snake.get(i).x + 12, snake.get(i).y + 5, 3, 3);
                }
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            moveSnake();
            repaint();
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
