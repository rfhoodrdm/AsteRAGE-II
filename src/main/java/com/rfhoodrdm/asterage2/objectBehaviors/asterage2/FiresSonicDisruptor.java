/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package objectBehaviors.asterage2;

import gameEffects.asterage2.SonicDisruptorEffect;

/**
 *
 * @author roberthood
 */
public interface FiresSonicDisruptor
{
	public void setFiringSonicDisruptor( boolean firingDisruptorFlag );		//turn the sonic disruptor on or off
	public boolean checkFiringSonicDisruptor ();							//see if the sonic disruptor is on or off.
	
	public SonicDisruptorEffect getSonicDisruptor();						//gets the sonic disruptor assigned to this object.
	
	public int getSonicDisruptorCooldown();								//what is the remaining time left on the sonic disruptor pulse?
	public boolean checkSonicDisruptorCoolingDown();						//is the sonic disruptor 
	public void resetSonicDisruptorCooldown();								//reset cooldown to maximum
	public void decrementSonicDisruptorCooldown();							//subtract one from the sonic disruptor pulse
	
} //end interface FiresSonicDisruptor definition
