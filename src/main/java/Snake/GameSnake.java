package Snake;
/**
 * Classic Game Snake
 * Written on 04.05.2023
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class GameSnake {
    final String TITLE_OF_PROGRAM = "Classic Game Snake";
    final String GAME_OVER_MSG = "GAME OVER";
    final int POINT_RADIUS = 20;//in px
    final int FIELD_HEIGHT = 20;//in points
    final int FIELD_WIDTH = 30;
    final int FIELD_DX = 6;
    final int FIELD_DY = 28;
    final int START_LOCATION = 200;
    final int START_SNAKE_SIZE = 6;
    final int START_SNAKE_X = 10;
    final int START_SNAKE_Y = 10;
    final int SHOW_DELAY = 150;
    final int LEFT = 37;
    final int UP = 38;
    final int RIGHT = 39;
    final int DOWN = 40;
    final int START_DIRECTION = RIGHT;
    final Color DEFAULT_COLOR = Color.green;
    final Color FOOD_COLOR = Color.orange;
    final Color POISON_COLOR = Color.red;
    Snake snake;
    Food food;
    Poison poison;
    JFrame frame;
    Canvas canvasPanel;
    Random random = new Random();
    Boolean gameOver = false;


    public static void main(String[] args) {
        new GameSnake().go();
    }

    void go() {
        frame = new JFrame(TITLE_OF_PROGRAM + " : " + START_SNAKE_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH * POINT_RADIUS + FIELD_DX, FIELD_HEIGHT * POINT_RADIUS + FIELD_DY);
        frame.setLocation(START_LOCATION, START_LOCATION);
        frame.setResizable(false);
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                snake.setDirection(e.getKeyCode());

            }
        });

        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.white);

        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);

        frame.setVisible(true);
        snake = new Snake(START_SNAKE_X, START_SNAKE_Y, START_SNAKE_SIZE, START_DIRECTION);
        food = new Food();
        poison = new Poison();
        while (!gameOver) {
            snake.move();
            if (food.isEaten()) {
                food.next();
                poison.next2();
            }
            canvasPanel.repaint();
            try {
                Thread.sleep(SHOW_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    class Snake {
        ArrayList<Point> snake = new ArrayList<>();
        int direction;

        public Snake(int x, int y, int length, int direction) {
            for (int i = 0; i < length; i++) {
                Point point = new Point(x - i, y, DEFAULT_COLOR);
                snake.add(point);
            }
            this.direction = direction;
        }

        boolean isInsideSnake(int x, int y) {
            for (Point point : snake) {
                if ((point.getX() == x) && (point.getY() == y)) {
                    return true;
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
            if (direction == LEFT) {
                x--;
            }
            if (direction == RIGHT) {
                x++;
            }
            if (direction == UP) {
                y--;
            }
            if (direction == DOWN) {
                y++;
            }
            if (x > FIELD_WIDTH - 1) {
                x = 0;
            }
            if (x < 0) {
                x = FIELD_WIDTH - 1;
            }
            if (y > FIELD_HEIGHT - 1) {
                y = 0;
            }
            if (y < 0) {
                y = FIELD_HEIGHT - 1;
            }
            gameOver = isInsideSnake(x, y);
            snake.add(0, new Point(x, y, DEFAULT_COLOR));
            if (isFood(food)) {
                food.eat();
                frame.setTitle(TITLE_OF_PROGRAM + " : " + snake.size());
            } else {
                snake.remove(snake.size() - 1);
            }
        }

        void setDirection(int direction) {
            if ((direction >= LEFT) && (direction <= DOWN)) {
                if (Math.abs(this.direction - direction) != 2) {
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
            super(-1, -1, FOOD_COLOR);
            this.color = FOOD_COLOR;
        }

        void eat() {
            this.setXY(-1, -1);
        }

        boolean isEaten() {
            return this.getX() == -1;
        }
        boolean isFood(int x, int y) {
                if ((food.getX() == x) && (food.getY() == y)) {
                    return true;

            }
            return false;
        }

        void next() {
            int x, y;
            do {
                x = random.nextInt(FIELD_WIDTH - 1);
                y = random.nextInt(FIELD_HEIGHT - 1);
            } while (snake.isInsideSnake(x, y));
            this.setXY(x, y);

        }
    }

    class Poison extends Point {
        ArrayList<Point> poisons = new ArrayList<>();

        public Poison() {
            super(-1, -1, POISON_COLOR);
            this.color = POISON_COLOR;
        }

        boolean isPoison(int x, int y) {
            for (Point poison : poisons) {
                if ((poison.getX() == x) && (poison.getY() == y)) {
                    return true;
                }
            }
            return false;
        }

        void next2() {
            int x, y;
            do {
                x = random.nextInt(FIELD_WIDTH - 1);
                y = random.nextInt(FIELD_HEIGHT - 1);
            } while (isPoison(x, y) || snake.isInsideSnake(x, y) ||food.isFood(x, y));
            this.setXY(x, y);
            poisons.add(new Point(x, y, color));

        }
    }


    class Point {
        int x, y;
        Color color = DEFAULT_COLOR;

        public Point(int x, int y, Color color) {
            this.setXY(x, y);
        }

        void paint(Graphics e) {
            e.setColor(color);
            e.fillOval(x * POINT_RADIUS, y * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

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
            if (gameOver) {
                g.setColor(Color.red);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(GAME_OVER_MSG, (FIELD_WIDTH * POINT_RADIUS + FIELD_DX - fm.stringWidth(GAME_OVER_MSG)) / 2,
                        (FIELD_HEIGHT * POINT_RADIUS - FIELD_DY) / 2);
            }
        }
    }
}
