

package com.rfhoodrdm.asterage2.objectBehaviors.asterage2;

import com.rfhoodrdm.asterage2.gameEffects.asterage2.SonicDisruptorEffect;

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
