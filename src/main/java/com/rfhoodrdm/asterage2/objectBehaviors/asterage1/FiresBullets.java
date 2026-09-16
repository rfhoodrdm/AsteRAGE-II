package com.rfhoodrdm.asterage2.objectBehaviors.asterage1;

import com.rfhoodrdm.asterage2.gameObjects.asterage1.SpaceObject;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This interface presents all of the functions that an object has to implement in order to fire bullets on the game board.
 * 
 */
public interface FiresBullets
{
	/**
	 * Compares the weapon cooldown counter to 0, to see if weapons are ready to auto-fire.
	 * @return boolean value which represents whether the weapons are ready to fire. True = yes, false = no.
	 */
	public boolean checkBulletCoolDown();
	
	/**
	 * Reduces the cooldown timer by 1 game tick.
	 */
	public void decrementBulletCoolDown();
	
	/**
	 * sets the cooldown to 0.
	 */
	public void finishBulletCoolDown();
	
	/**
	 * Sets the cooldown to its maximum.
	 */
	public void restartBulletCoolDown();
	
	/**
	 * Fire the bullet!
	 * @param spaceObjectList The current list of active space objects. Needed to add a new bullet object to the list.
	 */
	public void fireBullet( ConcurrentLinkedQueue<SpaceObject> spaceObjectList );
	
} //end interface firesWeapons definition
