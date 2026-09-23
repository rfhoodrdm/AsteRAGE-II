

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gui.Image;
import com.rfhoodrdm.asterage2.utility.RandomizedNumbers;

public class TrollMiningPod
extends TrollBaseShip
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	public static final int TROLL_POD_SPATIAL_RADIUS = 20;
	public static final double TROLL_POD_MAX_VELOCITY = 5;
	public static final double TROLL_POD_MAX_SHIELD_STRENGTH = PlasmaBolt.DAMAGE_RATING * 2;		//very fragile.
	public static final int PLASMA_BOLT_MAX_COOLDOWN = TrollScoutShip.PLASMA_BOLT_MAX_COOLDOWN;		//not used, currently
	
	//Limited life span for this type of ship
	public static final int TROLL_POD_MAX_LIFESPAN = GameConstants.FRAMES_PER_SECOND * 6;	//stay on the screen for 8 seconds.
	private int lifeSpan;
	private boolean podDiedOfDamage = false;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public TrollMiningPod( double pXCoord, double pYCoord )
	{
		super ( pXCoord, pYCoord, 0, TROLL_POD_SPATIAL_RADIUS );
		checkMovePatternRandomEvasive();							//start moving in a random direction.
		setFacingAngleDegrees( getMovementAngleDegrees() );			//face the same way we are moving.
		
		//set up limited life span of troll mining pods.
		lifeSpan = TROLL_POD_MAX_LIFESPAN;
		podDiedOfDamage = false;
	} //end constructor

	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	@Override	protected BufferedImage getSimpleSprite()	{	return Image.A2_TROLL_MINING_POD.getImage();	} //end method getSimpleSprite
	@Override	protected double getMaxVelocity()	{	return TROLL_POD_MAX_VELOCITY;	}
	@Override	public double getBaseMaxShieldStrength()	{	return TROLL_POD_MAX_SHIELD_STRENGTH;	}
	@Override	public boolean checkPlasmaBoltsCoolingDown()	{	return true;	/*Hack. Troll mining pods never shoot lasers. */}
	@Override	public PowerupOptionList getShipSpecificPowerUpOptionList()	{	return new PowerupOptionList();	/* no upgrades for this craft. */	}
	@Override	public int getMaxPlasmaBoltCooldown()	{	return PLASMA_BOLT_MAX_COOLDOWN;	}

	/**
	 * Determine if the mining pod was damaged, or died from old age.
	 * @return 
	 */
	public boolean checkPodDiedOfDamage()
	{
		return podDiedOfDamage;
	} //end method checkPodDiedOfDamage

	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	@Override
	public void paintObject( Graphics g )
	{
		Graphics2D g2d = (Graphics2D) g;		//cast to Graphics2D for enhanced drawing capability.
		
		paintEngineThrust( g2d );				//paint the engine thrust bubble.
		shieldEffect.paintObject(g);			//paint the shield effect, if appropriate.
		super.paintObject(g);					//now call to super to paint the icon as normal onto the screen.
	} //end method paintObject
	
	private void paintEngineThrust ( Graphics2D g2d )
	{
		int thrusterDistance = TROLL_POD_SPATIAL_RADIUS;
		double xOffset = thrusterDistance * Math.sin( Math.toRadians(getFacingAngleDegrees()) * -1);
		double yOffset = thrusterDistance * Math.cos( Math.toRadians(getFacingAngleDegrees()) );
		
		int thrusterXCoordinate = (int) Math.floor( getxCoordinate() + xOffset );
		int thrusterYCoordinate = (int) Math.floor( getyCoordinate() + yOffset );
		
		g2d.setColor( getRandomThrustColor() );
		int thrustSize = 10;
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
	
	/**
	 * Keep track of lifespan when moving.
	 * @param boardWidth
	 * @param boardHeight
	 * @param gravityNetActive 
	 */
	@Override
	public void moveAndRotate(int boardWidth, int boardHeight, boolean gravityNetActive)
	{
		super.moveAndRotate(boardWidth, boardHeight, gravityNetActive); 
		decrementLifespan();
	} //end method moveAndRotate
	
	private void decrementLifespan()
	{
		//take one life off of the lifespan. If we're at 0 or less, 
		lifeSpan -= 1;
		if ( lifeSpan <= 0 )
		{
			setExpiredFlag(true);
		} //end if check for expired pod.
	} //end method decrementLifeSpan
	
	/**
	 * Take damage as normal, but if we receive lethal damage, we need to remember it.
	 * @param damageAmount 
	 */
	@Override
	public void takeDamage(double damageAmount)
	{
		super.takeDamage(damageAmount); //To change body of generated methods, choose Tools | Templates.
		if (this.getShieldStrength() <= 0 )
		{
			podDiedOfDamage = true;
		} //end if check for lethal damage.
	} //end method takeDamage


	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */

	
	
	
} // end class TrollMiningPod definition 
