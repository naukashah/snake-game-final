package com.company;

//Game clock represent number of cycles that have elapsed over time.
public class GameClock {

	private float msPerCycle;

	private long lastUpdate;

	private int cyclesSpent;

	private float excessTimeToNextCycle;

	private boolean isGamePaused;
	
	private float currentCyclesPerSecond = 0;
	

	public GameClock(float cyclesPerSecond) {
		setCyclesPerSecond(cyclesPerSecond);
		resetGame();
	}


	public void setCyclesPerSecond(float cyclesPerSecond) {
		this.msPerCycle = (1.0f / cyclesPerSecond) * 1000;
	}


	public void updateCyclesPerSecond(boolean increment) {
		currentCyclesPerSecond =  1000/ msPerCycle;
		setCyclesPerSecond(1.5f * currentCyclesPerSecond * (increment ? 1f : 0.5f));
		updateGame();
	}


	public void incrementCyclesPerSecond() {
		updateCyclesPerSecond(true);
	}

	public void decrementCyclesPerSecond() {
		updateCyclesPerSecond(false);
	}


	public void resetGame() {
		this.cyclesSpent = 0;
		this.excessTimeToNextCycle = 1.0f;				
		this.lastUpdate = getCurrentTime();
		this.isGamePaused = false;
	}
	

	public void updateGame() {
		//Get the current time and calculate the delta time.
		long currUpdate = getCurrentTime();
		float delta = (float)(currUpdate - lastUpdate) + excessTimeToNextCycle;

		//Update the number of elapsed and excess ticks if we're not paused.
		if(!isGamePaused) {
			this.cyclesSpent += (int)Math.floor(delta / msPerCycle);
			this.excessTimeToNextCycle = delta % msPerCycle;
		}
		
		//Set the last update time for the next update cycle.
		this.lastUpdate = currUpdate;
	}
	

	public void setPaused(boolean paused) {
		this.isGamePaused = paused;
	}


	public boolean hasElapsedCycle() {
		if(cyclesSpent > 0) {
			this.cyclesSpent--;
			return true;
		}
		return false;
	}


	private static final long getCurrentTime() {
		return (System.nanoTime() / 1000000L);
	}

}
