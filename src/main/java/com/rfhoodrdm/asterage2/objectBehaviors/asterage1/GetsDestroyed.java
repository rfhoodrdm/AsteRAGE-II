package com.rfhoodrdm.asterage2.objectBehaviors.asterage1;

import com.rfhoodrdm.asterage2.gameObjects.asterage1.SpaceObject;
import com.rfhoodrdm.asterage2.state.asterage1.Asterage1ScoreState;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Ensures that any object presenting this interface has a method to handle its death.
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
