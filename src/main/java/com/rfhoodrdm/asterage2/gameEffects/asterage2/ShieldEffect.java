/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameEffects.asterage2;

import com.rfhoodrdm.asterage2.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.SpaceObject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author roberthood
 */
public class ShieldEffect
extends SpaceEffect
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	int shieldStrengthPercentageBeingRendered = 0;
	SpaceObject parentObject;
	
	public static final int MAX_SHIELD_EFFECT_EXPIRED_COUNTDOWN = GameConstants.FRAMES_PER_SECOND * 2;	//two second shield effect
	
	public static final int SHIELD_EFFECT_SPACER = 8;		//give a margin between the object and the shield effect drawn.
	public static final float SHIELD_WIDTH_AT_MAX = 5;		//how thick are the shields drawn when full?
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public ShieldEffect( SpaceObject passedParentObject )
	{
		super(0.0, 0.0);							//shield effects don't have a set location per se.
		parentObject = passedParentObject;			//remember which object this effect is attached to.
	} //end constructor
		
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override public int getMaxExpiredCowndown() { return ShieldEffect.MAX_SHIELD_EFFECT_EXPIRED_COUNTDOWN; }
		
	public void displayShieldStrength( int shieldStrengthPercentRemaining )
	{
		//remember how much shields are being rendered.
		shieldStrengthPercentageBeingRendered = shieldStrengthPercentRemaining;
		resetCoundownToMax();
	} //end method displayShieldEffect
	
	/**
	 * Custom painting method.
	 * @param g 
	 */
	@Override
	public void paintObject( Graphics g )
	{
		//first check if this effect is expired. If so, then we skip drawing it. Also check to see if we have a null parent.
		if ( (null == parentObject) ||  (true == checkExpired()) ) { return; }	
	
		Graphics2D g2d = (Graphics2D) g;
		
		//calculate and set attributes
		Color shieldColor = calculateShieldColor();
		double shieldWidth = calculateShieldThickness();
		int shieldXCoordinate = parentObject.getxCoordinateAsInt();
		int shieldYCoordinate = parentObject.getyCoordinateAsInt();
		int calculatedShieldRadius = parentObject.getSpatialRadius() + SHIELD_EFFECT_SPACER;
		
		//draw the shields
		g2d.setColor(shieldColor);
		g2d.setStroke( new BasicStroke((float)shieldWidth) );
		g2d.drawOval	(	shieldXCoordinate - calculatedShieldRadius, 
							shieldYCoordinate - calculatedShieldRadius, 
							2 * calculatedShieldRadius, 
							2 * calculatedShieldRadius);
		
		//age this object 1 tick for drawing it.
		decrementExpiredCountdownTimer();
	} //end method paintObject
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	private Color calculateShieldColor()
	{
		//shields start at white when the effect is new, and fade to black over time.
		int countdownRemaining = getExpiredCountdownTimer();
		int maxCountdown = getMaxExpiredCowndown();
		double fadeRatio = (double) countdownRemaining /  (double) maxCountdown;
		
		int rbgColorBase = (int) Math.floor(255.0 * fadeRatio);
		
		return new Color ( rbgColorBase, rbgColorBase, rbgColorBase, rbgColorBase );
	} //end method calculateShieldColor
	
	private double calculateShieldThickness()
	{
		//whatever percentage the shields are at, of the max thickness for this effect.
		//check for lower than 0, or greater than 100.
		double shieldRatio = (shieldStrengthPercentageBeingRendered > 0) ?
				((double) shieldStrengthPercentageBeingRendered / 100.0) : 0.01;
		if ( shieldRatio > 100.0 ) { shieldRatio = 100.0; }
				
		double shieldThickness = 5.0 * shieldRatio;
		return shieldThickness;
	} //end method calculateShieldThickness
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */

	@Override	protected BufferedImage getSimpleSprite()	{ return null; /* no sprite for shield effects */ }
	@Override	protected double getMaxVelocity()	{ return 0.0; /* space effects don't move on their own.*/	}
	
	
} //end class ShieldEffect definition
