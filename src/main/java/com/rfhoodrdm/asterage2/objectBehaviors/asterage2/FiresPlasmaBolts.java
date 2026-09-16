/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.objectBehaviors.asterage2;

/**
 * Interface for objects that fire plasma bolts.
 */
public interface FiresPlasmaBolts
{
	/**
	 * Marks the object as firing bolts if the cooldown condition is met.
	 * @param currentlyFiring 
	 */
	public void setPlasmaBoltFiringState( boolean currentlyFiring );
	public boolean getPlasmaBoltFiringState();
	
	/**
	 * Checks to see if the cooldown is ready to fire a plasma bolt or not.
	 * @return 
	 */
	public boolean checkPlasmaBoltsCoolingDown();
	
	/**
	 * Sets cooldown to 0.
	 */
	public void resetPlasmaBoltCooldown();
	
	/**
	 * Sets the cooldown for plasma bolts to max.
	 */
	public void startPlasmaBoltCooldown();
	
	/**
	 * Takes a tick off of the cooldown for the plasma bolt weapon.
	 */
	public void decrementPlasmaBoltCooldown();
	
	/**
	 * Get the current multishot system level of the ship in question.
	 */
	public int getMultiShotLevel();
	public void setMultiShotLevel ( int pMultishotLevel );
} //end interface FiresPlasmaBolts
