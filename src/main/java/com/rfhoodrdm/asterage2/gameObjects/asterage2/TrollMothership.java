
package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import lombok.extern.slf4j.Slf4j;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.ControlsWeaponsPods;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.PursuesPlayer;
import static com.rfhoodrdm.asterage2.state.Asterage2State.POINT_AWARDS.TROLL_MOTHERSHIP_DESTROYED;
import com.rfhoodrdm.asterage2.utility.ThetaCorrector;

@Slf4j
public class TrollMothership
extends TrollBaseShip
implements PursuesPlayer, ControlsWeaponsPods
{

	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final double BASE_MAX_VELOCITY = TrollScoutShip.BASE_MAX_VELOCITY / 3.0;
	public static final int TROLL_MOTHERSHIP_SPATIAL_RADIUS = 75;		//what is the size of the mothership.
	
	public static final double BASE_MAX_SHIELD_STRENGTH = 500;			//how strong are shields at full?
	
	public static final int PLASMA_BOLT_MAX_COOLDOWN 
			= GameConstants.FRAMES_PER_SECOND * 2;						//one volley per 3 second.
	
	
	private PlayerShip pursuitTarget = null;							//pursuit target for tracking behavior.
	
	public final static int TROLL_MOTHERSHIP_ROTATION_VELOCITY = 2;		//how fast the sprite rotates on the game board.
	
	private TrollWeaponPod[] weaponPodArray;	//weapons pod array initially empty.
	int weaponPodOrbitOffset;									//causes the pods to orbit around the mothership when incremented.
	public static final int ORBIT_ANGLE_INCREMENT_RATE = 1;		//rotate at 1 degree per movement frame.
	
	public static final int TROLL_MINING_SHIP_LEVEL_SPAWN_THRESHOLD = 25;		//what level does this ship start appearing?
	public static final int TROLL_MOTHERSHIP_LEVEL_SPAWN_FREQUENCY = 5;			//spawns once in this many levels.
	public static int TROLL_MINING_SHIP_RANDOM_SPAWN_THRESHOLD = 50;			//chance ( out of 10000) that a troll mining ship will spawn from an asteroid.
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public TrollMothership( int pUpgradeLevel )
	{
		super (0.0, 0.0, pUpgradeLevel, TROLL_MOTHERSHIP_SPATIAL_RADIUS );
		setRotationalVelocity(TROLL_MOTHERSHIP_ROTATION_VELOCITY);
		setRandomSpawnCoordinates();			//start mothership at random locations around the board edge.
		weaponPodOrbitOffset = 0;				//start the orbit increment at 0.
		randomizePlasmaBoltCooldown();			//start shooting at a semi-randomized interval
	} //end constructor
	
	@Override
	public PowerupOptionList getShipSpecificPowerUpOptionList( )
	{
		return new TrollMothershipPowerUpOptionList();
	} //end method getShipSpecificPowerUpOptionList
	
	@Override
	public void initializeWeaponsPodList()
	{
		weaponPodArray = new TrollWeaponPod[0];
	} //end method initializeWeaponsPodList
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

	@Override	protected BufferedImage getSimpleSprite()	{ return null; }	//no sprite, uses custom drawing.
	@Override	protected double getMaxVelocity()	{	return BASE_MAX_VELOCITY;	}
	@Override	public double getBaseMaxShieldStrength()	{ return BASE_MAX_SHIELD_STRENGTH; }
	@Override	public void setPursuitTarget(PlayerShip pTarget)	{	this.pursuitTarget = pTarget;	}
	@Override	public PlayerShip getPursuitTarget()	{	return this.pursuitTarget; }
	@Override	public int getMaxPlasmaBoltCooldown()	{ return PLASMA_BOLT_MAX_COOLDOWN; }
	@Override	public long getPointValueDestroy()	{ return TROLL_MOTHERSHIP_DESTROYED.getPointAward(); }
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	@Override
	public void paintObject ( Graphics g )
	{
		Graphics2D g2d = (Graphics2D) g;		//convert to graphics2d object for more capability.
		
		int shipSpatialRadius = getSpatialRadius();
		int trollHullShipSize = (int) Math.floor( shipSpatialRadius * 0.66);
		int trollDomeSize = (int) Math.floor(shipSpatialRadius * 0.5);
		
		//calculate the position and paint for the troll ship hull.
		int xCoordinate = getxCoordinateAsInt() - shipSpatialRadius;
		int yCoordinate = getyCoordinateAsInt() - trollHullShipSize;
		int width = getSpatialRadius() * 2;
		int height = trollHullShipSize * 2;
		
		
		Color lightColor = Color.BLUE;
		Color darkColor = new Color (50, 50, 100);
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
		checkMovePatternPursuesPlayer();
		
		//increment the orbit angle offset for weapons pods.
		incrementPodOrbitOffset();
		
		//then move normally. 
		super.moveAndRotate(boardWidth, boardHeight, gravityNetActive);
	} //end method moveAndRotate

	@Override
	public void attachWeaponPod(TrollWeaponPod newWeaponPod)
	{
		//go through our existing array, looking for null entries, or duplicate entries.
		for ( int index = 0;	index < weaponPodArray.length;  ++index )
		{
			if ( newWeaponPod == weaponPodArray[index])
			{
				log.warn("Will not attach weapon pod to a ship twice.");
				return;
			} //end if check for weapon pod already in the list.
		} //end for loop iterating through weapon pod array.
		
		//if we find none, create a new array to hold an extra pod reference, and set the last reference to the new pod.
		for ( int index = 0;	index < weaponPodArray.length;  ++index )
		{
			if ( null == weaponPodArray[index] )
			{
				//simply add the new pod to the empty slot.
				weaponPodArray[index] = newWeaponPod;
				newWeaponPod.setWeaponPodIndex(index);
				return;
			} //end if check for an empty slot.
		} //end for loop iterating through weapon pod array entries.
		
		//failing that, resize the array and add the weapon pod to the last slot.
		int oldLength = weaponPodArray.length;
		TrollWeaponPod[] newPodArray = new TrollWeaponPod[ oldLength + 1 ];
		for ( int index = 0;  index < oldLength;  ++index )
		{
			newPodArray[index] = weaponPodArray[index];
		} //end for loop iterating through array entries to copy over.
		//the old length is coincidentally also the index of the new pod in the new array.
		newPodArray[oldLength] = newWeaponPod;
		newWeaponPod.setWeaponPodIndex(oldLength);
		weaponPodArray = newPodArray;
	} //end method attachWeaponPod

	@Override
	public void detachDeadWeaponPod(TrollWeaponPod deadWeaponPod)
	{
		//go through the weapon pod array, looking for a matching entry. If found, then set its reference to null.
		for ( int index = 0;  index < weaponPodArray.length;  ++index )
		{
			if ( deadWeaponPod == weaponPodArray[index] )
			{
				//keep going and eliminate all entries. There should only be one, but no need to stop.
				//No resizing of array.
				weaponPodArray[index] = null;	
			} //end if check for a match.
		} //end for loop iterating through weapon pods
	} //end method detachDeadWeaponPod
	
	

	@Override	
	public Point getOrbitCoordinates(int podIndex)	
	{
		//calculate the coordinates, given the ship's current location, the orbit distance,
		//the index of the pod in question out of the total number of pods, and current offset.
		int orbitDistance = (int) Math.floor((TROLL_MOTHERSHIP_SPATIAL_RADIUS + TrollWeaponPod.POD_SPATIAL_RADIUS) ); //* 1.10 for slightly more than touching each other.
		double parentXCoord = getxCoordinate();
		double parentYCoord = getyCoordinate();
		
		int podSpacingOffset = ( 360 / getMaxWeaponPodCount() ) * podIndex;				//this is how far the pods are from each other.
		int totalOrbitAngle = podSpacingOffset + weaponPodOrbitOffset;
		double deltaX = orbitDistance * Math.sin(Math.toRadians(totalOrbitAngle));		//use sin to translate between planes.
		double deltaY = orbitDistance * Math.cos(Math.toRadians(totalOrbitAngle)) * -1;	//use -1 * cos to translate betweeb planes
		
		Point coordinates = new Point();
		coordinates.setLocation(parentXCoord + deltaX, parentYCoord + deltaY);
		return coordinates;
	} //end method getOrbitCoordinates
	
	public void incrementPodOrbitOffset()
	{
		weaponPodOrbitOffset = (weaponPodOrbitOffset + ORBIT_ANGLE_INCREMENT_RATE) % 360;
	} //end method incrementPodOrbitOffset

	@Override	public int getMaxWeaponPodCount()	{	return weaponPodArray.length;	}
	@Override	public List<TrollWeaponPod> getAttachedWeaponPods() { return Arrays.asList(weaponPodArray); }

	@Override
	public void freeAllPodsUponDeath()
	{
		//go through each entry in the set of weapons pods.
		//If the entry is not null, set the pod free.
		//Then set the entry to null so it is not referenced again.
		for ( int index = 0;    index < weaponPodArray.length;    ++ index )
		{
			if ( null != weaponPodArray[index] )
			{
				weaponPodArray[index].freePodFromParent();
			} //end if check for null
			weaponPodArray[index] = null;
		} //end for loop iterating through weapons pods.
	} //end method freeAllPodsUponDeath
	 
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static class TrollMothershipPowerUpOptionList
	extends PowerupOptionList
	{
		public TrollMothershipPowerUpOptionList()
		{
			super();
			
			//add the power up options indicative of troll scouts
			add ( Troll_Ship_PowerUp_Option.SHIELD_REINFORCEMENT );
			add ( Troll_Ship_PowerUp_Option.SHIELD_REINFORCEMENT );
			add ( Troll_Ship_PowerUp_Option.SHIELD_REGEN_BOOST );
			add ( Troll_Ship_PowerUp_Option.SHIELD_REGEN_BOOST );
			add ( Troll_Ship_PowerUp_Option.TROLL_LASER_EQUIPPED );
			add ( Troll_Ship_PowerUp_Option.MULTISHOT );
			add ( Troll_Ship_PowerUp_Option.MULTISHOT );
			add ( Troll_Ship_PowerUp_Option.WEAPON_POD );
			add ( Troll_Ship_PowerUp_Option.WEAPON_POD );
			add ( Troll_Ship_PowerUp_Option.WEAPON_POD );
			add ( Troll_Ship_PowerUp_Option.WEAPON_POD );
			
		} //end constructor
	} //end class TrollScoutPowerUpOptionList
} //end class TrollMothership definition
