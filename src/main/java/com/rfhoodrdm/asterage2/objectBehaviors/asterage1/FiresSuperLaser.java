package objectBehaviors.asterage1;
import gameObjects.asterage1.PlayerShip;
import gameObjects.asterage1.TrollMothership;
import gameObjects.asterage1.SpaceObject;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This interface presents all of the functions related to a spaceobject that shoots the troll super laser.
 * @author roberthood
 */
public interface FiresSuperLaser
{
	/**
	 * Checks to see if the laser is ready to fire. True = yes, false = no.
	 */
	public boolean checkLaserCooldown();
	
	/**
	 * Reduce the current cooldown of the laser by 1 game tick.
	 */
	public void decrementLaserCooldown();
	
	/**
	 * Reduce the current cooldown of the laser to 0.
	 */
	public void finishLaserCooldown();
	
	/**
	 * Resets the laser cooldown to its maximum.
	 */
	public void restartLaserCooldown();
	
	/**
	 * Fires the laser, attaching it to the attacker and targeting the player ship, and also adds
	 * the troll laser to the game object list.
	 * @param playerShip
	 * @param spaceObjectList 
	 */
	public void fireLaser( SpaceObject target, 
							ConcurrentLinkedQueue< SpaceObject> spaceObjectList );
} //end interface FiresSuperLaser definition
