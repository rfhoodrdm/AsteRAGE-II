package objectBehaviors.asterage1;

import gameObjects.asterage1.SpaceObject;
import java.util.concurrent.ConcurrentLinkedQueue;
import state.Asterage1ScoreState;

/**
 * Ensures that any object presenting this interface has a method to handle its death.
 * @author roberthood
 */
public interface GetsDestroyed
{
	/**
	 * Remove the space object from the list, thus ending its existence.
	 * Shares the method name with interface LimitedLifespan.
	 * @param spaceObjectList The list of space objects currently active in the game.
	 */
	public void killObject (	ConcurrentLinkedQueue<SpaceObject> spaceObjectList,
								Asterage1ScoreState scoreState );
}
