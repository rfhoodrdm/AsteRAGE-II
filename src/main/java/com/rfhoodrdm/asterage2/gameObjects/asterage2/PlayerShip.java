/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameEffects.asterage2.GravityNetEffect;
import com.rfhoodrdm.asterage2.gameEffects.asterage2.ShieldEffect;
import com.rfhoodrdm.asterage2.gameEffects.asterage2.SonicDisruptorEffect;
import com.rfhoodrdm.asterage2.gui.GUI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.DeploysShields;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.FiresPlasmaBolts;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.FiresSonicDisruptor;
import com.rfhoodrdm.asterage2.objectBehaviors.asterage2.TakesDamage;
import com.rfhoodrdm.asterage2.utility.RandomizedNumbers;

/**
 *
 * @author roberthood
 */
public class PlayerShip
extends SpaceObject
implements FiresPlasmaBolts, FiresSonicDisruptor, DeploysShields, TakesDamage
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	private double startingXCoordinate;			//where was the ship placed to start?
	private double startingYCoordinate;			//where was the ship placed to start?
	
	//movement flags and data members
	private boolean rotateClockwise;			//are we rotating clockwise?
	private boolean rotateCounterClockwise;		//or counter-clockwise?
	private boolean accelerating;				//speeding up?
	private boolean decelerating;				//slowing down
	
	public static final double MAX_VELOCITY = 20.0;						//how fast can the ship move at max speed?
	public static final int MAX_ROTATIONAL_VELOCITY = 8;				//how fast can the ship spin?
	public static final double FORWARD_ACCELERATION_RATE = 0.75;		//how fast can the ship speed up?
	public static final double BACKWARD_ACCELERATION_RATE = 0.05;		//how fast can the ship brake?
	public static final double DECELERATION_SYSTEM_BONUS = 0.05;		//how much extra deceleration bonus for that ship system?
	
	
	public static final int SHIP_SPATIAL_RADIUS = 38;		//how big is the ship on the game board.
	
	//weapons flags and data members.
	private boolean firingPlasmaBolts;					//will ship fire plasma bolts if ready?
	private boolean firingSonicDisruptor;				//is the sonic disruptor engaged?
	private int plasmaBoltCooldownRemaining;			//how much cooldown left on plasma bolt before firing again?
	public static final int PLASMA_BOLT_MAX_COOLDOWN 
			= GameConstants.FRAMES_PER_SECOND / 2;			//two bullets per second.
	
	
	private double shieldStrength;										//how much life does this ship have remaining?
	public static final double MAX_SHIELD_STRENGTH = 100;				//how strong are shields at full?
	public static final double SHIELD_REGEN_BASE_RATE = 0.05;			//how much shielding is restored per tick?
	public static final double SHIELD_GENERATOR_SYSTEM_BONUS = 0.025;	//how much extra shielding is restored for shield generator system bonus?
	private ShieldEffect shieldEffect;											//effect object that draws shields.
	
	private SonicDisruptorEffect sonicDisruptorEffect;							//sonic disruptor weapon effect.
	private int sonicDisruptorCoolDownRemaining;								//how much cooldown remains on sonic disruptor pulse.
	
	private GravityNetEffect gravityNetEffect;									//effect showing gravity net is working.
	
	private Ship_Status shipStatus;												//what is the current status of the ship?
	
	//ship's systems flags and stats
	private int homingMissileLevel;
	private int multiShotLevel;
	private boolean sonicDisruptorEquipped;
	
	private int decelerationLevel;
	private int shieldGeneratorLevel;
	private boolean gravityNetEquipped;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	private PlayerShip ( double passedXCoordinate, double passedYCoordinate )
	{
		super( passedXCoordinate, passedYCoordinate, SHIP_SPATIAL_RADIUS);		//first call to super with position, and size
		this.startingXCoordinate = passedXCoordinate;							//remember where the ship was initially placed.
		this.startingYCoordinate = passedYCoordinate;
	} //end constructor
	
	public static PlayerShip makeNewPlayerShip( double passedXCoordinate, double passedYCoordinate )
	{
		PlayerShip newShip = new PlayerShip( passedXCoordinate, passedYCoordinate );
		newShip.resetShipStats( true );						//reset attributes to starting standard.
		newShip.initializeShipSystems();					//set all ship systems to beginning state.
		newShip.setShipStatus( Ship_Status.UNSPAWNED );		//ship starts unspawned.
		return newShip;
	} //end factory method to create a new player ship
	
	
	public void resetShipStats( boolean restoreShieldStrength)
	{
		setxCoordinate(startingXCoordinate);		//reset position
		setyCoordinate(startingYCoordinate);
		
		rotateClockwise = false;					//set rotation to false.
		rotateCounterClockwise = false;
		
		setFacingAngleDegrees(0);					//face up and don't move
		setMovementVelocity(0);
		setRotationalVelocity(0);
		
		firingPlasmaBolts = false;					//not currently firing
		plasmaBoltCooldownRemaining = 0;			//ready to fire immediately
		
		if (restoreShieldStrength) { shieldStrength = MAX_SHIELD_STRENGTH; }		//shields to maximum if indicated
		shieldEffect = new ShieldEffect( this );	//create a new shield effect object to draw shields when needed
		
		sonicDisruptorEffect = new SonicDisruptorEffect(this);	//create a new sonic disruptor effect to display
		resetSonicDisruptorCooldown();							//set it to max cooldown at first.
		
		gravityNetEffect = new GravityNetEffect(this);			//create a new gravitynet effect
		
	} //end method resetShipStats
	
	private void initializeShipSystems()
	{
		//set ship's systems state:
		homingMissileLevel = 0;
		multiShotLevel = 0;
		sonicDisruptorEquipped = false;

		decelerationLevel = 0;
		shieldGeneratorLevel = 0;
		gravityNetEquipped = false;
	} //end method initializeShipSystems
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	public int getHomingMissileLevel()	{	return homingMissileLevel;	}
	public void setHomingMissileLevel(int homingMissileLevel)	{	this.homingMissileLevel = homingMissileLevel;	}
	public int getMultiShotLevel()	{	return multiShotLevel;	}
	public void setMultiShotLevel(int multiShotLevel)	{		this.multiShotLevel = multiShotLevel;	}
	public boolean checkSonicDisruptorEquipped()	{	return  sonicDisruptorEquipped;	}
	public int getDecelerationLevel()	{	return decelerationLevel;	}
	public void setDecelerationLevel(int decelerationLevel)	{	this.decelerationLevel = decelerationLevel;	}
	public int getShieldGeneratorLevel()	{	return shieldGeneratorLevel;	}
	public void setShieldGeneratorLevel(int shieldGeneratorLevel)	{	this.shieldGeneratorLevel = shieldGeneratorLevel;	}
	public boolean checkGravityNetEquipped()	{	return gravityNetEquipped;	}
	public void setGravityNetEquipped(boolean gravityNetEquipped)	{	this.gravityNetEquipped = gravityNetEquipped;	}
	
	@Override	public int getSonicDisruptorCooldown()			{	return sonicDisruptorCoolDownRemaining;	}
	@Override	public boolean checkSonicDisruptorCoolingDown()	{	return (sonicDisruptorCoolDownRemaining > 0);	}
	@Override	public void resetSonicDisruptorCooldown()		{	sonicDisruptorCoolDownRemaining = SonicDisruptorEffect.SONIC_DISRUPTOR_MAX_COOLDOWN;	}
	@Override	public SonicDisruptorEffect getSonicDisruptor()	{	return this.sonicDisruptorEffect;	}
	@Override	public void setFiringSonicDisruptor( boolean passedFiringDisruptorFlag ) { this.firingSonicDisruptor = passedFiringDisruptorFlag;	}	
	@Override	public boolean checkFiringSonicDisruptor () { return this.firingSonicDisruptor; }							
	@Override	public boolean getPlasmaBoltFiringState() { return firingPlasmaBolts; }
	@Override	public void setPlasmaBoltFiringState(boolean firingPlasmaBoltsToggle )	{	firingPlasmaBolts = firingPlasmaBoltsToggle;	}
	@Override	public boolean checkPlasmaBoltsCoolingDown()	{ return (plasmaBoltCooldownRemaining > 0);	}
	@Override	public void resetPlasmaBoltCooldown()	{ plasmaBoltCooldownRemaining = 0;	}
	@Override	public void startPlasmaBoltCooldown()	{	plasmaBoltCooldownRemaining = PLASMA_BOLT_MAX_COOLDOWN;	}
	@Override	protected BufferedImage getSimpleSprite()	{	return Image.A2_PLAYER_SHIP.getImage();	 } //end method getSimpleSprite
	@Override	protected double getMaxVelocity()	{	 return MAX_VELOCITY;	} 
	@Override	protected boolean checkAffectedByGravityNet() { return false; }	 //player ship is never affected by gravity net
	public void rotateClockwise ( boolean passedRotationToggle ) { this.rotateClockwise = passedRotationToggle; }
	public void rotateCounterClockwise ( boolean passedRotationToggle ) { this.rotateCounterClockwise = passedRotationToggle; }
	public void accelerate ( boolean passedAccelerationToggle ) { this.accelerating = passedAccelerationToggle; }
	public void decelerate ( boolean passedDecelerationToggle ) { this.decelerating = passedDecelerationToggle; }
	@Override	public void setShieldStrength( double newShieldAmount ) { this.shieldStrength = newShieldAmount; }
	@Override	public double getShieldStrength() { return this.shieldStrength; }
	@Override	public void regenerateShields()	
	{	
		//only regenerate if ship is in play
		if ( false == checkShipInPlay() ) { return; } //cannot regenerate shields if not on game board
		
		int shieldGeneratorSystemLevel = getShieldGeneratorLevel();
		double newShieldStrength = shieldStrength + SHIELD_REGEN_BASE_RATE + 
				(shieldGeneratorSystemLevel * SHIELD_GENERATOR_SYSTEM_BONUS);
		if ( newShieldStrength > MAX_SHIELD_STRENGTH ) { newShieldStrength = MAX_SHIELD_STRENGTH; }	//don't go over max.
		setShieldStrength ( newShieldStrength );
	} //end method regenerateShields
	
	@Override
	public int getRemainingShieldPercentage() 
	{
		double remainingShields = getShieldStrength();
		int remainingPercentage = (int) Math.floor( 100 * remainingShields / PlayerShip.MAX_SHIELD_STRENGTH );
		return remainingPercentage;
	} //end method getRemainingShieldPercentage
	
	public void setSonicDisruptorEquipped(boolean sonicDisruptorEquipped)	
	{	
		//reset the cooldown on the gravity net effect whenever we get a new system.
		this.sonicDisruptorEquipped = sonicDisruptorEquipped;	
		gravityNetEffect.resetCoundownToMax();
	} //end method setSonicDisruptorEquipped
	
	@Override	
	public void decrementSonicDisruptorCooldown()	
	{	
		//decrement, but don't go below 0.
		sonicDisruptorCoolDownRemaining -= 1;	
		if ( sonicDisruptorCoolDownRemaining < 0 ) { sonicDisruptorCoolDownRemaining = 0; }
	} //end method decrementSonicDisruptorCooldown
	
	@Override
	public void decrementPlasmaBoltCooldown()
	{
		//decrement, but don't go below 0.
		plasmaBoltCooldownRemaining -= 1;
		if ( plasmaBoltCooldownRemaining < 0 ) { plasmaBoltCooldownRemaining = 0; }
	} //end method decrementCooldown
	
	/**
	 * Ship rotates according to its rotational flags.
	 * @return 
	 */
	@Override	
	public int getRotationalVelocity() 
	{ 
		//take into account rotational flags. Keep in mind both buttons may be pressed at same time.
		int calculatedRotationVelocity = 0;
		if ( rotateClockwise )			{ calculatedRotationVelocity += MAX_ROTATIONAL_VELOCITY; }
		if ( rotateCounterClockwise )	{ calculatedRotationVelocity -= MAX_ROTATIONAL_VELOCITY; }
		
		return calculatedRotationVelocity;
	} //end method getRotationalVelocity
	
	@Override
	public double getMovementAcceleration ()
	{
		//check sum effect of thrusters on acceleration.
		double accelerationRate = 0.0;
		if ( true == accelerating )		
		{ 
			accelerationRate += FORWARD_ACCELERATION_RATE; 
		}
		if ( true == decelerating )		
		{ 
			double totalBackwardsDeceleration = BACKWARD_ACCELERATION_RATE + ( getDecelerationLevel() * DECELERATION_SYSTEM_BONUS);
			accelerationRate -= totalBackwardsDeceleration; 
		}
		
		return accelerationRate;
	} //end method getMovementAcceleration
	
	/**
	 * Override paintObject to do some custom painting.
	 * @param g 
	 */
	@Override
	public void paintObject ( Graphics g )
	{
		//first check to see if the ship is in play. If not, then don't draw it.
		if ( false == checkShipInPlay() ) { return; }
		
		Graphics2D g2d = (Graphics2D) g; //convert g since we need to rotate.
	
		//if ship is accelerating or decelerating, show engine thrust graphic.
		if ( accelerating || decelerating ) { paintEngineThrust(g2d); }
		
		//if the sonic disruptor is equipped and firing, then paint that object.
		if ( checkSonicDisruptorEquipped() && checkFiringSonicDisruptor() ) { sonicDisruptorEffect.paintObject(g); }
		
		//let the shield effect draw itself, if needed.
		shieldEffect.paintObject(g);
		
		//draw the gravity net effect, if that system is equipped.
		if ( checkGravityNetEquipped() ) { gravityNetEffect.paintObject(g); }
		
		//call to super to paint sprite over all.
		super.paintObject(g);
	} //end method paintObject
	
	@Override
	public void moveAndRotate ( int boardWidth, int boardHeight, boolean gravityNetActive )
	{
		//first check to see if the ship is in play before moving, rotating, or anything.
		if ( false == checkShipInPlay() ) { return; }	//return without doing anything.
		
		super.moveAndRotate(boardWidth, boardHeight, gravityNetActive);
	} //end method moveObject
	
	@Override
	public void takeDamage(double damageAmount)	
	{
		//record the amount of damage to current shields.
		double shieldTotal = getShieldStrength();
		shieldTotal -= damageAmount;
		setShieldStrength(shieldTotal);
		displayShieldEffect( getRemainingShieldPercentage() );
		
	} //end method takeDamage
	
	@Override
	public void displayShieldEffect(int shieldStrengthPercentage)
	{
		shieldEffect.displayShieldStrength( shieldStrengthPercentage );
	} //end method displayShieldEffect
	
	public void setShipStatus ( Ship_Status newStatus ) { this.shipStatus = newStatus; }
	public boolean checkShipInPlay () { return ( Ship_Status.IN_PLAY == shipStatus ); }
	public boolean checkShipIsDestroyed() { return( Ship_Status.EXPIRED == shipStatus); }
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	private void paintEngineThrust ( Graphics2D g2d )
	{
		int thrusterDistance = 40;
		double xOffset = thrusterDistance * Math.sin( Math.toRadians(getFacingAngleDegrees()) * -1);
		double yOffset = thrusterDistance * Math.cos( Math.toRadians(getFacingAngleDegrees()) );
		
		int thrusterXCoordinate = (int) Math.floor( getxCoordinate() + xOffset );
		int thrusterYCoordinate = (int) Math.floor( getyCoordinate() + yOffset );
		
		g2d.setColor( getRandomThrustColor() );
		int thrustSize = 20;
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
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
	public static enum Ship_Status
	{
		UNSPAWNED,			//ship that is not in play, but has not been destroyed. E.g. Warped Out at end of level.
		IN_PLAY,			//ship is showing on game board during regular play.
		EXPIRED;			//ship that has been destroyed
	} //end enum ship status definition
	
} //end class PlayerShip definition
