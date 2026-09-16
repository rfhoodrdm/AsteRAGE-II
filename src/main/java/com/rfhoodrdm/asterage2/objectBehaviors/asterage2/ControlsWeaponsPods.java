/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.objectBehaviors.asterage2;

import com.rfhoodrdm.asterage2.gameObjects.asterage2.TrollWeaponPod;
import java.awt.Point;
import java.util.List;

/**
 *
 * @author roberthood
 */
public interface ControlsWeaponsPods
{
	public boolean checkExpired();		//check to see if the parent has died or not.
	
	public void attachWeaponPod( TrollWeaponPod newWeaponPod );			//register and attach a new weapon pod.
	public void detachDeadWeaponPod( TrollWeaponPod deadWeaponPod );	//de-register old weapon pod that died.
	public Point getOrbitCoordinates( int podIndex );					//query the owner of this weapon pod for current expected location.	
	
	public int getMaxWeaponPodCount();									//how many pods at most?
	
	public List<TrollWeaponPod> getAttachedWeaponPods();				//gets all attached weapons pods
	
	public void freeAllPodsUponDeath();									//signal all weapons pods that they are now free.
	
	public void initializeWeaponsPodList();								//prepare to receive new weapons pods.
} //end interface ControlsWeaponsPods
