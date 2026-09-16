/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameEffects.asterage2;

import com.rfhoodrdm.asterage2.gameObjects.asterage2.SpaceObject;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.Expires;
import com.rfhoodrdm.asterage2.utility.GameConstants;

/**
 * Base class of space effects: objects that are drawn on the game board that have limited life spans.
 * @author roberthood
 */
public abstract class SpaceEffect
extends SpaceObject
implements Expires
{

	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	int expiredCountdownTimer = 0;			//how many ticks until this effect expires?
	boolean expired;						//is this effect expired?

	public static final int NO_SPATIAL_RADIUS = 0;	//spatial radius of 0 for objects that don't have size on the board.
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public SpaceEffect(double passedXCoordinate, double passedYCoordinate)
	{
		super(passedXCoordinate, passedYCoordinate, NO_SPATIAL_RADIUS);		//call to super with location.
		expired = true;														//assume we don't draw unless directed to do so.
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	@Override	public boolean checkExpired() { return expired; }
	@Override	public void setExpiredFlag(boolean newFlag)	{ this.expired = newFlag;	}
	@Override	protected boolean checkAffectedByGravityNet() { return false; }	 //space effects are never affected by gravity net
	
	abstract protected int getMaxExpiredCowndown();
	public int getExpiredCountdownTimer () { return this.expiredCountdownTimer; }
	public void setExpiredCountdownTimer ( int newCountDown ) { this.expiredCountdownTimer = newCountDown; }
	public void resetCoundownToMax()		
	{		
		setExpiredCountdownTimer( getMaxExpiredCowndown() );
		setExpiredFlag(false);
	} //end method resetCountdownToMax
	
	public void decrementExpiredCountdownTimer()
	{
		//if the time remaining is above 0, take off a tick. Then, set the expired flag to true if we've reached 0.
		expiredCountdownTimer = ( expiredCountdownTimer > 0 ) ?
						(expiredCountdownTimer - 1) : 0;
		
		if ( 0 >= expiredCountdownTimer ) { setExpiredFlag(true); }
	} //end method decrementExpiredCountdownTimer
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	
	
	
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
} //end class SpaceEffect definition
