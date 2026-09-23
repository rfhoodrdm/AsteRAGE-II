package com.rfhoodrdm.asterage2.state.asterage2.constants;

import static com.rfhoodrdm.asterage2.state.asterage2.PointsAwards.EXTRA_POINT_PURCHASE;

import lombok.Getter;

public enum PowerUpMenuOption {
	//each enum should have an index to identify it. All indicies should be contiguous.
	DECELERATION(0, 1),
	HOMING_MISSILE(1, 2),
	SHIELD_GENERATOR(2, 3),
	EXTRA_POINTS(3, 4),
	MULTISHOT(4, 5);
	
	private int index;
	@Getter private int cost;
	
	PowerUpMenuOption( int passedIndex,int passedCost ) {
		this.index = passedIndex;
		this.cost = passedCost;
	}
	
	public int toInt() { return this.index; }
	
	public static PowerUpMenuOption fromInt ( int indexToMatch ) {
		//go through each option, looking for a match. 
		//If no match found, throw an exception rather than returning null.
		for ( PowerUpMenuOption currentOption : values() ) {
			if ( indexToMatch == currentOption.toInt() ) { 
				return currentOption; 
			}
		} 
		
		throw new IllegalArgumentException ( "Cannot match Power Up option with index of : " + indexToMatch );
	} 
	
	public PowerUpMenuOption getNext() 	{
		//get the current index, and add one, adjusting for the limit.
		int currentIndex = this.toInt();
		int indexModulus = values().length;
		int indexToFind = (currentIndex + 1) % indexModulus;
		
		return fromInt ( indexToFind );
	} 
	
	public PowerUpMenuOption getPrevious()	{
		//check if we are at 0. If so, then we go to the highest indexed item. If not, just subtract 1.
		int currentIndex = this.toInt();
		int maxIndex = values().length - 1;
		int indexToFind = ( currentIndex > 0 ) ?  (currentIndex -1)	:	maxIndex;
		
		return fromInt ( indexToFind );
	} 
	
	public static long getExtraPointsPowerUpValue() { return EXTRA_POINT_PURCHASE.getPointAward(); }
} 