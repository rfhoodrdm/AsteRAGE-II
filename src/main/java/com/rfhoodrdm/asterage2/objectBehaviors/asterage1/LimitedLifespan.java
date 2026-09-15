package objectBehaviors.asterage1;

import java.util.concurrent.ConcurrentLinkedQueue;
import gameObjects.asterage1.SpaceObject;

/**
 * This interface presents all of the functions inherent to an object that only exists on the game board for a limited amount
 * of time, then vanishes.
 */
public interface LimitedLifespan
{
	/**
	 * Compares the current lifespan of the object to 0. If it is time for the object to die, returns true.
	 * @return Boolean value representing whether the object is as the end of its lifespan. True = yes, false = no.
	 */
	public boolean checkEndOfLifespan ();
	
	/**
	 * Decrement from the life span of the object
	 */
	public void ageObject();
	
	/**
	 * Prematurely set life span of object to 0.
	 */
	public void endLifespan ();
	
	/**
	 * Set the life span of the object back to its maximum.
	 */
	public void resetLifespan ();
	
	/**
	 * Remove the space object from the list, thus ending its existence.
	 * @param spaceObjectList The list of space objects currently active in the game.
	 */
	public void killObject ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList );
	
}
