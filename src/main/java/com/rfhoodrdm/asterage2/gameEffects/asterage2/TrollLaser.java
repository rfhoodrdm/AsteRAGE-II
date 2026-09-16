/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameEffects.asterage2;


import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlayerShip;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.TrollBaseShip;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.Expires;
import com.rfhoodrdm.asterage2.utility.GameConstants;

/**
 * Space effect which represents the troll super weapon.
 */
public class TrollLaser
extends SpaceEffect
implements Expires
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	TrollBaseShip firingShip;		//which ship is firing the laser
	PlayerShip targetShip;		//which ship is the target of the laser?
	
	public static final int MAX_LASER_COOLDOWN = GameConstants.FRAMES_PER_SECOND * 1;
	
	//damage rating for the laser is 10 seconds worth of standard shield regeneration.
	public static final double DAMAGE_RATING = 
			PlayerShip.SHIELD_REGEN_BASE_RATE * GameConstants.FRAMES_PER_SECOND * 10;
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public TrollLaser( TrollBaseShip passedFiringShip, PlayerShip passedTargetShip )
	{
		super (0.0, 0.0);		//drawing of the super laser depends on the parents' locations instead.
		this.firingShip = passedFiringShip;
		this.targetShip = passedTargetShip;
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override	protected int getMaxExpiredCowndown()		{	return MAX_LASER_COOLDOWN;	}
	@Override	protected BufferedImage getSimpleSprite()	{	return null;	}
	@Override	protected double getMaxVelocity()			{	return 0.0;	}
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	/**
	 * Custom painting method.
	 * @param g 
	 */
	@Override
	public void paintObject( Graphics g )
	{
		//first check to see if we should even draw the laser
		if ( true == firingShip.checkExpired() )	{ return; }
		
		//convert to Graphics2D object.
		Graphics2D g2d = (Graphics2D) g;
		
		//determine the coordinates of the laser being drawn.
		int originatingX = firingShip.getxCoordinateAsInt();
		int originatingY = firingShip.getyCoordinateAsInt();
		int targetX = targetShip.getxCoordinateAsInt();
		int targetY = targetShip.getyCoordinateAsInt();
		
		//determine the color and width of the laser
		g2d.setColor( calculateLaserColor() );
		
		g2d.setStroke( new BasicStroke (5.0f) );
		
		//draw the laser!
		g2d.drawLine(	originatingX, originatingY, 
						targetX, targetY);
				
	} //end method paintObject
	
	/**
	 * Determine what color to display for the laser.
	 * @return Color of laser as displayed on the game board.
	 */
	private Color calculateLaserColor()
	{
		int remainingCooldown = getExpiredCountdownTimer();		//remaining time
		int maxCooldown = getMaxExpiredCowndown();				//max time.
		
		double alphaChannelPercentage =  1.5 * (double)remainingCooldown / (double)maxCooldown ;		//start fading at 66%.
		if ( alphaChannelPercentage > 1.0 ) { alphaChannelPercentage = 1.0; }
		int alphaChannel = (int) Math.floor( 255 * alphaChannelPercentage );
		
		Color laserColor = new Color( 225, 225, 225, alphaChannel  );
		return laserColor;
	} //end method calculateLaserColor
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */


} //end TrollLaser definition
