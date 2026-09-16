/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import com.rfhoodrdm.asterage2.gameEffects.asterage2.GravityNetEffect;
import com.rfhoodrdm.asterage2.gui.GUI;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import com.rfhoodrdm.asterage2.utility.DebugManager;
import com.rfhoodrdm.asterage2.utility.ThetaCorrector;

/**
 * Base class of all objects that move around on the game board.
 */
public abstract class SpaceObject
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	private double xCoordinate = 0.0;				//location on game board, x-coordinate
	private double yCoordinate = 0.0;				//location on game board, y-coordinate
	private int movementAngleDegrees = 0;			//what is the theta angle of movement for this object?
	private int facingAngleDegrees = 0;				//what is the theta angle of position for this object?
	private double movementVelocity = 0.0;			//how fast is the object travelling in whatever direction it is going?
	private double movementAcceleration = 0.0;		//how fast is the object speeding up?	
	private int rotationalVelocity = 0;				//how fast is the object rotating? Negative is counterclockwise.
	private int spatialRadius = 0;					//how big is the object, measuring from the center going out.
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public SpaceObject ( double passedXCoordinate, double passedYCoordinate, int pSpacialRadius )
	{
		//we must have location coordinates to properly spawn the object. All else may be set later.
		setxCoordinate(passedXCoordinate);
		setyCoordinate(passedYCoordinate);
		setSpatialRadius(pSpacialRadius);
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	public double getxCoordinate()	{	return xCoordinate;	}
	public void setxCoordinate(double xCoordinate)	{	this.xCoordinate = xCoordinate;	}
	public int getxCoordinateAsInt() { return (int) Math.floor(getxCoordinate()); }
	public double getyCoordinate()	{	return yCoordinate;	}
	public void setyCoordinate(double yCoordinate)	{	this.yCoordinate = yCoordinate;	}
	public int getyCoordinateAsInt() { return (int) Math.floor(getyCoordinate()); }
	public int getMovementAngleDegrees()	{	return movementAngleDegrees;	}
	public int getFacingAngleDegrees()	{	return facingAngleDegrees;	}
	public double getMovementVelocity()	{	return movementVelocity;	}
	public double getMovementAcceleration()	{	return movementAcceleration;	}
	public void setMovementAcceleration(double movementAcceleration)	{	this.movementAcceleration = movementAcceleration;	}
	public int getRotationalVelocity()	{	return rotationalVelocity;	}
	public void setRotationalVelocity(int rotationalVelocity)	{	this.rotationalVelocity = rotationalVelocity;	}
	public int getSpatialRadius()	{	return spatialRadius;	}
	
	/**
	 * Sets x coordinate with width of screen as a parameter.
	 * @param pCoordinate
	 * @param boardWidth
	 */
	public void setxCoordinateWithBoundsCorrection( double pCoordinate, int boardWidth )
	{
		int spatialMargin = getSpatialRadius();				//assume that all such objects are round.
		int leftBoardMargin = 0 - spatialMargin;
		int rightBoardMargin = boardWidth + spatialMargin;
		double calculatedPosition = pCoordinate;
		int widthRelocationDistance = rightBoardMargin + spatialMargin;		// 2 radii plus board width.
		
		if ( calculatedPosition > rightBoardMargin )	{ calculatedPosition -= widthRelocationDistance; }
		else if ( calculatedPosition < leftBoardMargin )		{ calculatedPosition += widthRelocationDistance; }
		
		setxCoordinate(calculatedPosition);
	}  //end method setxCoordinateWithBoundsCorrection
	
	/**
	 * Sets y coordinate with height of screen as parameter.
	 * @param pCoordinate
	 * @param boardHeight
	 */
	public void setyCoordinateWithBoundsCorrection( double pCoordinate, int boardHeight )
	{
		int spatialMargin = getSpatialRadius();				//assume that all such objects are round.
		int topBoardMargin = 0 - spatialMargin;
		int bottomBoardMargin = boardHeight + spatialMargin;
		double calculatedPosition = pCoordinate;
		int heightRelocationDistance = bottomBoardMargin + spatialMargin;		// 2 radii plus board width.
		
		if ( calculatedPosition > bottomBoardMargin )	{ calculatedPosition -= heightRelocationDistance; }
		else if ( calculatedPosition < topBoardMargin )		{ calculatedPosition += heightRelocationDistance; }
		
		setyCoordinate(calculatedPosition);
	}  //end method setxCoordinateWithBoundsCorrection
	
	public void setRandomSpawnCoordinates ( )
	{
		//can spawn anywhere along top or right edge.
			int locationRange = GUI.panelWidth + GUI.panelHeight;
			int randomLocation = (int) Math.floor( Math.random() * locationRange );
			
			if ( randomLocation < GUI.panelWidth )
			{
				//then it's a top edge troll.
				setxCoordinate( randomLocation );
				setyCoordinate( 0 );	
			} //end if check for a top edge troll
			else
			{
				//else it's a right edge troll
				setxCoordinate ( GUI.panelWidth );
				setyCoordinate( randomLocation - GUI.panelWidth );
			} //end else clause for a right edge troll
	} //end method setRandomSpawnCoordinates
	
	/**
	 * Setter for movement angle degrees corrects argument to be in 0-359 range.
	 * @param movementAngleDegrees 
	 */
	public void setMovementAngleDegrees(int movementAngleDegrees)	
	{	
		this.movementAngleDegrees = ThetaCorrector.correctThetaRange(movementAngleDegrees);	
	} //end method setMovementAngleDegrees
	
	/**
	 * Setter for facing angle degrees corrects argument to be in 0-359 range.
	 * @param facingAngleDegrees 
	 */
	public void setFacingAngleDegrees(int facingAngleDegrees)	
	{	
		this.facingAngleDegrees = ThetaCorrector.correctThetaRange(facingAngleDegrees);	
	} //end method setFacingAngleDegrees
	
	/**
	 * Sets spatial radius. Must be at least 0. If not, sets to 0 automatically.
	 * @param spatialRadius 
	 */
	public void setSpatialRadius(int spatialRadius)
	{
		//make sure the radius is at least 0.
		this.spatialRadius = (spatialRadius >= 0) ? spatialRadius : 0;
	} //end method setSpatialRadius
	
	/**
	 * Sets the velocity, taking into consideration any given max acceleration.
	 * @param movementVelocity 
	 */
	public void setMovementVelocity(double movementVelocity)	{	this.movementVelocity = movementVelocity;	}
	
	
	/**
	 * Draws the object to the game board.
	 * Override for custom drawing. Otherwise, uses the simpleSprite draw method.
	 * @param g 
	 */
	public void paintObject ( Graphics g )
	{
		//if not overridden, use the simple sprite draw method.
		simpleSpriteDraw( g );
	} //end method drawObject
	
	
	/**
	 * Performs one update of position, velocity, and facing angle.
	 * @param boardWidth
	 * @param boardHeight 
	 * @param gravityNetInEffect Is Gravity Net system active?
	 */
	public void moveAndRotate ( int boardWidth, int boardHeight, boolean gravityNetActive )
	{
		//move the object, and wrap.
		rotateObject ();
		applyAcceleration();
		moveObject ( boardWidth, boardHeight, gravityNetActive );
	
	} //end method rotateAndMove
	
	protected void moveObject (  int boardWidth, int boardHeight, boolean gravityNetActive )
	{
		//calculate the new position from the velocity and the angle of movement.
		double currentXPosition = getxCoordinate();
		double currentYPosition = getyCoordinate();
		double currentVelocity = getMovementVelocity();
		boolean objectAffectedByGravityNet = checkAffectedByGravityNet();
		double movementAngle = getMovementAngleDegrees();
		
		double effectiveVelocity = ( objectAffectedByGravityNet && gravityNetActive ) ?
				(currentVelocity * GravityNetEffect.GRAVITY_NET_SLOWDOWN_COOEFFICIENT) : currentVelocity;
		
		double deltaX = Math.sin( Math.toRadians(movementAngle)) * effectiveVelocity;
		double deltaY = Math.cos( Math.toRadians(movementAngle)) * effectiveVelocity * -1;		//adjust sign for trig applied to game board angle.
		double calculatedXPosition = currentXPosition + deltaX;
		double calculatedYPosition = currentYPosition + deltaY;
		
		//correct the location to wrap around to the opposite side if the object completely disappears off of the side of screen.
//		int spatialMargin = getSpatialRadius();				//assume that all such objects are round.
//		int leftBoardMargin = 0 - spatialMargin;
//		int rightBoardMargin = boardWidth + spatialMargin;
//		int topBoardMargin = 0 - spatialMargin;
//		int bottomBoardMargin = boardHeight + spatialMargin;
//		int widthRelocationDistance = rightBoardMargin + spatialMargin;		//2 object radii plus board width.
//		int heightRelocationDistance = bottomBoardMargin + spatialMargin;	//2 object radii plus board height.
//		if ( calculatedXPosition > rightBoardMargin )	{ calculatedXPosition -= widthRelocationDistance; }
//		if ( calculatedXPosition < leftBoardMargin )	{ calculatedXPosition += widthRelocationDistance; }
//		if ( calculatedYPosition > bottomBoardMargin )	{ calculatedYPosition -= heightRelocationDistance; }
//		if ( calculatedYPosition < topBoardMargin )		{ calculatedYPosition += heightRelocationDistance; }
//		
//		DebugManager.logMessage(6, "Top: " + topBoardMargin + " Bottom: " + bottomBoardMargin + 
//				" Left : " + leftBoardMargin + " Right: " + rightBoardMargin );
		
		//set the new position.
		setxCoordinateWithBoundsCorrection(calculatedXPosition, boardWidth);
		setyCoordinateWithBoundsCorrection(calculatedYPosition, boardHeight);
	} //end method moveObject
	
	protected void rotateObject()
	{
		//calculate the new facing angle, and correct for range of degrees.
		int currentFacingAngle = getFacingAngleDegrees();
		int rotationVelocity = getRotationalVelocity();
		int calculatedAngle = currentFacingAngle + rotationVelocity;
		setFacingAngleDegrees( ThetaCorrector.correctThetaRange(calculatedAngle));
	} //end method rotateObject
	
	private void applyAcceleration()
	{
		double accelerationRate = getMovementAcceleration();
		double currentVelocity = getMovementVelocity();
		double maximumVelocity = getMaxVelocity();
		int movementDirection = getMovementAngleDegrees();
		int facingDirection = getFacingAngleDegrees();
		
		double calculatedVelocity = 0.0;
		
		//3 cases.
		//	1) No current acceleration ( accleration = 0 )  -> Return without doing anything.
		//	2) Decelerating ( accleration < 0 )				-> simply reduce current velocity without going below 0.
		//	3) Acceleration ( acccleratio > 0 )				-> calculate new movement direction and velocity.
		if ( 0.0 == accelerationRate )
		{
			return;	//perform no calculations. Return now.
		} //end if case for no current acceleration
		else if ( 0 > accelerationRate ) 
		{
			calculatedVelocity = currentVelocity + accelerationRate;
		} //end if case for negative acceleration
		else // 0 < accelerationRate
		{
			//factor in the acceleration + facing angle with the 
			//current velocity + movement angle.
			double currentXVelocity = Math.sin( Math.toRadians(movementDirection) ) * currentVelocity;
			double currentYVelocity = Math.cos( Math.toRadians(movementDirection) ) * currentVelocity;
			
			double deltaXVelocity = Math.sin( Math.toRadians(facingDirection) ) * accelerationRate;
			double deltaYVelocity = Math.cos( Math.toRadians(facingDirection) ) * accelerationRate;
			
			double cumulativeXVelocity = currentXVelocity + deltaXVelocity;
			double cumulativeYVelocity = currentYVelocity + deltaYVelocity;
			
			DebugManager.logMessage(6, 
								" xcur: " + currentXVelocity + " ycur: " + currentYVelocity + 
								" xdel: " + deltaXVelocity + " ydel: " + deltaYVelocity + 
								" Xcum: " + cumulativeXVelocity + " Ycum: " + cumulativeYVelocity );
			
			double squaredSum = Math.pow(cumulativeXVelocity, 2) + Math.pow(cumulativeYVelocity, 2);
			calculatedVelocity = Math.sqrt( squaredSum );
			
			//calculate the new angle of movement using ArcTangent, with the calculated velocities forming the triangle sides.
			int calculatedNewMovementAngle = 
					(int) Math.round (Math.toDegrees( Math.atan2(cumulativeXVelocity, cumulativeYVelocity) ) );		//adjust for gameboard trig discrepency
			setMovementAngleDegrees(calculatedNewMovementAngle);
		} //end if case for positive acceleration
	
		//check new velocity boundaries.
		if ( 0 > calculatedVelocity ) { calculatedVelocity = 0; }	//do not go below 0.
		if ( calculatedVelocity > maximumVelocity ) { calculatedVelocity = maximumVelocity; }
		
		setMovementVelocity(calculatedVelocity);
		
	} //end method applyAcceleration
	
	/**
	 * Checks to see if another space object has collided with this one.
	 * @param otherObject
	 * @return 
	 */
	public boolean checkCollision ( SpaceObject otherObject )
	{
		//Compare squared distances to save time on squareroot calculations. Error occuring at less than 1 distance is acceptable.
		
		//first calculate the threshold distance.
		int firstSpatialRadius = getSpatialRadius();
		int secondSpatialRadius = otherObject.getSpatialRadius();
		double thresholdSquared = Math.pow( (firstSpatialRadius + secondSpatialRadius), 2);
		
		double firstXCoordinate = getxCoordinate();
		double firstYCoordinate = getyCoordinate();
		double secondXCoordinate = otherObject.getxCoordinate();
		double secondYCoordinate = otherObject.getyCoordinate();
		
		double distanceSquared =	Math.pow((firstXCoordinate-secondXCoordinate), 2) + 
									Math.pow((firstYCoordinate-secondYCoordinate), 2);
		
		return ( distanceSquared < thresholdSquared );		//return same as if distance is less than threshold
	} //end method checkCollision
	
	//abstract methods pushed off to respective sub-classes
	//*****************************************************
	/**
	 * This method allows the sub-class to return a simple sprite object for drawing to the screen.
	 * Used if the object doesn't want to perform custom drawing of itself.
	 * @return BufferedImage to draw to the game board.
	 */
	protected abstract BufferedImage getSimpleSprite ();		
	
	/**
	 * Get the max velocity of this object. Used in calculated movement and velocity updates, and
	 * when spawning certain objects with constant velocity from birth.
	 * @return 
	 */
	protected abstract double getMaxVelocity();
	
	/**
	 * Query the object to see if it is the type that is affected by gravity net slowdown effect.
	 * Used in movement/velocity calculation.
	 * @return 
	 */
	protected abstract boolean checkAffectedByGravityNet();	

	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/**
	 * Draw the space object to the game board using its simple sprite, if it has one.
	 * @param g 
	 */
	protected void simpleSpriteDraw ( Graphics g )
	{
		//convert to g2d object for rotational capabilities.
		Graphics2D g2d = (Graphics2D) g;
		
		//get the sprite and draw it to the location, calculating offset from center depending on how big the image is.
		BufferedImage objectSprite = getSimpleSprite();
		if ( null == objectSprite )
		{
			return;	//can't draw the sprite if there is none.
		} //end if check for null.
		
		int x_offset = objectSprite.getWidth()/2;
		int y_offset = objectSprite.getHeight()/2;
		double rotationRequired = Math.toRadians( getFacingAngleDegrees() );
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, x_offset, y_offset);
		AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_BILINEAR );
		
		g2d.drawImage(	op.filter( objectSprite, null),
						( getxCoordinateAsInt() - x_offset),
						( getyCoordinateAsInt() - y_offset ),
						null ); 
		
	} //end method simpleSpriteDraw
	
	/**
	 * Common service method to set the object moving in a random direction, and traveling at
	 * a speed from half max to full max.
	 */
	protected void randomizeStartingVelocityAndDirection()
	{
		double maxVelocity = getMaxVelocity();
		
		int randomizedTravelDirection = (int) Math.floor ( Math.random() * 360);
		double randomizedVelocity = ( .5 * maxVelocity ) +
									( .5 * maxVelocity * Math.random() );
		
		//make sure angle is not 0, 90, 180, or 270, as we don't want asteroids or ships strafing the edge of the map
		//practically invisible.
		if ( 0 == randomizedTravelDirection % 90 ) { randomizedTravelDirection += 1; }
		
		setMovementAngleDegrees(randomizedTravelDirection);
		setMovementVelocity(randomizedVelocity);
	} //end method randomizeStartingVelocityAndDirection
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} //end class SpaceObject definition