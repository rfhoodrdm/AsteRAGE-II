package objectBehaviors.asterage1;
import gameObjects.asterage1.SpaceObject;
import java.util.concurrent.ConcurrentLinkedQueue;
import gameEffects.asterage1.ShieldRing;

/**
 * Presents all methods involving interacting with an object's shields.
 * @author roberthood
 */
public interface DeploysShields
{
	/**
	 * Trigger the object's shields to regenerate by a predetermined amount, if applicable.
	 */
	public void regenerateShields ();
	
	/**
	 * Assigns a given amount of damage to the object's shields. Needs the spaceObjectList in case the object dies, but also
	 * to create a shield effect.
	 * @param amountOfDamage Double value representing the amount of damage taken by the shields.
	 * @param spaceObjectList List of currently active space objects.
	 */
	public void damageShields ( double amountOfDamage, ConcurrentLinkedQueue<SpaceObject> spaceObjectList );
	
	/**
	 * Anything that can take shield damage can be destroyed.
	 * @param spaceObjectList 
	 */
	public void killObject ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList );
	
	public double getCurrentShields();
	public double getMaxShields();
	
	/**
	 * Does the object currently have a shield effect attach.
	 * @return 
	 */
	public boolean hasShieldsAttached();
	
	/**
	 * Set the current shield effect of this object.
	 * @param shieldRing 
	 */
	public void attachShields ( ShieldRing shieldRing );
	
	/**
	 * Remove the object's attachment to the shield effect.
	 */
	public void detachShields ();
	
	
	/**
	 * Shields were hit again. Renew the life span.
	 */
	public void renewShieldEffect ();
	
	
	/**
	 * Tell the space object to remove any associated shield effect from the list, so it won't continue to be drawn.
	 * @param spaceObjectList 
	 */
	//public void removeShieldEffect ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList );
	
	/**
	 * Attach the shield to the space object in question, and add it to the list of objects.
	 * This is so the shield effect can travel with the object.
	 * @param spaceObjectList 
	 */
	//public void attachShieldEffect ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList );
	
	//public void detachShieldEffect ( 
}
