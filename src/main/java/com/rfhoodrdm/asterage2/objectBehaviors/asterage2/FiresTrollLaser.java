/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.objectBehaviors.asterage2;

/**
 * Interface for objects that fire Troll lasers.
 */
public interface FiresTrollLaser
{
	/**
	 * see if the ship has a troll laser currently equipped.
	 * @return 
	 */
	public boolean checkTrollLaserEquipped();		
	
	/**
	 * see if the troll laser is still cooling down ( and thus unable to fire )
	 * @return 
	 */
	public boolean checkTrollLaserCoolingDown();	
	
	/**
	 * sets the troll laser weapon cooldown to its max.
	 */
	public void resetTrollLaserCooldown();		
	
	/**
	 * Take one tick off of the troll laser cooldown for this ship.
	 */
	public void decrementTrollLaserCooldown();
	
	/**
	 * Get the max cooldown for this vessel after firing the troll laser weapon.
	 * @return 
	 */
	public int getMaxTrollLaserCooldown();
	
	
} //end interface FiresTrollLaser
