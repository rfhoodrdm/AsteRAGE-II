
package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.Expires;
import com.rfhoodrdm.asterage2.state.asterage2.Asterage2State;



public abstract class PowerUpBaseObject
extends SpaceObject
implements Expires
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	public static final int POWER_UP_STANDARD_SPATIAL_RADIUS = 25;
	public static final int POWER_UP_ROTATION = 5;
	
	private boolean expiredFlag;							//has this power up expired?
	
	public static final int POWER_UP_SPAWN_CHANCE = 2;				//percent chance of power up spawning when asteroid dies.
	public static final int POWER_UP_SPECIAL_SYSTEM_CHANCE = 25;	//what percent of power up drops will be ship systems
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public PowerUpBaseObject(double passedXCoordinate, double passedYCoordinate)
	{
		super(passedXCoordinate, passedYCoordinate, POWER_UP_STANDARD_SPATIAL_RADIUS);
		setRotationalVelocity(POWER_UP_ROTATION);
		
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	@Override	protected double getMaxVelocity()	{ return Asteroid.MEDIUM_ASTEROID_MAX_SPEED; }
	@Override	protected boolean checkAffectedByGravityNet()	{ return true; } //power ups are slowed to make them easier to catch.
	@Override	public boolean checkExpired()	{ return this.expiredFlag; }
	@Override	public void setExpiredFlag(boolean newFlag)	{ this.expiredFlag = newFlag; }
	
	
	/**
	 * What to do when this power up is collected.
	 * @param asterage2State 
	 */
	abstract public void handlePickup( Asterage2State asterage2State );
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */


	
		
	
} //end class PowerUpBaseObject
