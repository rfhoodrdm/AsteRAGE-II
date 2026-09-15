/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package objectBehaviors.asterage2;

/**
 *
 * @author roberthood
 */
public interface DeploysShields
{
	public void takeDamage( double damageAmount );			//take away some shield strength
	
	public double getShieldStrength();						//how much shields does the object have left?
	
	public void setShieldStrength( double newStrength );	//set the shields to a given amount
	
	public void regenerateShields();						//add standard shield regeneration amount for frame tick
	
	public void displayShieldEffect( int shieldStrength );	//how strong should the shields be rendered to show?
	
	public int getRemainingShieldPercentage();				//get shield strength in terms of 0-100 integer.
	
} //end method deploysShields
