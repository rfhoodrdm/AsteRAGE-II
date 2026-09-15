/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameEffects.asterage2;

import gameObjects.asterage2.SpaceObject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import utility.GameConstants;

/**
 *
 * @author roberthood
 */
public class GravityNetEffect
extends SpaceEffect
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final double GRAVITY_NET_SLOWDOWN_COOEFFICIENT = 0.70;
	public static final int MAX_EXPIRED_COOLDOWN = GameConstants.FRAMES_PER_SECOND * 5;	//one pulse every certain number of seconds.
	public static final int DRAWING_TIMER_THRESHOLD = GameConstants.FRAMES_PER_SECOND;		//when to start drawing.
	
	SpaceObject parentObject;		//what object owns this effect?
	
	Color GRAVITY_NET_RING_COLOR = new Color( 0X00, 0X99, 0X00 );
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public GravityNetEffect( SpaceObject passedParentObject )
	{
		super(0.0, 0.0);							//does not use standard coordinate system. Pass 0.
		parentObject = passedParentObject;			//remember which object owns this effect
		resetCoundownToMax();						//reset the timer for this effect to max, so that it lives.
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	@Override	protected int getMaxExpiredCowndown()		{ return MAX_EXPIRED_COOLDOWN;	}
	@Override	protected BufferedImage getSimpleSprite()	{	return null;	}				//this effect has no sprite
	@Override	protected double getMaxVelocity()			{	return 0.0; }					//this effect does not move normally.
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */

	@Override
	public void paintObject( Graphics g )
	{
		//Special case. This effect is drawn only once every so often. 
		//if we are not down to our last time span of life, then don't bother drawing this effect. 
		decrementExpiredCountdownTimer();					//decrement the timer 
		int remainingCooldown = getExpiredCountdownTimer();
		if ( remainingCooldown > DRAWING_TIMER_THRESHOLD )
		{
			return;
		} //end if check for skipping drawing for now
		
		//then we draw the gravity net effect.
		//calculate the drawing ratio based on the remaining time compared to the threshold time.
		double sizeRatio = ((double) DRAWING_TIMER_THRESHOLD - (double) remainingCooldown) / (double) DRAWING_TIMER_THRESHOLD;
		int maxRadius = parentObject.getSpatialRadius() * 2;
		int currentRadius = (int) Math.floor( sizeRatio * maxRadius );
		int xLocation = parentObject.getxCoordinateAsInt() - currentRadius;
		int yLocation = parentObject.getyCoordinateAsInt() - currentRadius;
		double colorFadeRatio = 1.0 - sizeRatio;
		
		Color currentColor = new Color (	GRAVITY_NET_RING_COLOR.getRed(), 
											GRAVITY_NET_RING_COLOR.getGreen(), 
											GRAVITY_NET_RING_COLOR.getBlue(),
											(int) Math.floor(255 * colorFadeRatio) );
		
		Graphics2D g2d = (Graphics2D) g;		//convert to Graphics2D object for enhanced drawing capability.
		g2d.setColor(currentColor);
		g2d.setStroke( new BasicStroke(5f) );
		g2d.drawOval( xLocation, yLocation, currentRadius * 2, currentRadius * 2);
		
		
		//lastly, if this effect has expired, then reset it.
		if ( checkExpired() ) { resetCoundownToMax(); }
	} //end method paintObject
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} //end class Gravity Net Effect definition
