package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.company.GameConstant.GAME_BOARD_ROW_COUNT;
import static java.util.Arrays.fill;

// The GameBoard class is responsible for managing and displaying the contents of the game board.
public class GameBoardGUI extends JPanel {

    private static final Font FONT = new Font("Arial", Font.BOLD, 30);

    private SnakeGame game;

    private CellType[] cells;

    private BufferedImage treeImage;

    private BufferedImage snakeBody;
    private BufferedImage snakeHead; // default is down looking.
    private BufferedImage upSnakeHead;
    private BufferedImage leftSnakeHead;
    private BufferedImage rightSnakeHead;
    private BufferedImage fruitImage;

    public GameBoardGUI (SnakeGame game) {
        this.game = game;
        this.cells = new CellType[GAME_BOARD_ROW_COUNT * GameConstant.GAME_BOARD_COLUMN_COUNT];

        setPreferredSize(new Dimension(GameConstant.GAME_BOARD_COLUMN_COUNT * GameConstant.GAME_BOARD_CELL_SIZE, GAME_BOARD_ROW_COUNT * GameConstant.GAME_BOARD_CELL_SIZE));

        try {

            treeImage = ImageIO.read(new File("./src/com/company/tree.png"));
            treeImage = Utils.resize(treeImage, 35,35);

            snakeBody  = Utils.resize( ImageIO.read(new File("./src/com/company/snake-body-circle.jpeg")), 35,35);
            int snakeHeadWidth = GameConstant.GAME_BOARD_COLUMN_COUNT+5;
            int snakeHeadHeight = GAME_BOARD_ROW_COUNT+5;
            snakeHead = Utils.resize( ImageIO.read(new File("./src/com/company/snakehead-2.png")), snakeHeadWidth,snakeHeadHeight);
            upSnakeHead =  Utils.resize( ImageIO.read(new File("./src/com/company/snakehead-up.png")), snakeHeadWidth,snakeHeadHeight);
            leftSnakeHead = Utils.resize( ImageIO.read(new File("./src/com/company/snakehead-left.png")), snakeHeadWidth,snakeHeadHeight);
            rightSnakeHead =  Utils.resize( ImageIO.read(new File("./src/com/company/snakehead-right.png")), snakeHeadWidth,snakeHeadHeight);


            fruitImage = ImageIO.read(new File("./src/com/company/berry.png"));//berry.png
            fruitImage = Utils.resize(fruitImage, 35,35);//25,25


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clearBoard() {
        fill(cells, null);
    }

    public void setCell(Point point, CellType type) {
        setCell(point.x, point.y, type);
    }

    public void setCell(int x, int y, CellType type) {
        cells[y * GAME_BOARD_ROW_COUNT + x] = type;
    }

    public CellType getCell(int x, int y) {
        return cells[y * GAME_BOARD_ROW_COUNT + x];
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        //Loop through each tile on the board and draw it if it is not null.
        for(int x = 0; x < GameConstant.GAME_BOARD_COLUMN_COUNT; x++) {
            for(int y = 0; y < GAME_BOARD_ROW_COUNT; y++) {
                CellType type = getCell(x, y);
                if(type != null) {
                    System.out.println("Type="+type+ " "+x+","+y);
                    drawCell(x * GameConstant.GAME_BOARD_CELL_SIZE, y * GameConstant.GAME_BOARD_CELL_SIZE, type, graphics);
                }
            }
        }

        graphics.setColor(Color.DARK_GRAY);


        if(game.isGameOver() || game.isNewGame() || game.isPaused()) {
            graphics.setColor(Color.BLACK);

            //Get the center coordinates of the game board
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            String statusMessage = "";
            String shortMessage = "";
            if(game.isNewGame()) {
                statusMessage = "Snake Game!";
                shortMessage = "Press Enter to Start";
            } else if(game.isGameOver()) {
                statusMessage = "Game Over!";
                shortMessage = "Press Enter to Restart";
            } else if(game.isPaused()) {
                statusMessage = "Paused";
                shortMessage = "Press P to Resume";
            }

            graphics.setFont(FONT);
            graphics.drawString(statusMessage, centerX - graphics.getFontMetrics().stringWidth(statusMessage) / 2, centerY - 50);
            graphics.drawString(shortMessage, centerX - graphics.getFontMetrics().stringWidth(shortMessage) / 2, centerY + 50);
        }
    }

    private void drawCell(int x, int y, CellType type, Graphics g) {
        /*  Use switch statement to go through each cell*/

        switch(type) {

            case Fruit:
                g.drawImage(fruitImage, x + 2, y + 2, this);
                break;

            case Tree:
                g.drawImage(treeImage, x+2, y+2, this);
                break;

            case SnakeBody:
                Color sbColor = new Color(88, 136, 65);
                g.setColor(sbColor);
                int factor = 5;
                switch(game.getDirection()) {
                    case UP:
                        g.fillOval(x+factor, y, GameConstant.GAME_BOARD_CELL_SIZE, GameConstant.GAME_BOARD_CELL_SIZE);
                        break;

                    case DOWN:
                        g.fillOval(x+factor, y, GameConstant.GAME_BOARD_CELL_SIZE, GameConstant.GAME_BOARD_CELL_SIZE);
                        break;

                    case LEFT:
                        g.fillOval(x, y + 5, GameConstant.GAME_BOARD_CELL_SIZE, GameConstant.GAME_BOARD_CELL_SIZE);
                        break;

                    case RIGHT:
                        g.fillOval(x, y + 5, GameConstant.GAME_BOARD_CELL_SIZE, GameConstant.GAME_BOARD_CELL_SIZE);
                        break;
                }

                break;
            case SnakeHead:
                BufferedImage snakeHeadToDraw = snakeHead;
                switch(game.getDirection()) {
                    case UP:
                        snakeHeadToDraw = upSnakeHead;
                        break;

                    case DOWN:
                        snakeHeadToDraw = snakeHead;
                        break;

                    case LEFT:
                        snakeHeadToDraw = leftSnakeHead;
                        break;

                    case RIGHT:
                        snakeHeadToDraw = rightSnakeHead;
                        break;
                }
                g.drawImage(snakeHeadToDraw, x, y, this);
                break;
        }
    }
}

