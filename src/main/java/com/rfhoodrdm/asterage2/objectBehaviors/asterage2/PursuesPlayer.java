/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.objectBehaviors.asterage2;

import com.rfhoodrdm.asterage2.gameObjects.asterage2.PlayerShip;

/**
 *
 * @author roberthood
 */
public interface PursuesPlayer
{
	
	public void setPursuitTarget( PlayerShip pTarget );
	public PlayerShip getPursuitTarget();

} //end interface PursuesPlayer definition
