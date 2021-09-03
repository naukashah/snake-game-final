package com.company;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

//SideBoardGUI class represent statistics and controls
public class SideBoardGUI extends JPanel {

	private SnakeGame game;

	private GameClock clockInstance;

	JButton speedUpButton;
	JButton speedDownButton;

	public SideBoardGUI(SnakeGame game) {
		this.game = game;

		setPreferredSize(new Dimension(300, GameConstant.GAME_BOARD_ROW_COUNT * GameConstant.GAME_BOARD_CELL_SIZE));
		setBackground(Color.WHITE);

		speedUpButton = new JButton("Up");
		speedUpButton.setBounds(10, 75, 100, 30);
		speedUpButton.setFocusable(false);
		speedUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game.increaseGameSpeed();
			}
		});

		speedDownButton = new JButton("Down");
		speedDownButton.setBounds(150, 75, 100, 30);
		speedDownButton.setFocusable(false);
		speedDownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game.decreaseGameSpeed();
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.BLACK);

		g.setFont(GameConstant.GAME_FONT_LARGE);
		g.drawString(" Team 1 Project : Snake Game", 0, 50);

		g.setFont(GameConstant.GAME_FONT_MEDIUM);
		g.drawString("Game Console ", GameConstant.GAME_MESSAGE_SPACE, GameConstant.GAME_CONTROL +'\n');
		g.drawString("Scores", GameConstant.GAME_MESSAGE_SPACE, GameConstant.GAME_SCORES_OFFSET);


		g.setFont(GameConstant.GAME_FONT_SMALL);

		//Draw the content for the controls category.
		int displayScoresAndCodes = GameConstant.GAME_CONTROL;
		g.drawString("Move Up: Up Arrow", GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Move Down: Down Arrow", GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Move Left: Left Arrow", GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Move Right: Right Arrow", GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Pause/Resume Game: P", GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);

		g.drawString("Restart: A", GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Show Trees: T", GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Cheat Code: C", GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Interactive Mode: I", GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Speed Up: F", GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Slow Down: S", GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);

		//Draw the content for the scored category.
		displayScoresAndCodes = GameConstant.GAME_SCORES_OFFSET;
		g.drawString("Total Score: " + game.getScore(), GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Fruit Eaten: " + game.getFruitsEaten(), GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Fruit Score: " + game.getNextFruitScore(), GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		g.drawString("Speed Points: " + game.getSpeedPoints(), GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);

		g.setColor(Color.RED);
		if(game.getCountDownIfGameIsOverWithCheatCode() != 0) {
			g.drawString("CountDown to use cheat code: " + game.getCountDownIfGameIsOverWithCheatCode(), GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		}

		if(game.isTreeHit()) {
			g.drawString("Countdown to move back from tree hit: " + game.getTreeHitCountDown(), GameConstant.GAME_MESSAGE_SPACE, displayScoresAndCodes += GameConstant.GAME_MESSAGE_STRIP);
		}
	}

	public void showInteractiveSpeedOptions() {
		this.add(this.speedUpButton);
		this.add(this.speedDownButton);
	}

	public void hideInteractiveSpeedOptions() {
		this.remove(this.speedUpButton);
		this.remove(this.speedDownButton);
	}
}
