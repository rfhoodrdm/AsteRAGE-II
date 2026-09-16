/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.Expires;
import com.rfhoodrdm.asterage2.gui.GUI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import com.rfhoodrdm.asterage2.utility.GameConstants;
import com.rfhoodrdm.asterage2.utility.RandomizedNumbers;
import com.rfhoodrdm.asterage2.utility.ThetaCorrector;

/**
 *
 * @author roberthood
 */
public class HomingMissile
extends SpaceObject
implements Expires
{
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final double MAX_HOMING_MISSILE_VELOCITY = 40.0;			//max missile velocity should be quite high.
	public static final double IDLE_HOMING_MISSILE_VELOCITY = 7.0;			//how fast when not tracking a target
	public static final double HOMING_MISSILE_TRACKING_ACCELERATION = 0.05;	//how fast to speed towards the target.
	
	public static final double MAX_IDLE_DISTANCE = GameConstants.FRAMES_PER_SECOND * IDLE_HOMING_MISSILE_VELOCITY;		//how far to idle travel before exploding
	private double idleDistance;		//how far have we idle traveled so far?
	
	public static final double HOMING_MISSILE_DAMAGE_RATING = PlasmaBolt.DAMAGE_RATING;	//same damage as plasma bolt
	
	public static final int HOMING_MISSILE_SPATIAL_RADIUS = 23;		//how big is the homing missile on the game board?
	
	
	private MissileType missileType;			//what type of missile? Player's or enemy's?
	private TrollBaseShip currentTarget;		//what is this missile targetting?
	private boolean expiredFlag;				//expired flag
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public HomingMissile( SpaceObject parentObject, MissileType whatType, TrollBaseShip passedTarget )
	{
		//remember initial position, what type, and what we're targetting.
		super(parentObject.getxCoordinate(), parentObject.getyCoordinate(), HOMING_MISSILE_SPATIAL_RADIUS );
		this.missileType = whatType;
		currentTarget = passedTarget;
		
		//set the movement and facing angle to be the same as the parent, just in case we lose our target.
		int initialAngle = parentObject.getFacingAngleDegrees();
		setFacingAngleDegrees( initialAngle );
		setMovementAngleDegrees(initialAngle);
		setMovementVelocity(IDLE_HOMING_MISSILE_VELOCITY);
		
		//set idling distance initially to 0.
		idleDistance = 0.0;
		
		expiredFlag = false;
	} //end constructor
	
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	@Override	protected boolean checkAffectedByGravityNet()	{ return false;	}
	@Override	protected double getMaxVelocity()	{	return MAX_HOMING_MISSILE_VELOCITY;	}
	@Override	public boolean checkExpired()	{	return expiredFlag;	}
	@Override	public void setExpiredFlag(boolean newFlag)	{	expiredFlag = newFlag;	}
	@Override	
	protected BufferedImage getSimpleSprite()	
	{	
		//return the missile sprite corresponding to the missile type.
		if ( MissileType.PLAYER == getMissileType() )
		{
			return GUI.Image.A2_HOMING_MISSILE_PLAYER.getImage();
		}
		else
		{
			return GUI.Image.A2_HOMING_MISSILE_TROLL.getImage();
		}
	} //end method getSimpleSprite
	
	public MissileType getMissileType() { return this.missileType; }
	public SpaceObject getTarget() { return this.currentTarget; }
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	@Override
	public void paintObject ( Graphics g )
	{
		//paint the engine thrust of the missile.
		Graphics2D g2d = (Graphics2D) g;
		paintEngineThrust(g2d);
		
		//lastly, call to super's paint method.
		super.paintObject(g);
	} //end method paintObject
	
	private void paintEngineThrust ( Graphics2D g2d )
	{
		int thrusterDistance = 20;
		double xOffset = thrusterDistance * Math.sin( Math.toRadians(getFacingAngleDegrees()) * -1);
		double yOffset = thrusterDistance * Math.cos( Math.toRadians(getFacingAngleDegrees()) );
		
		int thrusterXCoordinate = (int) Math.floor( getxCoordinate() + xOffset );
		int thrusterYCoordinate = (int) Math.floor( getyCoordinate() + yOffset );
		
		g2d.setColor( getRandomThrustColor() );
		int thrustSize = 15;
		g2d.fillOval(	thrusterXCoordinate - (thrustSize /2 ), 
						thrusterYCoordinate - (thrustSize /2 ), 
						thrustSize, 
						thrustSize);
	} //end method paintEngineThrust
	
	private Color getRandomThrustColor ()
	{
		int randomColorChance = RandomizedNumbers.random100();
		
		if ( randomColorChance < 20 ) return Color.YELLOW;
		if ( randomColorChance < 40 ) return Color.ORANGE;
		if ( randomColorChance < 60 ) return Color.WHITE;
		if ( randomColorChance < 80 ) return Color.PINK;
		
		return Color.RED; 
	} //end method getRandomThrustColor
	
	@Override
	public void moveAndRotate(int boardWidth, int boardHeight, boolean gravityNetActive)
	{
		//check to see if we're idle moving due to a lack of a target.
		if (	null == currentTarget  ||
				currentTarget.checkExpired() )
		{
			//add the amount of our current velocity to our idle distance, and then idle drift.
			idleDistance += getMovementVelocity();
			super.moveAndRotate(boardWidth, boardHeight, gravityNetActive);
		} //end if check for not tracking an object
		else
		{
			//move according to our tracking algorithm.
			moveAndRotateByTracking( boardWidth, boardHeight, gravityNetActive);
		}
		
		//check if idle drift exceeds our max allowance. If so, mark this homing missile as expired
		if ( idleDistance >= MAX_IDLE_DISTANCE )
		{
			setExpiredFlag(true);
		} //end if check for expired homing missile due to idle drift.
	} //end method moveAndRotate
	
	private void moveAndRotateByTracking( int boardWidth, int boardHeight, boolean gravityNetActive )
	{
		//turn to face the target being tracked.
		int targetXCoordinate = computeTargetXCoordinate( boardWidth );
		int targetYCoordinate = computeTargetYCoordinate( boardHeight );
		
		double deltaX = targetXCoordinate - getxCoordinate() ;
		double deltaY = targetYCoordinate - getyCoordinate();
		int targetAngle = ThetaCorrector.correctThetaRange(90 + (int) Math.floor(Math.toDegrees(Math.atan2( deltaY , deltaX ))));
		
		setMovementAngleDegrees(targetAngle);
		setFacingAngleDegrees(targetAngle);
		
		//accelerate slightly.
		double currentAcceleration = getMovementAcceleration();
		setMovementAcceleration( currentAcceleration + HOMING_MISSILE_TRACKING_ACCELERATION);
		
		//move towards the target.
		super.moveAndRotate(boardWidth, boardHeight, gravityNetActive);
	} //end method moveAndRotateByTracking

	
	private int computeTargetXCoordinate( int boardWidth )
	{
		//if there is more than a half screen difference between the missile and target, project off of the left or right
		//of the screen, as appropriate.
		//Else just return the target's x coordinate.
		int targetRawXCoordinate = currentTarget.getxCoordinateAsInt();
		int missileXCoordinate = getxCoordinateAsInt();
		
		int computedXCoordinate = targetRawXCoordinate;			//go with the actual coordinate, unless...
		int threshold = (boardWidth/2);
		
		if ( Math.abs(targetRawXCoordinate - missileXCoordinate) > threshold )
		{
			computedXCoordinate = ( targetRawXCoordinate < threshold ) ?
					(targetRawXCoordinate + boardWidth) : (targetRawXCoordinate - boardWidth);
		} //end if check for greater than half a screen difference
		
		return computedXCoordinate;
		
	} //end method computeTargetXCoordinate
	
	private int computeTargetYCoordinate( int boardHeight )
	{
		//if there is more than a half screen difference between the missile and target, project off of the left or right
		//of the screen, as appropriate.
		//Else just return the target's x coordinate.
		int targetRawYCoordinate = currentTarget.getyCoordinateAsInt();
		int missileYCoordinate = getyCoordinateAsInt();
		
		int computedYCoordinate = targetRawYCoordinate;			//go with the actual coordinate, unless...
		int threshold = (boardHeight/2);
		
		if ( Math.abs(targetRawYCoordinate - missileYCoordinate) > threshold )
		{
			computedYCoordinate = ( targetRawYCoordinate < threshold ) ?
					(targetRawYCoordinate + boardHeight) : (targetRawYCoordinate - boardHeight);
		} //end if check for greater than half a screen difference
		
		return computedYCoordinate;
		
	} //end method computeTargetYCoordinate
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum MissileType
	{
		PLAYER,
		TROLL;
	} //end MISSILE_TYPE definition
	
} //end class HomingMissile
