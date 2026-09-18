package com.rfhoodrdm.asterage2.utility;

public class RandomizedNumbers
{
	/**
	 * Generates a random number from 0 to 99.
	 */
	public static int random100() {
		return (int) Math.round ( Math.random() * 100 );
	} 
	
	/**
	 * Generates a random number from 0 to 9999
	 */
	public static int random10000()	{
		return (int) Math.round ( Math.random() * 10000 );
	}
} 
