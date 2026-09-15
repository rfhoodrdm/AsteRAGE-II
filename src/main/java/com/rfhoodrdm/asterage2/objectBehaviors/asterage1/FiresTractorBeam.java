package objectBehaviors.asterage1;

import gameObjects.asterage1.SpaceObject;

/**
 * This interface presents all of the functions for a spaceobject that employs the troll tractor beam.
 * @author roberthood
 */
public interface FiresTractorBeam
{
	/**
	 * Checks to see if the tractor beam is ready to fire based on cooldown and any other factors.
	 * @return 
	 */
	public boolean checkTractorReadiness();
	
	/**
	 * Decreases the tractor beam cooldown by one tick.
	 */
	public void decrementTractorCoolDown();
	
	/**
	 * Completely finishes off the tractor beam cooldown.
	 */
	public void finishTractorCoolDown();
	
	/**
	 * Resets the tractor beam cooldown to maximum.
	 */
	public void restartTractorCooldown();
	
	/**
	 * Engage the tractor beam! Fire at either the player ship or the newly created pod.
	 * @param target
	 */
	public void fireTractor( SpaceObject target );
	
	/**
	 * Turns the tractor beam off.
	 */
	public void disengageTractor();
	
	/**
	 * Tractor beam reachs out a little further as time goes on.
	 * @param incrementAmount 
	 */
	public void incrementTractorRange ( );
	
	/**
	 * Tractor beam has reached its target and starts to affect it, in whatever way the functionality dictates.
	 * @param target 
	 */
	public void lockOnTractorBeam ( SpaceObject target );
	
	public SpaceObject getTractorTarget();
	
	public double getCurrentTractorRange();
} //end interface FiresTractorBeam definition.
