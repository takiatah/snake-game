import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import java.nio.file.Files;
import java.nio.file.Path;

public class SnakeGame extends JPanel implements ActionListener {

    // size of frame
    static final int WIDTH = 700;
    static final int HEIGHT = 700;
    static final int HEADER_HEIGHT = 25;

    // size of unit squares
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (WIDTH*HEIGHT)/UNIT_SIZE;

    // timer and delay
    Timer timer;
    static final int DELAY = 100;

    // location of each body part of snake
    Random random;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;

    ColorButton colorChosen;

    // state of game
    static final int START = 0;
    static final int RUNNING = 1;
    static final int OVER = 2;
    int state;

    // location and count of food
    int foodEaten;
    int highScore = 0;
    int foodX;
    int foodY;

    // golden apple
    boolean goldApple = false;
    int goldApplesEaten = 0;
    Timer goldTimer;
    Timer spawnGoldTimer;
    int goldX;
    int goldY;

    // direction snake is going
    static final int RIGHT = 0;
    static final int LEFT = 1;
    static final int UP = 2;
    static final int DOWN = 3;
    int direction = RIGHT;

    JButton playAgainButton;
    JButton start;
    ArrayList<ColorButton> colorButtons = new ArrayList<>();;

    public SnakeGame() {
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT+HEADER_HEIGHT));
        this.setBackground((Color.black));
        this.setFocusable(true);
        this.addKeyListener(new SnakeKeyAdapter(this));
        this.addMouseListener(new SnakeMouseAdapter(this));

        timer = new Timer(DELAY, this);
        spawnGoldTimer = new Timer(10000, this);
        loadHighScore();
        initButtons();
        state = START;
    }

    public void initButtons() {
        // start game button
        start = new JButton("PLAY");
        start.setBounds(WIDTH/2 - 150, HEIGHT/2 + 30, 300, 60);
        start.setFont(new Font("Bahnschrift", Font.BOLD, 30));
        start.setBackground(new Color(78, 129, 76));
        start.setForeground(Color.WHITE);
        start.setVisible(true);
        start.addActionListener(this);
        this.add(start);

        // play again button
        this.setLayout(null);
        playAgainButton = new JButton("Play Again");
        playAgainButton.setBounds(WIDTH/2 - 100, HEIGHT/2 + 75, 200, 50);
        playAgainButton.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        playAgainButton.setVisible(false);
        playAgainButton.addActionListener(this);
        this.add(playAgainButton);

        // color buttons
        int midpoint = WIDTH/2 - 40/2;
        colorChosen = new ColorButton(this, "Green", midpoint-200, 300, new Color(26,101,1), new Color(45,180,0));
        colorButtons.add(colorChosen);
        colorButtons.add(new ColorButton(this, "Blue", midpoint-100, 300, new Color(0,76,153), new Color(51,153,255)));
        colorButtons.add(new ColorButton(this, "Purple", midpoint, 300, new Color(75,0,130), new Color(170,85,255)));
        colorButtons.add(new ColorButton(this, "Yellow", midpoint+100, 300, new Color(204,170,0), new Color(255,221,51)));
        colorButtons.add(new ColorButton(this, "Red", midpoint+200, 300, new Color(153,0,0), new Color(255,80,80)));
    }

    public void startGame() {
        state = RUNNING;
        bodyParts = 6;
        foodEaten = 0;
        goldApplesEaten = 0;
        direction = RIGHT;
        goldApple = false;

        Arrays.fill(x, 0);
        Arrays.fill(y, HEADER_HEIGHT);

        makeFood();

        timer.start();
        spawnGoldTimer.start();

        playAgainButton.setVisible(false);

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (state == START) {
            drawStartScreen(g);
        } else {
            drawGameScreen(g);
            if (state == OVER) {
                drawGameOverScreen(g);
            }
        }
    }

    public void drawStartScreen(Graphics g)  {
        // draw start screen
        g.setFont(new Font("Bahnschrift", Font.BOLD, 75));
        g.setColor(Color.red);
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("SNAKE GAME", (WIDTH - metrics.stringWidth("SNAKE GAME"))/2, HEIGHT/3-30);
        g.setFont(new Font("Bahnschrift", Font.BOLD, 30));
        g.setColor(Color.white);
        metrics = getFontMetrics(g.getFont());
        g.drawString("Choose your color:", (WIDTH - metrics.stringWidth("Choose your color:"))/2, HEIGHT/3 + 30);

        for (ColorButton button : colorButtons) {
            g.setColor(button.body);
            g.fillRect(button.x, button.y, button.size, button.size);
        }
        g.setColor(Color.white);
        g.drawRect(colorChosen.x - 10, colorChosen.y - 10, colorChosen.size + 20,colorChosen.size + 20);

        // snake picture
        g.setColor(colorChosen.body);
        g.fillRect(125, 600, 75, 25);
        g.fillRect(200, 525, 25, 100);
        g.fillRect(200, 525, 100, 25);
        g.fillRect(300, 525, 25, 100);
        g.fillRect(300, 600, 100, 25);
        g.fillRect(400, 525, 25, 100);
        g.fillRect(400, 525, 100, 25);
        g.setColor(colorChosen.head);
        g.fillRect(500, 525, 25, 25);

        g.setColor(Color.red);
        g.fillOval(550, 525, 25, 25);

        // draw header
        g.setColor(new Color(55, 64, 59));
        g.fillRect(0, 0, WIDTH, HEADER_HEIGHT);

        // draw high score
        g.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        g.setColor(Color.white);
        g.drawString("High Score: " + highScore, 20, g.getFont().getSize());

    }

    public void drawGameScreen(Graphics g)  {
        // draw food
        g.setColor(Color.red);
        g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

        // draw golden apple
        if (goldApple == true) {
            g.setColor(new Color(204, 188, 77));
            g.fillOval(goldX, goldY, UNIT_SIZE, UNIT_SIZE);
        }

        // draw snake
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g.setColor(colorChosen.head);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(colorChosen.body);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }

        // draw header
        g.setColor(new Color(55, 64, 59));
        g.fillRect(0, 0, WIDTH, HEADER_HEIGHT);

        // draw score
        g.setFont(new Font("Bahnschrift", Font.BOLD, 20));
        g.setColor(Color.white);
        FontMetrics scoreMetrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + foodEaten, 20, g.getFont().getSize());
        g.drawString("Golden Apples: " + goldApplesEaten, WIDTH - scoreMetrics.stringWidth("Golden Apples: " + goldApplesEaten) - 20, g.getFont().getSize());
    }

    public void drawGameOverScreen(Graphics g)  {
        // game over screen
        g.setColor(new Color(55, 64, 59));
        g.fillRect(100, 200, WIDTH-200, HEIGHT-400);
        g.setFont(new Font("Bahnschrift", Font.BOLD, 75));
        g.setColor(Color.red);
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("GAME OVER", (WIDTH - metrics.stringWidth("GAME OVER"))/2, HEIGHT/2-30);

        g.setFont(new Font("Bahnschrift", Font.BOLD, 30));
        FontMetrics scoreMetrics = getFontMetrics(g.getFont());
        g.setColor(Color.white);
        g.drawString("Score: " + foodEaten + "    High Score: " + highScore, (WIDTH - scoreMetrics.stringWidth("Score: " + foodEaten + "    High Score: " + highScore))/2, HEIGHT/2 + 3*getFont().getSize());
    }

    public void makeFood() {
        foodX = random.nextInt(WIDTH/UNIT_SIZE)*UNIT_SIZE;
        foodY = (random.nextInt(HEIGHT/UNIT_SIZE)*UNIT_SIZE) + HEADER_HEIGHT;
    }

    public void makeGoldenApple() {
        // make golden apple, 20% chance
        int r = random.nextInt(100);
        if (goldApple == false && r < 50) {
            goldApple = true;
            goldX = random.nextInt(WIDTH/UNIT_SIZE)*UNIT_SIZE;
            goldY = (random.nextInt(HEIGHT/UNIT_SIZE)*UNIT_SIZE) + HEADER_HEIGHT;
            goldTimer = new Timer(5000, this);
            goldTimer.setRepeats(false);
            goldTimer.start();
        }
    }

    public void move() {
        if (state == RUNNING) {
            // rest of the body parts follow head
            for (int i = bodyParts; i > 0; i--) {
                x[i] = x[i - 1];
                y[i] = y[i - 1];
            }

            // head moves in specified direction
            switch (direction) {
                case RIGHT:
                    x[0] = x[0] + UNIT_SIZE;
                    break;
                case LEFT:
                    x[0] = x[0] - UNIT_SIZE;
                    break;
                case UP:
                    y[0] = y[0] - UNIT_SIZE;
                    break;
                case DOWN:
                    y[0] = y[0] + UNIT_SIZE;
                    break;
            }
        }
    }

    public void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            foodEaten++;
            bodyParts++;
            makeFood();
        }
        if (goldApple == true && ((x[0] == goldX) && (y[0] == goldY))) {
            foodEaten += 5;
            goldApplesEaten += 1;
            // add 5 new body parts
            int tailX = x[bodyParts - 1];
            int tailY = y[bodyParts - 1];

            for (int i = 0; i < 5; i++) {
                x[bodyParts + i] = tailX;
                y[bodyParts + i] = tailY;
            }
            bodyParts += 5;

            goldApple = false;
            goldX = -1;
            goldY = -1;
            if (goldTimer != null) {
                goldTimer.stop();
            }
        }
    }

    public void checkCollisions() {
        // check if head collides with body
        for (int i = 1; i < bodyParts; i++) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                state = OVER;
            }
        }
        // check if head collides with borders
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < HEADER_HEIGHT || y[0] >= HEIGHT + HEADER_HEIGHT) {
            state = OVER;
        }

        if (state == OVER) { // game over
            timer.stop();
            spawnGoldTimer.stop();
            if (goldTimer != null) {
                goldTimer.stop();
            }
            if (foodEaten > highScore) {
                highScore = foodEaten;
                saveHighScore();
            }
            playAgainButton.setVisible(true);
        }
    }


    public void loadHighScore() {
        try {
            Path path = Path.of("highscore.txt");

            if (Files.exists(path)) {
                highScore = Integer.parseInt(Files.readString(path).trim());
            }
        } catch (Exception e) {
            highScore = 0;
        }
    }


    public void saveHighScore() {
        try {
            Files.writeString(Path.of("highscore.txt"), String.valueOf(highScore));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == goldTimer) {
            goldApple = false;
            goldX = -1;
            goldY = -1;
        } else if (e.getSource() == spawnGoldTimer && state == RUNNING) {
            makeGoldenApple();
        } else if (state == RUNNING) {
            move();
            checkFood();
            checkCollisions();
        } else if (e.getSource() == start) {
            start.setVisible(false);
            startGame();
        } else if (e.getSource() == playAgainButton) {
            startGame();
        }
        repaint();
    }
}
