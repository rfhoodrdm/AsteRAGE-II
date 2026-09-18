package com.rfhoodrdm.asterage2.common.constants;

public enum CurrentState {
	TITLE_SCREEN,
	ASTERAGE_1,
	ASTERAGE_2;
	
	@Override
	public String toString()
	{
		switch (this)
		{
			case TITLE_SCREEN: return "Title Screen.";
			case ASTERAGE_1: return "AsteRAGE 1";
			case ASTERAGE_2: return "AsteRAGE 2";
			default: return "";
		} 
	} 
} 
