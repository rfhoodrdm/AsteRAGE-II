/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utility;

/**
 *
 * @author roberthood
 */
public class RandomizedNumbers
{
	/**
	 * Generates a random number from 0 to 99.
	 * @return 
	 */
	public static int random100()
	{
		return (int) Math.round ( Math.random() * 100 );
	} //end method random100
	
	/**
	 * Generates a random number from 0 to 9999
	 * @return 
	 */
	public static int random10000()
	{
		return (int) Math.round ( Math.random() * 10000 );
	}
} //end class RandomizedNumbers
