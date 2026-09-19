
package com.rfhoodrdm.asterage2.utility;

public class ThetaCorrector
{
	/**
	 * Recursively wrap theta around the 0-359 degree range.
	 */
	public static int correctThetaRange ( int passedTheta )
	{
		if ( passedTheta >= 360 ) {
			return correctThetaRange( passedTheta - 360 );
		} else if ( passedTheta < 0 ) {
			return correctThetaRange ( passedTheta + 360 );
		} else {
			//it's just right. Return it as it is.
			return passedTheta;
		} 
	} 
} 
