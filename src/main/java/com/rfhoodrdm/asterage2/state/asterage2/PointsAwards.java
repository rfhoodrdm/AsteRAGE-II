package com.rfhoodrdm.asterage2.state.asterage2;

import lombok.Getter;

public enum PointsAwards {
	LARGE_ASTEROID_HIT(10l),
	MEDIUM_ASTEROID_HIT(10l),
	SMALL_ASTEROID_HIT(10l),
	
	TROLL_BASIC_SHIP_HIT(5l),
	TROLL_BASIC_SHIP_DESTROYED(250l),
	TROLL_MOTHERSHIP_DESTROYED(1000l),
	
	EXTRA_POINT_PURCHASE(Asterage2State.POINTS_REQUIRED_PER_EXTRA_LIFE/2);
	
	@Getter
	private long pointAward;
	
	PointsAwards( long passedValue ) { 
		this.pointAward = passedValue;
	}

}
