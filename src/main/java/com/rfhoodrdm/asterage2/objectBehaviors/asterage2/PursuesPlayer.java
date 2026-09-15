/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package objectBehaviors.asterage2;

import gameObjects.asterage2.PlayerShip;

/**
 *
 * @author roberthood
 */
public interface PursuesPlayer
{
	
	public void setPursuitTarget( PlayerShip pTarget );
	public PlayerShip getPursuitTarget();

} //end interface PursuesPlayer definition
