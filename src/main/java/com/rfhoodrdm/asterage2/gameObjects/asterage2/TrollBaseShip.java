/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import lombok.extern.slf4j.Slf4j;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameEffects.asterage2.ShieldEffect;
import java.util.ArrayList;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.ControlsWeaponsPods;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.DeploysShields;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.FiresPlasmaBoltsAtIntervals;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.FiresTrollLaser;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.PursuesPlayer;

import static com.rfhoodrdm.asterage2.state.Asterage2State.POINT_AWARDS.*;

/**
 *
 * @author roberthood
 */



@Slf4j
public abstract class TrollBaseShip
extends SpaceObject
implements FiresPlasmaBoltsAtIntervals, DeploysShields, FiresTrollLaser
{

	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	private boolean expired;											//is ship still alive or dead?
	
	private double shieldStrength;										//how much life does the troll have remaining?
	public static final double SHIELD_REGEN_BASE_RATE = 0.05;			//how much shielding is restored per tick?
	public static final double SHIELD_GENERATOR_SYSTEM_BONUS = 0.025;	//how much extra shielding is restored for shield generator system bonus?
	protected int shieldReinforcementLevel;								//level of extra max shielding.
	protected ShieldEffect shieldEffect;								//effect object that draws shields.
	protected int shieldGeneratorLevel;									//level of increased shield regeneration
	public final double SHIELD_REINFORCEMENT_BONUS = 0.5;				//how much of an extra set power bonus do we get per level?
	
	//weapons flags and data members.
	private boolean firingPlasmaBolts;					//will ship fire plasma bolts if ready?
	private int plasmaBoltCooldownRemaining;			//how much cooldown left on plasma bolt before firing again?
	
	
	
	private boolean trollLaserEquipped;				//is this ship equipped with a troll laser
	private int trollLaserCooldownRemaining;		//how much cooldown left on troll laser weapon?
	public static int TROLL_LASER_MAX_COOLDOWN = 
			GameConstants.FRAMES_PER_SECOND * 10;
	
	//movement data members
	public static final int MAX_RANDOM_EVASIVE_PERIOD 
			= GameConstants.FRAMES_PER_SECOND * 10;				//10 second period max.
	private int evasiveRemainingCooldown = 0;					//how much cooldown is left?
	protected int speedBoostLevel;								//enhancement level to max speed stat.
	public static final double SPEED_BOOST_MULTIPLIER = 0.25;	//how much of a boost over standard is each enhancement level?
	
	private int multiShotLevel;			//what level of multi shot system upgrade does this ship have available? 
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */

	public TrollBaseShip( double xCoordinate, double yCoordinate, int passedUpgradeLevel, int pTrollSpatialRadius )
	{
		super ( xCoordinate, yCoordinate, pTrollSpatialRadius );	//call to super with location of spawn, and size.
		
		initializeTrollSystems( passedUpgradeLevel );
	} //end constructor
	
	private void initializeTrollSystems( int passedUpgradeLevel )
	{
		//initialize ship system state.
		resetPlasmaBoltCooldown();						//start at a state of max cooldown so we don't fire at once when spawned.
		multiShotLevel = 0;								//assume no multishot system unless specifically equipped on initialization.
		
		shieldEffect = new ShieldEffect( this );		//make a new shield effect for this object.
		shieldGeneratorLevel = 0;						//assume no extra regeneration.
		shieldReinforcementLevel = 0;					//assume no extra shielding.
		
		//assume that the laser weapon isn't equipped unless specifically stated otherwise in the special system initialization.
		//still set the cooldown at max from the getgo, since we don't want the troll ship firing right off the bat.
		resetTrollLaserCooldown();
		trollLaserEquipped = false;
		
		//initialize the troll weapons pod data structure, if this is such a ship.
		//we must do this here to avoid a null pointer, since the data structure is not initialized before call to super.
		if ( this instanceof ControlsWeaponsPods ) { ((ControlsWeaponsPods)this).initializeWeaponsPodList(); }
		
		initializeSpecialTrollSystems( passedUpgradeLevel );
		
		setShieldStrength( getMaxShieldStrength() );	//set shields to max. Must do this AFTER determining reinforcement level.
	} //end method initializeTrollSystems
	
	abstract public PowerupOptionList getShipSpecificPowerUpOptionList();
	
	private void initializeSpecialTrollSystems( int pUpgradeLevel )
	{	
		//initialize weapon systems.
		//determine number and extend of power-up systems.
		PowerupOptionList selectedPowerups = new PowerupOptionList();
		PowerupOptionList availablePowerups = getShipSpecificPowerUpOptionList();
		int numberPowerupsToSelect = pUpgradeLevel;
		
		while ( numberPowerupsToSelect > 0	&&	availablePowerups.size() > 0 )
		{
			//pick a power-up at random off of the available list, and add it to the selected power ups.
			int selectedIndex = (int) Math.floor( Math.random() * availablePowerups.size() );
			Troll_Ship_PowerUp_Option selectedOption = availablePowerups.remove(selectedIndex);
			selectedPowerups.add(selectedOption);
			
			numberPowerupsToSelect -= 1;	//decrement one from the number we should pick. 
		}	//end while loop 
		
		//now, iterate through our list of selected powerups, and enable/enhance those systems on the ship.
		for ( Troll_Ship_PowerUp_Option currentOption: selectedPowerups )
		{
			equipOrEnableShipSystem ( currentOption );
		} //end for loop iterating through activation of selected power ups.
	} //end method initializeSpecialTrollSystems
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	public long getPointValueHit()		{ return TROLL_BASIC_SHIP_HIT.getPointAward(); }
	public long getPointValueDestroy()	{ return TROLL_BASIC_SHIP_DESTROYED.getPointAward(); }
	public boolean checkExpired() { return this.expired; }
	public void setExpiredFlag( boolean newFlag ) { this.expired = newFlag; }
	@Override	protected boolean checkAffectedByGravityNet() { return false; }	 //troll ships are never affected by gravity net
	
	public abstract int getMaxPlasmaBoltCooldown();		//get the max cooldown for each type of vessel.
	@Override	public boolean getPlasmaBoltFiringState() { return firingPlasmaBolts; }
	@Override	public void setPlasmaBoltFiringState(boolean firingPlasmaBoltsToggle )	{	firingPlasmaBolts = firingPlasmaBoltsToggle;	}
	@Override	public boolean checkPlasmaBoltsCoolingDown()	{ return (plasmaBoltCooldownRemaining > 0);	}
	@Override	public void resetPlasmaBoltCooldown()	{ plasmaBoltCooldownRemaining = 0;	}
	@Override	public void startPlasmaBoltCooldown()	{	plasmaBoltCooldownRemaining = getMaxPlasmaBoltCooldown();	}
	@Override	public int getMultiShotLevel() { return this.multiShotLevel; }
	@Override	public void setMultiShotLevel( int pMultiShotLevel ) { this.multiShotLevel = pMultiShotLevel; }
	@Override
	public void decrementPlasmaBoltCooldown()
	{
		//decrement, but don't go below 0.
		plasmaBoltCooldownRemaining -= 1;
		if ( plasmaBoltCooldownRemaining < 0 ) { plasmaBoltCooldownRemaining = 0; }
	} //end method decrementCooldown
	@Override
	public void randomizePlasmaBoltCooldown()
	{
		//between 100% and 200%
		int maxPlasmaBoltCooldown = getMaxPlasmaBoltCooldown();
		int semiRandomCooldown = maxPlasmaBoltCooldown + (int) Math.floor( Math.random() * maxPlasmaBoltCooldown );
		plasmaBoltCooldownRemaining = semiRandomCooldown;
	} //end method randomizePlasmaBoltCooldown
	


	public abstract double getBaseMaxShieldStrength();
	public double getMaxShieldStrength() 
	{ 
		//base shield level, plus one extra set of bonus points for each reinforcement level.
		return getBaseMaxShieldStrength() * (1.0 + (shieldReinforcementLevel * SHIELD_REINFORCEMENT_BONUS)); 
	}
	@Override	public double getShieldStrength()	{ return this.shieldStrength;	}
	@Override	public void setShieldStrength( double newShieldAmount ) { this.shieldStrength = newShieldAmount; }
	public int getShieldGeneratorLevel()	{	return shieldGeneratorLevel;	}
	@Override
	public void regenerateShields()
	{
		int shieldGeneratorSystemLevel = getShieldGeneratorLevel();
		double newShieldStrength = shieldStrength + SHIELD_REGEN_BASE_RATE + 
				(shieldGeneratorSystemLevel * SHIELD_GENERATOR_SYSTEM_BONUS);
		if ( newShieldStrength > getMaxShieldStrength() ) { newShieldStrength = getMaxShieldStrength(); }	//don't go over max.
		setShieldStrength ( newShieldStrength );
	} //end method regenerateShields
	
	@Override
	public void takeDamage(double damageAmount)	
	{
		//record the amount of damage to current shields.
		double shieldTotal = getShieldStrength();
		shieldTotal -= damageAmount;
		setShieldStrength(shieldTotal);
		displayShieldEffect( getRemainingShieldPercentage() );
		
		//check to see if the ship has been destroyed. If so, mark it as expired.
		if ( 0.0 >= shieldTotal )
		{
			setExpiredFlag(true);
		} //end if check for no shields remaining
		
	} //end method takeDamage
	
	@Override
	public void displayShieldEffect(int shieldStrengthPercentage)
	{
		shieldEffect.displayShieldStrength( shieldStrengthPercentage );
	} //end method displayShieldEffect

	@Override
	public int getRemainingShieldPercentage()
	{
		double remainingShields = getShieldStrength();
		int remainingPercentage = (int) Math.floor( 100 * remainingShields / getMaxShieldStrength() );
		return remainingPercentage;
	} //end method getRemainingShieldPercentage
	
	
	@Override public boolean checkTrollLaserEquipped() { return trollLaserEquipped; }		
	@Override public boolean checkTrollLaserCoolingDown() {	return (trollLaserCooldownRemaining > 0); }
	@Override public void resetTrollLaserCooldown() { this.trollLaserCooldownRemaining = getMaxTrollLaserCooldown(); }
	@Override public int getMaxTrollLaserCooldown() { return TROLL_LASER_MAX_COOLDOWN; }
	
	@Override 
	public void decrementTrollLaserCooldown()
	{
		//Decrement the cooldown if greater than 0.
		trollLaserCooldownRemaining = ( trollLaserCooldownRemaining > 0 ) ?
					(trollLaserCooldownRemaining - 1)	:	0;
	} //end method decrementTrollLaserCooldown
	
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/**
	 * For ships set to random movement patterns, check to see if a change in direction is indicated. 
	 * If so, change the direction and speed of the ship, and reset the cooldown.
	 */
	protected void checkMovePatternRandomEvasive()
	{
		//decrement the countdown timer without going below 0.
		evasiveRemainingCooldown -= 1;
		if ( 0 > evasiveRemainingCooldown ) { evasiveRemainingCooldown = 0; }
		
		//if the countdown timer is at 0, randomize our speed and direction,
		//then reset the countdown timer for an interval between half max and max seconds.
		if ( 0 == evasiveRemainingCooldown )
		{
			randomizeStartingVelocityAndDirection();
			evasiveRemainingCooldown =	(int) Math.floor( Math.random() * (MAX_RANDOM_EVASIVE_PERIOD/2) ) +
										(MAX_RANDOM_EVASIVE_PERIOD/2);
		} //end if check for time to shift move pattern
	}  //end method checkMovePatternRandomEvasive
	
	
	/**
	 * For ships set to pursuit move pattern: Set the direction to move towards the player.
	 * Set velocity to move the ship if we are more than a threshold distance away, else don't move at all.
	 */
	protected void checkMovePatternPursuesPlayer()
	{
		//check for being a type that pursues a target. If not, then halt movement and return.
		if ( !(this instanceof PursuesPlayer) ) 
		{ 
			setMovementVelocity(0.0);
			return; 
		} //end if check for not being a pursuer.
		
		//first, we only move if the target is alive. Check to see if the player has expired.
		//if player expired, don't move--set velocity to 0.
		//Also, if we are already within the threshold distance of the player ship target, 
		//then we don't move either 
		PlayerShip pursuitTarget = ((PursuesPlayer) this).getPursuitTarget();
		if (	null == pursuitTarget ||
				false == pursuitTarget.checkShipInPlay() ||
				checkCollision(pursuitTarget)	)
		{
			setMovementVelocity(0.0);
			return;
		} //end if check for conditions in which we stop moving.
		
		
		//else, set the velocity to max and determine the direction in which the player is closest.
		setMovementVelocity( getMaxVelocity() );
		double deltaX = pursuitTarget.getxCoordinate() - getxCoordinate();
		double deltaY = pursuitTarget.getyCoordinate() - getyCoordinate();
		int movementAngle = (int) Math.floor( Math.toDegrees( Math.atan2(deltaX, -1 * deltaY))); 
		setMovementAngleDegrees(movementAngle);
		
	} //end method checkMovePatternPursuesPlayer
	
	
	protected void equipOrEnableShipSystem ( Troll_Ship_PowerUp_Option optionToEnable )
	{	
		switch ( optionToEnable )
		{
			case FAST_MOVEMENT:
				speedBoostLevel += 1;
				break;
				
			case SHIELD_REINFORCEMENT:
				shieldReinforcementLevel += 1;
				break;
				
			case SHIELD_REGEN_BOOST:
				shieldGeneratorLevel += 1;
				break;
				
			case TROLL_LASER_EQUIPPED:
				trollLaserEquipped = true;
				break;
				
			case MULTISHOT:
				multiShotLevel += 1;
				break;
				
			case WEAPON_POD:
				attachNewWeaponPod();
				break;
				
			default:
				log.error("Cannot enable troll ship system: {}. Not recognized.", optionToEnable);
				break;
		} //end switch based on which ship system we want to enable.
	} //end method equipOrEnableShipSystem
	
	/**
	 * Spawn a new weapon pod accessory for this ship, then 
	 */
	private void attachNewWeaponPod()
	{
		if ( !(this instanceof ControlsWeaponsPods) ) 
		{ 
			//incorrect type of ship to attach a weapon pod
			log.warn("Cannot attach a weapon pod to a ship that doesn't control them.");
			return;
		} //end if check for type
		
		//make a new weapon pod and attach it now.
		ControlsWeaponsPods podController = (ControlsWeaponsPods) this;
		TrollWeaponPod newPod = new TrollWeaponPod(podController);			//pod sends signal to attach to controller
		
	} //end method attachNewWeaponPod

	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */

	/**
	 * Enumeration of possible powered up systems that troll ships might have. Each class of troll ship will assemble 
	 */
	public static enum Troll_Ship_PowerUp_Option
	{
		FAST_MOVEMENT,
		SHIELD_REINFORCEMENT,
		SHIELD_REGEN_BOOST,
		TROLL_LASER_EQUIPPED,
		MULTISHOT,
		WEAPON_POD;
	} //end enum Troll_Ship_PowerUp_Option definition
	
	/**
	 * Concurrency safe collection of power ups.
	 * Used for constructing lists of possibilities to choose from, and also for holding active selections.
	 */
	public static class PowerupOptionList
	extends ArrayList<Troll_Ship_PowerUp_Option>
	{
		//basically same as Concurrent Linked Queue for now, but abstracted for if we need to change it.
		public PowerupOptionList()
		{
			super();
			
		}
	} //end class Powerup_Option_List definition
	
	
} //end class TrollBaseShip definition
