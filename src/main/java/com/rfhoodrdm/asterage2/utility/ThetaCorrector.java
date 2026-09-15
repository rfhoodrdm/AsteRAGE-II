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
public class ThetaCorrector
{
	/**
	 * Recursively wrap theta around the 0-359 degree range.
	 * @param passedTheta
	 * @return 
	 */
	public static int correctThetaRange ( int passedTheta )
	{
		if ( passedTheta >= 360 )
		{
			return correctThetaRange( passedTheta - 360 );
		} //end if check for too large angles
		else if ( passedTheta < 0 )
		{
			return correctThetaRange ( passedTheta + 360 );
		} //end if check for too small angles
		else
		{
			//it's just right. Return it as it is.
			return passedTheta;
		} //end else case for value in range.
	} //end method correctThetaRange
} //end class ThetaCorrector
