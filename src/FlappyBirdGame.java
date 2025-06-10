import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class FlappyBirdGame extends Application {
    private static final int WIDTH = 720;
    private static final int HEIGHT = 640;

    private double highestScore = 0;
    private double score = 0;
    private boolean gameOver = false;
    private boolean gameStarted = false;

    private Image backgroundImg;
    private Image birdImg;
    private Image topPipeImg;
    private Image bottomPipeImg;

    private Bird bird;
    private ArrayList<Pipe> pipes = new ArrayList<>();

    private int velocityY = 0;
    private final double gravity = 1;
    private final int pipeSpeed = -4;

    private Random random = new Random();

    public void start(Stage stage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        try {
            backgroundImg = new Image(getClass().getResourceAsStream("/flappybirdbg.jpg"));
            birdImg = new Image(getClass().getResourceAsStream("/flappybird.png"));
            topPipeImg = new Image(getClass().getResourceAsStream("/toppipe.jpg"));
            bottomPipeImg = new Image(getClass().getResourceAsStream("/bottompipe.jpg"));
        } 
        
        catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        bird = new Bird(WIDTH / 8, HEIGHT / 2, birdImg);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                if (gameOver) {
                    resetGame();
                } 
                
                else if (!gameStarted) {
                    gameStarted = true;
                } 
                
                else {
                    velocityY = -9;
                }
            }
        });


        AnimationTimer gameLoop = new AnimationTimer() {
        	
            public void handle(long now) {
            	
                if (gameStarted && !gameOver) {
                    updateGame();
                    drawGame(gc);
                } 
                else if (!gameStarted) {
                    drawStartScreen(gc);
                }
            }
        };

        gameLoop.start();

        stage.setTitle("Flappy Bird");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void updateGame() {
        // Apply gravity only after the game has started
        if (gameStarted && !gameOver) {
            // Add a small initial positive velocity to prevent the bird from falling instantly
            if (velocityY == 0) {
                velocityY = 2; // Start with a small downward velocity
            }

            velocityY += gravity;  // Gravity increases velocity (falls down over time)
            bird.y += velocityY;   // Apply the updated velocity to the bird's vertical position

            ArrayList<Pipe> newPipes = new ArrayList<>();
            for (Pipe pipe : pipes) {
                pipe.x += pipeSpeed;

                if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                    score += 0.5;
                    pipe.passed = true;
                }

                if (collision(bird, pipe)) {
                    gameOver = true;
                    highestScore = Math.max(highestScore, score);
                }

                if (pipe.x + pipe.width > 0) {
                    newPipes.add(pipe);
                }
            }
            pipes = newPipes;

            if (bird.y > HEIGHT || bird.y < 0) {
                gameOver = true;
            }

            if (pipes.isEmpty() || pipes.get(pipes.size() - 1).x < WIDTH / 2) {
                placePipes();
            }
        }
    }


    private void drawGame(GraphicsContext gc) {

        gc.drawImage(backgroundImg, 0, 0, WIDTH, HEIGHT);


        gc.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height);


        for (Pipe pipe : pipes) {
            gc.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height);
        }


        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(32));
        if (gameOver) {
            gc.fillText("Game Over: " + (int) score, 10, 35);
            gc.fillText("Highest Score: " + (int) highestScore, 10, 75);
        } else {
            gc.fillText(String.valueOf((int) score), 10, 35);
        }
    }

    private void drawStartScreen(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(32));
        gc.fillText("Press SPACE to Start", WIDTH / 4, HEIGHT / 2);
    }

    private void resetGame() {
        score = 0;
        bird.y = HEIGHT / 2;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        gameStarted = false;
    }

    private void placePipes() {
        int pipeHeight = 512;
        int pipeWidth = 64;
        int openingSpace = HEIGHT / 4;

        int randomPipeY = -pipeHeight / 4 - random.nextInt(pipeHeight / 2);

        pipes.add(new Pipe(WIDTH, randomPipeY, pipeWidth, pipeHeight, topPipeImg));
        pipes.add(new Pipe(WIDTH, randomPipeY + pipeHeight + openingSpace, pipeWidth, pipeHeight, bottomPipeImg));
    }

    private boolean collision(Bird bird, Pipe pipe) {
        return bird.x < pipe.x + pipe.width && bird.x + bird.width > pipe.x &&
                bird.y < pipe.y + pipe.height && bird.y + bird.height > pipe.y;
    }

    public static void main(String[] args) {
        launch(args);
    }   
}
