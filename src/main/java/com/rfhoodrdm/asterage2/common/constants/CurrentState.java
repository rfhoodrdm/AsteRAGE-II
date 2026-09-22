package com.rfhoodrdm.asterage2.common.constants;

public enum CurrentState {
	TITLE_SCREEN,
	ASTERAGE_1,
	ASTERAGE_2;
	
	@Override
	public String toString() {
		return switch (this) 		{
			case TITLE_SCREEN -> 	"Title Screen.";
			case ASTERAGE_1 -> 		"AsteRAGE 1";
			case ASTERAGE_2 -> 		"AsteRAGE 2";
			default -> "";
		};
	} 
} 
