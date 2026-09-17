/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import com.rfhoodrdm.asterage2.gui.Image;

import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.gui.GUI;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.Expires;

/**
 *
 * @author roberthood
 */
public class PlasmaBolt
extends SpaceObject
implements Expires
{
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	PlasmaBoltType plasmaBoltType;			//which type of plasma bolt is this? Player, or enemy?
	
	//movement data members.
	public static final double MAX_PLASMA_BOLT_VELOCITY = 30.0;
	public static final int PLASMA_BOLT_SPATIAL_RADIUS = 8;
	
	public static final double MAX_PLAYER_PLASMA_BOLT_RANGE = 600.0;
	public static final double MAX_TROLL_PLASMA_BOLT_RANGE = 1200.00;
	private double traveledDistance;
	
	boolean expired;			//is plasma bolt still "alive" or dead?
	
	public static final double DAMAGE_RATING = 15.0;		//how much damage will this inflict on impact?
	
	public static final int MULTISHOT_ARC_VARIATION = 10;	//multishots are a few degrees off of center, in either direction
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	private PlasmaBolt ( double passedxCoordinate, double passedyCoordinate, int firingDirection, PlasmaBoltType whatType)
	{
		super ( passedxCoordinate, passedyCoordinate, PLASMA_BOLT_SPATIAL_RADIUS  );			//call to super with location and size.
		setMovementAngleDegrees(firingDirection);				//move and face in the same direction
		setFacingAngleDegrees(firingDirection);					
		plasmaBoltType = whatType;								//remember what bullet type this is?
		setMovementVelocity(MAX_PLASMA_BOLT_VELOCITY);			//set traveling speed.
		traveledDistance = 0;									//start out at 0 distance
		setExpiredFlag(false);									//hasn't expired yet.
	} //end constructor
	
	public static PlasmaBolt createPlayerPlasmaBolt ( double passedxCoordinate, double passedyCoordinate, int firingDirection )
	{
		return new PlasmaBolt(passedxCoordinate, passedyCoordinate, firingDirection, PlasmaBoltType.PLAYER );
	} //end factory method to create a player plasmabolt.
	
	public static PlasmaBolt createTrollPlasmaBolt ( double passedxCoordinate, double passedyCoordinate, int firingDirection )
	{
		return new PlasmaBolt(passedxCoordinate, passedyCoordinate, firingDirection, PlasmaBoltType.ENEMY );
	} //end factory method to create a troll plasmabolt
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	@Override	protected double getMaxVelocity()	{ return MAX_PLASMA_BOLT_VELOCITY;	}
	public PlasmaBoltType getPlasmaBoltType() { return this.plasmaBoltType; }
	@Override	
	protected BufferedImage getSimpleSprite()	
	{ 
		return (plasmaBoltType == PlasmaBoltType.PLAYER) ? 
				Image.A2_PLASMA_BOLT_PLAYER.getImage() : Image.A2_PLASMA_BOLT_ENEMY.getImage(); 
	}	//end method getSimpleSprite
	
	public boolean checkExpired() { return this.expired; }
	public void setExpiredFlag( boolean newFlag ) { this.expired = newFlag; }
	
	@Override	
	protected boolean checkAffectedByGravityNet() 
	{ 
		//player plasma bolts are not affected, but enemy plasma bolts are.
		PlasmaBoltType whatType = getPlasmaBoltType();
		switch ( whatType )
		{
			case PLAYER:
				return false;
				
			case ENEMY:
			default:
				return true;
		} //end switch based on plasma bolt type
	} //end method affectedByGravity Net
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	/**
	 * Plasma bolt needs to track how far it has moved so it can expire at proper time.
	 * @param boardWidth
	 * @param boardHeight 
	 */
	@Override
	protected void moveObject ( int boardWidth, int boardHeight, boolean gravityNetActive )
	{
		super.moveObject(boardWidth, boardHeight, gravityNetActive);		//call to super, to handle move like other objects
		
		//add the distance we've traveled to our total. If we've gone over the max, then the plasma bolt has expired.
		traveledDistance += getMovementVelocity();
		
		double rangeThreshold = (PlasmaBoltType.PLAYER == getPlasmaBoltType() ) ?
				MAX_PLAYER_PLASMA_BOLT_RANGE : MAX_TROLL_PLASMA_BOLT_RANGE;
		if ( traveledDistance > rangeThreshold ) { setExpiredFlag(true); }
	} //end method moveObject
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	/**
	 * Enum denoting the owner of the plasma bolt in question: the player or the enemies.
	 */
	public static enum PlasmaBoltType
	{
		PLAYER,
		ENEMY;
	} //end enum PlasmaBoltType definition
} //end class PlasmaBolt definition
