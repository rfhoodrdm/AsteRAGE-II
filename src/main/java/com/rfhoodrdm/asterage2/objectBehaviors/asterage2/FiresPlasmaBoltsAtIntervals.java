/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.objectBehaviors.asterage2;

/**
 * This interface is an extension of the basic FiresPlasmaBolts interface.
 * It includes methods that deal with ships that always fire in set intervals.
 */
public interface FiresPlasmaBoltsAtIntervals
extends FiresPlasmaBolts
{
	public int getMaxPlasmaBoltCooldown();			//each vessel determines its own max cooldown.
	
	public void randomizePlasmaBoltCooldown();	//set a semi-random amount of time left on plasma bolt cooldown.
} //end interface definition FiresPlasmaBoltsAtIntervals
