
package com.rfhoodrdm.asterage2.gameEffects.asterage2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.utility.RandomizedNumbers;

public class HUDExplosionEffect
extends SpaceEffect
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final int MAX_EXPLOSION_COOLDOWN = GameConstants.FRAMES_PER_SECOND * 2 ;	//time, in seconds
	
	
	private int explosionWidth;					//how big is the explosion?
	private int explosionHeight;

	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public HUDExplosionEffect ( int xCoordinate, int yCoordinate, int passedWidth, int passedHeight )
	{
		super( xCoordinate, yCoordinate );			//call to super to set location
		explosionWidth = passedWidth;				//remeber how big to make the explosion
		explosionHeight = passedHeight;
		resetCoundownToMax();						//reset the timer for this effect to max, so that it lives.
	} //end constructor

	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override	protected int getMaxExpiredCowndown()		{ return MAX_EXPLOSION_COOLDOWN;	}
	@Override	protected BufferedImage getSimpleSprite()	{ return null;}		//no specific sprite for this effect
	@Override	protected double getMaxVelocity()			{ return 0.0; }		//this effect does not move.

	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	@Override
	public void paintObject ( Graphics g )
	{
		//calculate the ratios of how the explosion is presented, based on elapsed time vs. max time.
		double timeRatio = ((double) getMaxExpiredCowndown() - getExpiredCountdownTimer()) / ((double) getMaxExpiredCowndown());
		double fadeRatio = ((double) getExpiredCountdownTimer()) / ((double) getMaxExpiredCowndown());
		int explosionFade = (int) Math.floor(255 * fadeRatio);
		
		//calculate the bounds of the explosion graphic.
		int locationX = getxCoordinateAsInt() - explosionWidth;
		int locationY = getyCoordinateAsInt() - explosionHeight;
		
		//correct the explosion color based on timing.
		Color fadedRandomColor = new Color (	255,
												0,
												0,
												explosionFade);
		g.setColor( fadedRandomColor );
		
		//draw the explosion
		g.fillRect( locationX, 
					locationY, 
					explosionWidth * 2, 
					explosionHeight * 2 ); 
	} //end method paintObject
	

	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} //end class HUDExplosionEffect definition
