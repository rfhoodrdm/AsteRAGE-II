package com.rfhoodrdm.asterage2.gameEffects.asterage1;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameObjects.asterage1.SpaceObject;
import com.rfhoodrdm.asterage2.gui.Image;

public class Alien
	extends SpaceEffect {
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	public static final int alienLifeSpan = (int) Math.floor( GameConstants.FRAMES_PER_SECOND * 2.5 );
	BufferedImage TrollSprite1 = Image.TROLL_1.getImage();
	BufferedImage TrollSprite2 = Image.TROLL_2.getImage();
	BufferedImage NO_SPRITE = Image.NO_IMAGE.getImage();
	
	int currentFrame = 0;
	int frameHoldCount = 0;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	/**
	 * Spawn where the asteroid was killed, in which the alien was living.
	 * @param xSpawn
	 * @param ySpawn 
	 */
	public Alien ( double xSpawn, double ySpawn ) {
		super(	xSpawn, 
				ySpawn,
				alienLifeSpan,
				null );
		//send the alien off in a random direction, at a slow-ish speed.
		this.velocityAngle = (int) Math.floor (Math.random() * 360 );
		this.velocity = 3.0 + (Math.random() * 3.0 );
	} 
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	@Override
	public void paintToBoard( Graphics g )	{
		int offset = 12;	//radius of the alien sprite.
		BufferedImage currentSprite = getAlienSprite();
		g.drawImage(	currentSprite,
						((int)Math.round( this.getXPosition() ) - offset),
						((int)Math.round( this.getYPosition() ) - offset ),
						null );
	} 
	
	@Override
	public void killObject ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList) {
		spaceObjectList.remove( this );
	}
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	private BufferedImage getAlienSprite() {
		int timeToDisappear = GameConstants.FRAMES_PER_SECOND * 1;
		
		//First, the alien blinks off of the screen 1 second before dying. If that's the case, then return no  sprite.
		if ( this.currentLifespan < timeToDisappear ) {
			return this.NO_SPRITE;
		}
		
		//Else decide which sprite to fetch.
		//Increment the frame counter, if appropriate.
		frameHoldCount = (frameHoldCount + 1) % 10;				//for how many ticks do we hold a frame?
		if ( 0 == frameHoldCount ) {
			currentFrame = (currentFrame + 1) % 2;				//move to the next frame.
		}
		
		//decide which frame sprite to return.
		if ( 0 == currentFrame ) {
			return this.TrollSprite1;
		} else {
			return this.TrollSprite2;
		} 
	} 
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}
