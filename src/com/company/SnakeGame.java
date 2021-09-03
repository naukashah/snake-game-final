package com.company;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import javax.sound.sampled.*;
import javax.swing.*;

public class SnakeGame extends JFrame {

	private GameBoardGUI board;

	private SideBoardGUI side;

	private Random random;

	private volatile GameClock logicTimer;

	private boolean isNewGame;

	private volatile boolean isGameOver;

	private volatile boolean isKeyPressedAfterGameOver = false;

	private boolean isTreeEnabled;

	private boolean isGamePaused;

	private volatile LinkedList<Point> snake;

	private volatile LinkedList<Point> previousSnake;

	private LinkedList<SnakeDirection> directions;

	private volatile int score;

	private int fruitsEaten;

	private int nextFruitScore;

	private int speedPoints = 0;

	private int timerForCheatInSeconds = 10;

	private volatile int countDownIfGameIsOverWithCheatCode=0;

	private volatile boolean isCheatCodeUsed = false;

	public boolean isTreeHit = false;

	public int treeHitCountDown = GameConstant.TREE_HIT_COUNTDOWN_MAX;

	public boolean isTreeHit() {
		return isTreeHit;
	}

	private void setTreeHit(boolean treeHit) {
		isTreeHit = treeHit;
	}

	public int getTreeHitCountDown() {
		return treeHitCountDown;
	}

	private void setTreeHitCountDown(int treeHitCountDown) {
		this.treeHitCountDown = treeHitCountDown;
	}


	SnakeGame() {
		super("Snake Game Project Team 1");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		this.board = new GameBoardGUI(this);
		this.side = new SideBoardGUI(this);

		add(board, BorderLayout.CENTER);
		add(side, BorderLayout.EAST);
		board.setOpaque(true);


		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				// if user is pressing key after game-over then it means user wants to change
				// the snake direction and set the flag that key has been pressed.
				if (countDownIfGameIsOverWithCheatCode > 0 && e.getKeyCode() != KeyEvent.VK_C) {
					isKeyPressedAfterGameOver = true;
				}

				switch (e.getKeyCode()) {

				case KeyEvent.VK_UP:
					upKeyPressed();
					break;

				case KeyEvent.VK_DOWN:
					downKeyPressed();
					break;

				case KeyEvent.VK_LEFT:
					leftKeyPressed();
					break;

				case KeyEvent.VK_RIGHT:
					rightKeyPressed();
					break;

				case KeyEvent.VK_P:
					playPauseGame();
					side.hideInteractiveSpeedOptions();
					break;

				case KeyEvent.VK_I:
					if (!isPaused()) {
						playPauseGame();
						side.showInteractiveSpeedOptions();
					}
					break;

				case KeyEvent.VK_T:
					if (isTreeEnabled) {
						isTreeEnabled = false;
						clearTree();
					} else {
						isTreeEnabled = true;
						showTrees();
					}
					break;

				case KeyEvent.VK_ENTER:
					if (isNewGame || isGameOver) {
						resetGame();
					}
					break;

				case KeyEvent.VK_A:
					if (isNewGame || isGameOver || !isNewGame || !isGameOver) {
						resetGame();
					}
					break;

				case KeyEvent.VK_S:
					if (!isGamePaused && !isGameOver) {
						decreaseGameSpeed();
						break;
					}

				case KeyEvent.VK_F:
					if (!isGamePaused && !isGameOver) {
						increaseGameSpeed();
						break;
					}

				case KeyEvent.VK_C:
					if (isGameOver) {
						applyCheatCode();
					}
					break;

				case KeyEvent.VK_H:
					helpKeyPressed();
				}


			}
		});

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void playPauseGame() {
		if (!isGameOver) {
			isGamePaused = !isGamePaused;
			logicTimer.setPaused(isGamePaused);
		}
	}

	public void increaseGameSpeed() {
		logicTimer.incrementCyclesPerSecond();
		addScoreWithSpeed();
	}

	public void decreaseGameSpeed() {
		logicTimer.decrementCyclesPerSecond();
		reduceScoreWithSpeed();
	}

	public void upKeyPressed() {
		if (!isGamePaused && !isGameOver) {
			if (directions.size() < GameConstant.GAME_MAX_DIRECTIONS) {
				SnakeDirection last = directions.peekLast();
				if (last != SnakeDirection.DOWN && last != SnakeDirection.UP) {
					directions.addLast(SnakeDirection.UP);
				}
			}
		}
	}

	public void downKeyPressed() {
		if (!isGamePaused && !isGameOver) {
			if (directions.size() < GameConstant.GAME_MAX_DIRECTIONS) {
				SnakeDirection last = directions.peekLast();
				if (last != SnakeDirection.UP && last != SnakeDirection.DOWN) {
					directions.addLast(SnakeDirection.DOWN);
				}
			}
		}
	}

	public void leftKeyPressed() {
		if (!isGamePaused && !isGameOver) {
			if (directions.size() < GameConstant.GAME_MAX_DIRECTIONS) {
				SnakeDirection last = directions.peekLast();
				if (last != SnakeDirection.RIGHT && last != SnakeDirection.LEFT) {
					directions.addLast(SnakeDirection.LEFT);
				}
			}
		}
	}

	public void rightKeyPressed() {
		if (!isGamePaused && !isGameOver) {
			if (directions.size() < GameConstant.GAME_MAX_DIRECTIONS) {
				SnakeDirection last = directions.peekLast();
				if (last != SnakeDirection.LEFT && last != SnakeDirection.RIGHT) {
					directions.addLast(SnakeDirection.RIGHT);
				}
			}
		}
	}

	public void helpKeyPressed() {
		StringBuilder message = new StringBuilder();
		message.append("ABOUT\n");
		message.append("\n This is recreations of Snake Game.\n\n\n");
		message.append("P: Pause/Resume Game \n");
		message.append("A: Start new game \n");
		message.append("T: Show Trees \n");
		message.append("C: Cheat Code \n");
		message.append("F: Speed Up \n");
		message.append("S: Speed Down \n");
		message.append("I: Interactive Mode \n");
		logicTimer.setPaused(true);
		JOptionPane.showMessageDialog(board, message);
		logicTimer.setPaused(false);
	}

	private void applyCheatCode() {
		if (!isGameOver) {
			return;
		}

		// execute apply cheat code in separate thread to avoid blocking key-listener
		// thread from where this operation is called.
		ForkJoinPool.commonPool().execute(() -> {
			countDownIfGameIsOverWithCheatCode = timerForCheatInSeconds;
			isGameOver = false;
			// wait for 10 seconds to move the snake
			for (int i = 0; i < timerForCheatInSeconds; i++) {
				try {
					Thread.sleep(1000);
					if (isKeyPressedAfterGameOver) {
						break;
					}
				} catch (InterruptedException e) {
					// Ok
				}
				countDownIfGameIsOverWithCheatCode--;
			}
			countDownIfGameIsOverWithCheatCode = 0;
			if(isKeyPressedAfterGameOver) {
				//System.out.println("flag : " + isCheatCodeUsed);
				if(isCheatCodeUsed == false) {
					score = score / 2;
					snake.clear();
					snake.addAll(previousSnake);
					logicTimer.resetGame();
					isKeyPressedAfterGameOver = false;
					isCheatCodeUsed = true;
					//System.out.println("flag : " + isCheatCodeUsed);
				}
			}
		});
	}

	void playBackGroundMusic() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		File url = new File("./src/com/company/bg_music_1.wav");
		Clip clip = AudioSystem.getClip();
		// getAudioInputStream() also accepts a File or InputStream
		AudioInputStream ais = AudioSystem.getAudioInputStream(url);
		clip.open(ais);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	void playDingMusic() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		File url = new File("./src/com/company/ding.wav");
		Clip clip = AudioSystem.getClip();
		// getAudioInputStream() also accepts a File or InputStream
		AudioInputStream ais = AudioSystem.getAudioInputStream(url);
		clip.open(ais);
		clip.loop(0);
	}

	void crashMusic() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		File url = new File("./src/com/company/crash.wav");
		Clip clip = AudioSystem.getClip();
		// getAudioInputStream() also accepts a File or InputStream
		AudioInputStream ais = AudioSystem.getAudioInputStream(url);
		clip.open(ais);
		clip.loop(0);
	}


	void startGame() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		this.random = new Random();
		this.snake = new LinkedList<>();
		this.previousSnake = new LinkedList<>();
		this.directions = new LinkedList<>();
		this.logicTimer = new GameClock(1.0f);
		this.isNewGame = true;
		
		
		// Set the timer to paused initially.
		logicTimer.setPaused(true);
		playBackGroundMusic();


		while (true) {
			// Get the current frame's start time.
			long start = System.nanoTime();

			// Update the logic timer.
			logicTimer.updateGame();

			if (logicTimer.hasElapsedCycle()) {
				updateGame();
			}

			// Repaint the board and side panel with the new content.
			board.repaint();
			side.repaint();


			long delta = (System.nanoTime() - start) / 1000000L;
			if (delta < GameConstant.GAME_FRAME_TIME) {
				try {
					Thread.sleep(GameConstant.GAME_FRAME_TIME - delta);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void updateGame() throws UnsupportedAudioFileException, IOException, LineUnavailableException {

		CellType collision = updateSnake();


		if (collision != null) {
			if (collision != CellType.Tree) {
				setTreeHit(false);
				this.setTreeHitCountDown(GameConstant.TREE_HIT_COUNTDOWN_MAX);
			}

			switch (collision) {
				case Fruit:
					this.isTreeHit = false;
					this.treeHitCountDown = 10;
					fruitsEaten++;
					//added speedPoints for add/reduce based on speed F, S
					score += nextFruitScore + speedPoints;
					newFruit();
					playDingMusic();
					break;
				case SnakeBody:
					isGameOver = true;
					logicTimer.setPaused(true);
					crashMusic();
					break;
				case Tree:
					if (isTreeHit()) {
						crashMusic();
						if (this.treeHitCountDown <= 0) {
							isGameOver = true;
							logicTimer.setPaused(true);
						} else {
							this.treeHitCountDown--;
						}
					} else {
						this.setTreeHit(true);
					}
					break;
			}
		}

		if (nextFruitScore > 10) {
			nextFruitScore--;
		}
	}


	public void addScoreWithSpeed() {
		speedPoints = Math.min(100, speedPoints + 10);
	}


	public void reduceScoreWithSpeed() {
		speedPoints = Math.max(0, speedPoints - 10);
	}


	private CellType updateSnake() {

		SnakeDirection direction = directions.peekFirst();

		Point head = new Point(snake.peekFirst());
		switch (direction) {
		case UP:
			head.y--;
			break;

		case DOWN:
			head.y++;
			break;

		case LEFT:
			head.x--;
			break;

		case RIGHT:
			head.x++;
			break;
		}

		if (head.x < 0 || head.x >= GameConstant.GAME_BOARD_COLUMN_COUNT || head.y < 0 || head.y >= GameConstant.GAME_BOARD_ROW_COUNT) {
			return CellType.SnakeBody; // Pretend we collided with our body.
		}

		if (this.board.getCell(head.x, head.y) == CellType.Tree) {
			if (directions.size() > 1) {
				directions.poll();
			}
			return  CellType.Tree;
		}

		this.setTreeHit(false);
		this.setTreeHitCountDown(GameConstant.TREE_HIT_COUNTDOWN_MAX);

		CellType old = board.getCell(head.x, head.y);
		if (old != CellType.Fruit && snake.size() > GameConstant.GAME_MIN_SNAKE_LENGTH) {
			Point tail = snake.removeLast();
			board.setCell(tail, null);
			old = board.getCell(head.x, head.y);
		}

		if (old != CellType.SnakeBody) {
			board.setCell(snake.peekFirst(), CellType.SnakeBody);
			// before updating snake with next move save it's previous state
			previousSnake.clear();
			previousSnake.addAll(snake);
			snake.push(head);
			board.setCell(head, CellType.SnakeHead);
			if (directions.size() > 1) {
				directions.poll();
			}
		}

		return old;
	}


	private void resetGame() {

		this.score = 0;
		this.fruitsEaten = 0;
		//System.out.println("reset method: :" + isCheatCodeUsed);
		isCheatCodeUsed = false;

		this.setTreeHit(false);
		this.setTreeHitCountDown(GameConstant.TREE_HIT_COUNTDOWN_MAX);

		this.isNewGame = false;
		this.isGameOver = false;

		Point head = new Point(GameConstant.GAME_BOARD_COLUMN_COUNT / 2, GameConstant.GAME_BOARD_ROW_COUNT / 2);

		snake.clear();
		snake.add(head);

		board.clearBoard();
		board.setCell(head, CellType.SnakeHead);

		directions.clear();
		directions.add(SnakeDirection.UP);

		logicTimer.resetGame();

		newFruit();
	}

	public boolean isNewGame() {
		return isNewGame;
	}


	public boolean isGameOver() {
		return isGameOver;
	}


	public boolean isPaused() {
		return isGamePaused;
	}


	private void newFruit() {
		// Reset the score for this fruit to 100.
		this.nextFruitScore = 100;

		int index = random.nextInt(GameConstant.GAME_BOARD_COLUMN_COUNT * GameConstant.GAME_BOARD_ROW_COUNT - snake.size());

		int freeFound = -1;
		for (int x = 0; x < GameConstant.GAME_BOARD_COLUMN_COUNT; x++) {
			for (int y = 0; y < GameConstant.GAME_BOARD_ROW_COUNT; y++) {
				CellType type = board.getCell(x, y);
				if (type == null || type == CellType.Fruit) {
					if (++freeFound == index) {
						board.setCell(x, y, CellType.Fruit);
						break;
					}
				}
			}
		}
		showTrees();
	}


	private void showTrees() {
		if (!isTreeEnabled) {
			return;
		}
		for (int i = 0; i < GameConstant.GAME_MAX_TREES; i++) {

			int index = random.nextInt(GameConstant.GAME_BOARD_COLUMN_COUNT * GameConstant.GAME_BOARD_ROW_COUNT - snake.size());

			int freeFound = -1;
			for (int x = 0; x < GameConstant.GAME_BOARD_COLUMN_COUNT; x++) {
				for (int y = 0; y < GameConstant.GAME_BOARD_ROW_COUNT; y++) {
					CellType type = board.getCell(x, y);
					if (type == null || type == CellType.Tree) {
						if (++freeFound == index) {
							board.setCell(x, y, CellType.Tree);
							break;
						}
					}
				}
			}

			// increase score on new tree
			score += 100;
		}
	}

	private void clearTree() {
		for (int x = 0; x < GameConstant.GAME_BOARD_COLUMN_COUNT; x++) {
			for (int y = 0; y < GameConstant.GAME_BOARD_ROW_COUNT; y++) {
				CellType type = board.getCell(x, y);
				if (type == CellType.Tree) {
					board.setCell(x, y, null);
				}
			}
		}
	}


	public int getScore() {
		return score;
	}

	public int getCountDownIfGameIsOverWithCheatCode() {
		return countDownIfGameIsOverWithCheatCode;
	}


	public int getFruitsEaten() {
		return fruitsEaten;
	}


	public int getNextFruitScore() {
		return nextFruitScore;
	}

	

	public int getSpeedPoints() {
		return speedPoints;
	}


	public SnakeDirection getDirection() {
		return directions.peek();
	}


}
