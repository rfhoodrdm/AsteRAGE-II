/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.rfhoodrdm.asterage2.constants.GameConstants;
import com.rfhoodrdm.asterage2.utility.ThetaCorrector;

/**
 *
 * @author roberthood
 */
public class TrollScoutShip
extends TrollBaseShip
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final double BASE_MAX_VELOCITY = 15.0;					//how fast can the ship move at max speed?
	public static final int TROLL_SCOUT_SPATIAL_RADIUS = 38;				//how far out officially does this object extend?
	
	public static final double BASE_MAX_SHIELD_STRENGTH = 100;				//how strong are shields at full?
	
	public static final int TROLL_SCOUT_ROTATIONAL_VELOCITY = 3;			//how fast is the ship spinning?
	
	public static final int PLASMA_BOLT_MAX_COOLDOWN 
			= GameConstants.FRAMES_PER_SECOND * 2;		//one bullets per 2 second.
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public TrollScoutShip( int passedUpgradeLevel)
	{
		super(0.0, 0.0, passedUpgradeLevel, TROLL_SCOUT_SPATIAL_RADIUS);	//location determined randomly for scout, 0.0 dummy values for each
		setRotationalVelocity(TROLL_SCOUT_ROTATIONAL_VELOCITY);				//rate of sprite rotation on game board.
		setRandomSpawnCoordinates();										//start scouts at random locations around the board edge.
	} //end constructor
	
	@Override
	public PowerupOptionList getShipSpecificPowerUpOptionList( )
	{
		return new TrollScoutPowerUpOptionList(); 
	} //end method getShipSpecificPowerUpOptionList
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override	public double getBaseMaxShieldStrength()	{ return BASE_MAX_SHIELD_STRENGTH; }
	@Override	public int getMaxPlasmaBoltCooldown()		{ return PLASMA_BOLT_MAX_COOLDOWN; }
	
	/**
	 * Custom troll ship painting.
	 * @param g 
	 */
	@Override
	public void paintObject ( Graphics g )
	{
		Graphics2D g2d = (Graphics2D) g;		//convert to graphics2d object for more capability.
		
		
		int trollHullShipSize = (int) Math.floor(TROLL_SCOUT_SPATIAL_RADIUS * 0.66);
		int trollDomeSize = (int) Math.floor(TROLL_SCOUT_SPATIAL_RADIUS * 0.5);
		
		//calculate the position and paint for the troll ship hull.
		int xCoordinate = getxCoordinateAsInt() - TROLL_SCOUT_SPATIAL_RADIUS;
		int yCoordinate = getyCoordinateAsInt() - trollHullShipSize;
		int width = getSpatialRadius() * 2;
		int height = trollHullShipSize * 2;
		
		GradientPaint TROLL_SCOUT_HULL_GRADIENT  
			= new GradientPaint(	xCoordinate,
									yCoordinate,
									Color.BLUE, 
									xCoordinate,
									yCoordinate + height,
									Color.CYAN);
		g2d.setPaint(TROLL_SCOUT_HULL_GRADIENT);
		g2d.fillOval(	xCoordinate, 
						yCoordinate, 
						width, 
						height);
		
		//draw the "scanning arc of the ship."
		g2d.setPaint( Color.WHITE );
		g2d.fillArc(xCoordinate, yCoordinate, width, height, getFacingAngleDegrees(), 15);
		g2d.fillArc(xCoordinate, yCoordinate, width, height, ThetaCorrector.correctThetaRange(getFacingAngleDegrees() + 180), 15);
		
		//draw the glass dome of the ship.
		int domeLeftSide = getxCoordinateAsInt() - trollDomeSize;
		int domeTop = getyCoordinateAsInt() - trollDomeSize - 00;
		int domeHorizon = getyCoordinateAsInt() - 10;
		GradientPaint TROLL_DOME_GRADIENT  
			= new GradientPaint(	domeLeftSide,
									domeTop,
									Color.WHITE, 
									domeLeftSide + trollDomeSize * 2,
									domeTop + trollDomeSize * 2,
									Color.DARK_GRAY);
		g2d.setPaint(TROLL_DOME_GRADIENT);
		g2d.fillArc (	domeLeftSide,
						domeTop,
						trollDomeSize * 2,
						trollDomeSize * 2,
						0,
						180 );
		g2d.fillArc(	domeLeftSide, 
						domeHorizon,
						trollDomeSize * 2,
						20, 
						180, 
						180);
		
		//let the shield effect draw itself, if needed.
		shieldEffect.paintObject(g);
	} //end method paintObject
	
	
	@Override
	public void moveAndRotate( int boardWidth, int boardHeight, boolean gravityNetActive )
	{
		//first check to see if we are going to change direction.
		checkMovePatternRandomEvasive();
		
		//then move normally
		super.moveAndRotate(boardWidth, boardHeight, gravityNetActive);
	} //end method moveAndRotate
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	@Override	protected BufferedImage getSimpleSprite()	{ return null; }	//no sprite, uses custom drawing.
	@Override	protected double getMaxVelocity()	
	{ 
		//base max speed, plus a percentage of max speed for every boost level this ship has.
		return BASE_MAX_VELOCITY + ( BASE_MAX_VELOCITY * SPEED_BOOST_MULTIPLIER * speedBoostLevel); 
	} //end method getMaxVelocity
	
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static class TrollScoutPowerUpOptionList
	extends PowerupOptionList
	{
		public TrollScoutPowerUpOptionList()
		{
			super();
			
			//add the power up options indicative of troll scouts
			add ( Troll_Ship_PowerUp_Option.FAST_MOVEMENT );
			add ( Troll_Ship_PowerUp_Option.FAST_MOVEMENT );
			add ( Troll_Ship_PowerUp_Option.SHIELD_REINFORCEMENT );
			add ( Troll_Ship_PowerUp_Option.SHIELD_REINFORCEMENT );
			add ( Troll_Ship_PowerUp_Option.SHIELD_REGEN_BOOST );
			add ( Troll_Ship_PowerUp_Option.SHIELD_REGEN_BOOST );
			add ( Troll_Ship_PowerUp_Option.TROLL_LASER_EQUIPPED );
		} //end constructor
	} //end class TrollScoutPowerUpOptionList
	
} //end class TrollScoutShip definition
