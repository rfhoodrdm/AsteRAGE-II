/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameEffects.asterage2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.rfhoodrdm.asterage2.constants.GameConstants;
import com.rfhoodrdm.asterage2.utility.RandomizedNumbers;

/**
 *
 * @author roberthood
 */
public class ShipExplosionEffect
extends SpaceEffect
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final int MAX_EXPLOSION_COOLDOWN = GameConstants.FRAMES_PER_SECOND * 1 ;	//time, in seconds
	
	
	private int explosionRadius;					//how big is the explosion?
	private ExplosionType explosionType;			//what type of explosion is this? Player or troll?
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public ShipExplosionEffect ( int xCoordinate, int yCoordinate, ExplosionType whatType, int passedExplosionRadius )
	{
		super( xCoordinate, yCoordinate );			//call to super to set location
		this.explosionType = whatType;				//remember what explosion type this is.
		explosionRadius = passedExplosionRadius;	//remeber how big to make the explosion
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
		int explosionSize = (int) Math.floor(explosionRadius * timeRatio);
		int explosionFade = (int) Math.floor(255 * fadeRatio);
		
		//calculate the bounds of the explosion graphic.
		int locationX = getxCoordinateAsInt() - explosionSize;
		int locationY = getyCoordinateAsInt() - explosionSize;
		
		//get and correct the explosion color based on timing.
		Color randomColor = getRandomColor();
		int randomRed = randomColor.getRed();
		int randomGreen = randomColor.getGreen();
		int randomBlue = randomColor.getBlue();
		Color fadedRandomColor = new Color (	randomRed,
												randomGreen,
												randomBlue,
												explosionFade);
		g.setColor( fadedRandomColor );
		
		//draw the explosion
		g.fillOval(	locationX, 
					locationY, 
					explosionSize * 2, 
					explosionSize * 2 ); 
	} //end method paintObject
	
	
	private Color getRandomColor()
	{
		//get a different random color, depending on what type of explosion this is.
		switch ( explosionType )
		{
			case PLAYER:
				return randomPlayerColor();
				
			case TROLL:
			default:
				return randomTrollColor();
		} //end switch based on explosion type
		
	} //end method getRandomColor
	
	
	private Color randomPlayerColor()
	{
		int randomChance = RandomizedNumbers.random100();
		
		if ( randomChance < 25 )		return Color.RED;
		else if ( randomChance < 50 )	return Color.ORANGE;
		else if ( randomChance < 75 )	return Color.YELLOW;
		else return Color.BLACK;
		
	} //end method randomPlayerColor
	
	
	private Color randomTrollColor ()
	{
		int randomChance = RandomizedNumbers.random100();
		
		if ( randomChance < 25 )		return new Color( 0x99, 0x00, 0x66);
		else if ( randomChance < 50 )	return new Color( 0x33, 0x33, 0xFF);
		else if ( randomChance < 75 )	return new Color( 0x00, 0x66, 0x66);
		else return Color.BLACK;
	}
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	public static enum ExplosionType
	{
		PLAYER,
		TROLL;
	}
} //end class ShipExplosionEffect definition
