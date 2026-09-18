/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameEffects.asterage2;

import com.rfhoodrdm.asterage2.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlasmaBolt;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlayerShip;
import com.rfhoodrdm.asterage2.gameObjects.asterage2.SpaceObject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.utility.DebugManager;
import com.rfhoodrdm.asterage2.utility.ThetaCorrector;

/**
 *
 * @author roberthood
 */
public class SonicDisruptorEffect
extends SpaceEffect
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	static final int DISRUPTOR_TRANSPARENCY = 100;
	
	SpaceObject parentObject;		//owner of this effect, which provides coordinates and facing angle for drawing.
	
	public static final int DISRUPTOR_ARC_MEASURE = 30;			//degrees of drawn sonic disruptor arc.
	public static final int disruptorColorBandWidth = 7;			//how big are the color bands of the disruptor arc?
	public static final int BASE_SONIC_DISRUPTOR_RANGE = 200;	//how far out does the base sonic disruptor reach?
	
	public static final Color[] colorPallete = 
			{	new Color(0x66, 0x00, 0xFF, DISRUPTOR_TRANSPARENCY), 
				new Color(0x66, 0x66, 0xFF, DISRUPTOR_TRANSPARENCY),  
				new Color(0x66, 0xFF, 0xFF, DISRUPTOR_TRANSPARENCY),  
				new Color(0x00, 0xFF, 0xFF, DISRUPTOR_TRANSPARENCY)	  };	//colors of the disruptor when drawn
	
	int offset = 0;
	int offsetRange = colorPallete.length * disruptorColorBandWidth;
	int ARC_SPEED = 3;
	
	public static final int SONIC_DISRUPTOR_MAX_COOLDOWN = PlayerShip.PLASMA_BOLT_MAX_COOLDOWN/2;	//double frequency as auto-shot
	public static final double DISRUPTOR_PULSE_DAMAGE = PlasmaBolt.DAMAGE_RATING * 2;				//same as two plasma bolts
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public SonicDisruptorEffect(  SpaceObject passedParentObject )
	{
		super(0.0, 0.0);							//location is derived from parent when drawing.
		parentObject = passedParentObject;			//remember which object this effect is attached to. 
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	@Override	protected int getMaxExpiredCowndown()	{ return -1;	}		//cooldown is not applicable.
	@Override	protected BufferedImage getSimpleSprite()	{ return null; }	//no sprite for this effect
	@Override	protected double getMaxVelocity()	{ return 0; }				//velocity is not applicable.
	
	public int getMaxSonicDisruptorRange()			
	{
		if ( null == parentObject ) { return BASE_SONIC_DISRUPTOR_RANGE; }
		
		//else, calculate in the parent object's width.
		return BASE_SONIC_DISRUPTOR_RANGE + parentObject.getSpatialRadius();
	} //end 
		
	
	@Override
	public void paintObject( Graphics g )
	{
		//first check that we have a parent. If not, we cannot draw anything since we lack information.
		if ( null == parentObject ) { return; }	
		Graphics2D g2d = (Graphics2D) g;		//convert reference for enhanced drawing capacity.
		
		int maxSonicDisruptorRange = getMaxSonicDisruptorRange();	//determine how far out the beam will be drawn.
		int colorPalleteIndex = 0;									//start at the first color
	
		//now start drawing disruptor arcs, going through the color pallete and back around, out to the max range.
		//increment by ring width + 1 to avoid color overlap.
		for ( int currentRange = 0;  currentRange <= maxSonicDisruptorRange ;   currentRange += (disruptorColorBandWidth+1) )
		{
			Color currentColor = colorPallete[colorPalleteIndex];			//pick this arc's color form the palette
			colorPalleteIndex += 1;											//advance the color pallete choice for next time
			colorPalleteIndex %= colorPallete.length;						//wrap after reaching end.
			
			int correctedRange = currentRange + offset;						//adjust the arc for frame offset
			if ( correctedRange < 0 ) { continue; }							//don't bother for this one
			if ( correctedRange > maxSonicDisruptorRange ) { break; } 		//signals that we're done.
			
			drawDisruptorArc( g2d, correctedRange, currentColor );			//draw the arc

		} //end for loop drawing arcs
		
		offset += ARC_SPEED;									//advance the frame offset
		offset %= offsetRange;									//wrap after reaching limit.
		
	} //end method paintObject
	
	/**
	 * Checks to see if a spaceobject has been caught in the sonic disruptor field.
	 * Calculates theta and distance, taking into consideration the parent object of this effect.
	 */
	public boolean checkSonicDisruptorImpact( SpaceObject objectToCheck )
	{
		//both theta angle and range must be correct for an impact to occur. 
		//start gathering information about each object to do calculations.
		int parentXCoordinate = parentObject.getxCoordinateAsInt();
		int parentYCoordinate = parentObject.getyCoordinateAsInt();
		int targetXCoordinate = objectToCheck.getxCoordinateAsInt();
		int targetYCoordinate = objectToCheck.getyCoordinateAsInt();
		int disruptorRange = getMaxSonicDisruptorRange();
		int disruptorAngle = parentObject.getFacingAngleDegrees();
		
		int deltaX = parentXCoordinate - targetXCoordinate;
		int deltaY = parentYCoordinate - targetYCoordinate;
		
		//first check the distance. If the object is not in distance, then it cannot be affected.
		long disruptorRangeSquared = disruptorRange * disruptorRange;
		long distanceSquared = ( deltaX * deltaX ) + (deltaY * deltaY );
		if ( distanceSquared > disruptorRangeSquared ) { return false; } //cannot be in range if distance is too far.
		
		//now, check the theta
		int rawObjectAngle = (int) Math.toDegrees(Math.atan2(deltaY, deltaX)) - 90;		//adjust for game board discrepency.
		int objectAngle = ThetaCorrector.correctThetaRange(rawObjectAngle);
		
		//for there to be an impact, the objectAngle has to fall between facingAngle +- (disruptorArc/2)
		int angleDifference = Math.abs( disruptorAngle - objectAngle );
		
		boolean verdict = ( angleDifference < (SonicDisruptorEffect.DISRUPTOR_ARC_MEASURE/2) );
		DebugManager.logMessage(6,	"Parent: " + parentXCoordinate + "," + parentYCoordinate + 
									"Target: " + targetXCoordinate + "," + targetYCoordinate + 
									"\tDistanceSquared: " + distanceSquared + " \tObjectAngle: " + objectAngle  + 
									"\tFiringAngle: " + disruptorAngle);
		return verdict;
	} //end method checkSonicDisruptorImpact
	
	/**
	 * Make checkCollision defer to checkSonicDisruptorImpact.
	 * @param objectToCheck
	 * @return 
	 */
	@Override public boolean checkCollision( SpaceObject objectToCheck ) { return checkSonicDisruptorImpact ( objectToCheck ); }
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/**
	 * Adjusts the angle of the drawing arc, since the game board has 0 degrees at top and runs clockwise,
	 * and drawArc has 0 degrees to the right, and runs counter-clockwise.
	 * @param facingAngle
	 * @return 
	 */
	private int thetaCorrection ( int facingAngle )
	{
		int facingAngleAdjustedForArc = facingAngle + ( DISRUPTOR_ARC_MEASURE / 2 );
		return 90 - facingAngleAdjustedForArc;
	}
	
	private void drawDisruptorArc ( Graphics2D g2d, int howBig, Color whatColor )
	{
		g2d.setColor(whatColor);
		
		int parentXCoordinate = parentObject.getxCoordinateAsInt();
		int parentYCoordinate = parentObject.getyCoordinateAsInt();
		int xUpperLeft = parentXCoordinate - howBig;
		int yUpperLeft = parentYCoordinate - howBig;
		int boxSize = 2 * howBig;
		int coneDirectionAngle = thetaCorrection( parentObject.getFacingAngleDegrees() );
		
		g2d.setStroke( new BasicStroke( (float) disruptorColorBandWidth));
		g2d.drawArc(	xUpperLeft, 
						yUpperLeft, 
						boxSize, 
						boxSize, 
						coneDirectionAngle, 
						DISRUPTOR_ARC_MEASURE);
	} //end method drawDisruptorArc
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} //end effect SonicDisruptorEffect
