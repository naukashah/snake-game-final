package com.company;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

// Main class
public class Snake{
	public static void main(String[] args) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		SnakeGame snake = new SnakeGame();
		snake.startGame();
	}
}
