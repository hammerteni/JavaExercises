/**
 * Java. Classic Game Snake
 *
 * @author Sergey Iryupin
 * @version 0.3 dated 30 July 2016
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class GameSnake {

    final String nameOfGame = "Classic Game Snake";
    final String gameOverMsg = "GAME OVER";
    final int POINT_RADIUS = 20; // size of one point
    final int FIELD_WIDTH = 30; // in point
    final int FIELD_HEIGHT = 20;
    final int FIELD_DX = 6; // determined experimentally
    final int FIELD_DY = 28;
    final int START_LOCATION = 200;
    final int START_SNAKE_SIZE = 6;
    final int START_SNAKE_X = 10;
    final int START_SNAKE_Y = 10;
    int showDelay = 150;
    final int LEFT = 37; // key codes
    final int UP = 38;
    final int RIGHT = 39;
    final int DOWN = 40;
    final int START_DIRECTION = RIGHT;
    final Color DEFAULT_COLOR = Color.black;
    final Color FOOD_COLOR = Color.green;
    final Color POISON_COLOR = Color.red;
    Snake snake;
    Food food;
    Poison poison;
    Random random = new Random();
    JFrame frame;
    Canvas canvasPanel;
    boolean gameOver = false;

    public static void main(String[] args) {
        new GameSnake().go();
    }

    void go() {
        frame = new JFrame(nameOfGame + " : " + START_SNAKE_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH * POINT_RADIUS + FIELD_DX, FIELD_HEIGHT * POINT_RADIUS + FIELD_DY);
        frame.setLocation(START_LOCATION, START_LOCATION);
        frame.setResizable(false);

        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.white);

        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                snake.setDirection(e.getKeyCode());
            }
        });

        frame.setVisible(true);

        snake = new Snake(START_SNAKE_X, START_SNAKE_Y, START_SNAKE_SIZE, START_DIRECTION);
        food = new Food();
        poison = new Poison();

        // main loop of game
        while (!gameOver) {
            snake.move();
            if (food.isEaten()) {
                food.next();
                poison.add();
            }
            canvasPanel.repaint();
            try {
                Thread.sleep(showDelay);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    class Snake {
        ArrayList<Point> snake = new ArrayList<Point>();
        int direction;

        public Snake(int x, int y, int length, int direction) {
            for (int i = 0; i < length; i++) {
                Point point = new Point(x - i, y);
                snake.add(point);
            }
            this.direction = direction;
        }

        boolean isInsideSnake(int x, int y) {
            for (Point point : snake) {
                if ((point.getX() == x) && (point.getY() == y)) {
                    if ((snake.get(snake.size() - 1).getX() != x) && (snake.get(snake.size() - 1).getY() != y)) { // to exclude snake's tail
                        return true;
                    }
                }
            }
            return false;
        }

        boolean isFood(Point food) {
            return ((snake.get(0).getX() == food.getX()) && (snake.get(0).getY() == food.getY()));
        }

        void move() {
            int x = snake.get(0).getX();
            int y = snake.get(0).getY();
            if (direction == LEFT) { x--; }
            if (direction == RIGHT) { x++; }
            if (direction == UP) { y--; }
            if (direction == DOWN) { y++; }
            if (x > FIELD_WIDTH - 1) { x = 0; }
            if (x < 0) { x = FIELD_WIDTH - 1; }
            if (y > FIELD_HEIGHT - 1) { y = 0; }
            if (y < 0) { y = FIELD_HEIGHT - 1; }
            gameOver = isInsideSnake(x, y) || poison.isPoison(x, y); // check game over
            snake.add(0, new Point(x, y)); // new position for head
            if (isFood(food)) { // check meeting food
                food.eat();
                frame.setTitle(nameOfGame + " : " + snake.size());
            } else {
                snake.remove(snake.size() - 1);
            }
        }

        void setDirection(int direction) {
            if ((direction >= LEFT) && (direction <= DOWN)) { // block wrong codes
                if (Math.abs(this.direction - direction) != 2) { // block moving back
                    this.direction = direction;
                }
            }
        }

        void paint(Graphics g) {
            for (Point point : snake) {
                point.paint(g);
            }
        }
    }

    class Food extends Point {

        public Food() {
            super(-1, -1);
            this.color = FOOD_COLOR;
        }

        void eat() {
            this.setXY(-1, -1);
        }

        boolean isEaten(){
            return this.getX() == -1;
        }

        boolean isFood(int x, int y) {
            return (this.x == x) && (this.y == y);
        }

        void next() {
            int x, y;
            do {
                x = random.nextInt(FIELD_WIDTH);
                y = random.nextInt(FIELD_HEIGHT);
            } while (snake.isInsideSnake(x, y));
            this.setXY(x, y);
        }
    }

    class Poison {
        ArrayList<Point> poison = new ArrayList<Point>();
        Color color = POISON_COLOR;

        boolean isPoison(int x, int y) {
            for (Point point : poison) {
                if ((point.getX() == x) && (point.getY() == y)) {
                    return true;
                }
            }
            return false;
        }

        void add() {
            int x, y;
            do {
                x = random.nextInt(FIELD_WIDTH);
                y = random.nextInt(FIELD_HEIGHT);
            } while (isPoison(x, y) || snake.isInsideSnake(x, y) || food.isFood(x, y));
            poison.add(new Point(x, y, color));
        }

        void paint(Graphics g) {
            for (Point point : poison) {
                point.paint(g);
            }
        }
    }

    class Point {
        int x, y;
        Color color = DEFAULT_COLOR;

        public Point(int x, int y) {
            this.setXY(x, y);
        }

        public Point(int x, int y, Color color) {
            this.setXY(x, y);
            this.color = color;
        }

        void paint(Graphics g) {
            g.setColor(color);
            g.fillOval(getX()*POINT_RADIUS, getY()*POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
        }

        int getX() { return x; }
        int getY() { return y; }
        void setXY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public class Canvas extends JPanel {

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            snake.paint(g);
            food.paint(g);
            poison.paint(g);
            if (gameOver) {
                g.setColor(Color.red);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(gameOverMsg, (FIELD_WIDTH * POINT_RADIUS + FIELD_DX - fm.stringWidth(gameOverMsg))/2, (FIELD_HEIGHT * POINT_RADIUS + FIELD_DY)/2);
            }
        }
    }
}