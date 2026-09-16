/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import static com.rfhoodrdm.asterage2.gameObjects.asterage2.TrollScoutShip.TROLL_SCOUT_SPATIAL_RADIUS;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.ControlsWeaponsPods;
import com.rfhoodrdm.asterage2.utility.GameConstants;
import com.rfhoodrdm.asterage2.utility.ThetaCorrector;

/**
 *
 * @author roberthood
 */
public class TrollWeaponPod
extends TrollScoutShip
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	private ControlsWeaponsPods parentShip = null;				//which ship owns this pod?
	
	private int podIndex;		//what number pod is this out of the group?
	
	public static final int POD_SPATIAL_RADIUS = TrollScoutShip.TROLL_SCOUT_SPATIAL_RADIUS;
	
	public static final int PLASMA_BOLT_MAX_COOLDOWN 
			= GameConstants.FRAMES_PER_SECOND * 4;				//one volley per 4 second.
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public TrollWeaponPod( ControlsWeaponsPods pParentShip )
	{
		super(0);	//call super to set up other attributes.
		this.parentShip = pParentShip;
		pParentShip.attachWeaponPod(this);
		setRotationalVelocity(-1 * TROLL_SCOUT_ROTATIONAL_VELOCITY);	//rate of sprite rotation on game board, opposite direction as normal
		randomizePlasmaBoltCooldown();									//start shooting at a semi-randomized interval
	} //end constructor
	
	@Override
	public PowerupOptionList getShipSpecificPowerUpOptionList( )
	{
		//troll pods do not get power ups.
		return new PowerupOptionList();
	} //end method getShipSpecificPowerUpOptionList
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	public ControlsWeaponsPods getParentShip()	{	return parentShip;	}
	public void setParentShip(ControlsWeaponsPods parentShip)	{	this.parentShip = parentShip;	}
	public void freePodFromParent() { parentShip = null; }
	public void setWeaponPodIndex( int pIndex ) { this.podIndex = pIndex; }
	@Override	public int getMaxPlasmaBoltCooldown()	{ return PLASMA_BOLT_MAX_COOLDOWN; }
	
	@Override
	public void moveAndRotate( int boardWidth, int boardHeight, boolean gravityNetActive )
	{
		//if the parent is alive, then the pod moves to orbit the mothership.
		//if not, then the ship moves as a regular troll scout ship.
		if (	null == parentShip ||
				parentShip.checkExpired() )
		{
			super.moveAndRotate(boardWidth, boardHeight, gravityNetActive);
		} //end if check for still living mothership parent.
		else
		{
			checkMoveOrbitMothership(boardWidth, boardHeight, gravityNetActive);
			rotateObject();	//don't forget to spin.
		}
	} //end method moveAndRotate
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/**
	 * Poll the mothership for the right location and move towards that point.
	 * Parent ship reference must NOT be null.
	 */
	private void checkMoveOrbitMothership( int boardWidth, int boardHeight, boolean gravityNetActive )
	{
		//we already know at this point that the owning ship is not null or dead. 
		//Poll for expected location, and move towards that point. 
		Point expectedOrbitLocation = parentShip.getOrbitCoordinates( this.podIndex );
		
		setxCoordinateWithBoundsCorrection(expectedOrbitLocation.getX(), boardWidth);
		setyCoordinateWithBoundsCorrection(expectedOrbitLocation.getY(), boardHeight);
		
	} //end method checkMoveOrbitMothership
	
	/**
	 * Custom troll ship painting.
	 * @param g 
	 */
	@Override
	public void paintObject ( Graphics g )
	{
		Graphics2D g2d = (Graphics2D) g;		//convert to graphics2d object for more capability.
		
		
		int trollHullShipSize = (int) Math.floor(TROLL_SCOUT_SPATIAL_RADIUS * 0.66);
		
		
		//calculate the position and paint for the troll ship hull.
		int xCoordinate = getxCoordinateAsInt() - TROLL_SCOUT_SPATIAL_RADIUS;
		int yCoordinate = getyCoordinateAsInt() - trollHullShipSize;
		int width = getSpatialRadius() * 2;
		int height = trollHullShipSize * 2;
		
		Color lightColor = new Color( 0x33, 0x00, 0xFF);
		Color darkColor = new Color( 0x66, 0x33, 0x99);
		GradientPaint TROLL_SCOUT_HULL_GRADIENT  
			= new GradientPaint(	xCoordinate,
									yCoordinate,
									darkColor, 
									xCoordinate,
									yCoordinate + height,
									lightColor);
		g2d.setPaint(TROLL_SCOUT_HULL_GRADIENT);
		g2d.fillOval(	xCoordinate, 
						yCoordinate, 
						width, 
						height);
		
		//draw the "scanning arc of the ship."
		g2d.setPaint( Color.WHITE );
		g2d.fillArc(xCoordinate, yCoordinate, width, height, getFacingAngleDegrees(), 15);
		g2d.fillArc(xCoordinate, yCoordinate, width, height, ThetaCorrector.correctThetaRange(getFacingAngleDegrees() + 180), 15);
		
		//draw the 'dome' of the ship.
		int trollDomeWidth = (int) Math.floor(TROLL_SCOUT_SPATIAL_RADIUS * 0.33);
		int trollDomeHeight = (int) Math.floor(TROLL_SCOUT_SPATIAL_RADIUS * 0.22);
		int domeLeftSide = getxCoordinateAsInt() - trollDomeWidth;
		int domeTop = getyCoordinateAsInt() - trollDomeHeight;

		Color podDomeColor = new Color(0x33, 0x00, 0x33);
		g2d.setPaint(podDomeColor);
		g2d.fillOval(domeLeftSide, domeTop, trollDomeWidth * 2, trollDomeHeight * 2);

		
		//let the shield effect draw itself, if needed.
		shieldEffect.paintObject(g);
	} //end method paintObject
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */

	
} //end class TrollWeaponPod
