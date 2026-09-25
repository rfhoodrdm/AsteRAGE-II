package com.rfhoodrdm.asterage2.gui.titlescreen.components;

import java.awt.Color;
import java.awt.Graphics;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gui.GUI;

public class StarPoint {

	private int xCoordinate; 
	private int yCoordinate;
	private int speed; 
	
	private StarPoint() {
		randomizePositionAndSpeed();
	}

	private StarPoint(int xLocation, int yLocation) {
		setPosition(xLocation, yLocation);
		randomizeSpeed();
	}
	
	private void randomizeSpeed() {
		speed = (int) Math.floor(Math.random() * 4) + 1; // from 1 to 4.
	}
	
	private void setPosition(int x, int y) {
		this.xCoordinate = x;
		this.yCoordinate = y;
	}
	
	public static StarPoint createNewRandomStarPoint() {
		return new StarPoint();
	} 
	
	public void reset() {
		// can spawn anywhere along top or right edge.
		int locationRange = GameConstants.GAME_PANEL_WIDTH + GameConstants.GAME_PANEL_HEIGHT;
		int randomLocation = (int) Math.floor(Math.random() * locationRange);
		
		if (randomLocation < GameConstants.GAME_PANEL_WIDTH) {	
			// then it's a top edge star.
			setPosition(randomLocation, 0);
		} else {
			// else it's a right edge star
			setPosition(GameConstants.GAME_PANEL_WIDTH, randomLocation - GameConstants.GAME_PANEL_WIDTH);
		}
		
		randomizeSpeed();
	}

	private void randomizePositionAndSpeed() {
		xCoordinate = (int) Math.floor(Math.random() * GameConstants.GAME_PANEL_WIDTH);
		yCoordinate = (int) Math.floor(Math.random() * GameConstants.GAME_PANEL_HEIGHT);
		randomizeSpeed();
	}

	public boolean checkExpired() {
		// if we've traveled off of the right edge or bottom edge, then it has expired.
		return ((xCoordinate < 0) || (yCoordinate > GameConstants.GAME_PANEL_HEIGHT));
	}

	public void paintStar(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawLine(xCoordinate - 1, yCoordinate, xCoordinate + 1, yCoordinate);
		g.drawLine(xCoordinate, yCoordinate - 1, xCoordinate, yCoordinate + 1);
	}

	public void moveStar() {
		xCoordinate -= speed;
		yCoordinate += speed;
	}

}
