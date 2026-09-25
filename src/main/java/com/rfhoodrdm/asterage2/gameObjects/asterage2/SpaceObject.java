
package com.rfhoodrdm.asterage2.gameObjects.asterage2;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.gameEffects.asterage2.GravityNetEffect;
import com.rfhoodrdm.asterage2.utility.ThetaCorrector;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class of all objects that move around on the game board.
 */
@Slf4j
public abstract class SpaceObject
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	@Getter @Setter private double xCoordinate = 0.0;				//location on game board, x-coordinate
	@Getter @Setter private double yCoordinate = 0.0;				//location on game board, y-coordinate
	@Getter private int movementAngleDegrees = 0;			//what is the theta angle of movement for this object?
	@Getter private int facingAngleDegrees = 0;				//what is the theta angle of position for this object?
	@Getter private double movementVelocity = 0.0;					//how fast is the object travelling in whatever direction it is going?
	@Getter @Setter private double movementAcceleration = 0.0;		//how fast is the object speeding up?	
	@Getter @Setter private int rotationalVelocity = 0;				//how fast is the object rotating? Negative is counterclockwise.
	@Getter private int spatialRadius = 0;							//how big is the object, measuring from the center going out.
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	public SpaceObject ( double passedXCoordinate, double passedYCoordinate, int pSpacialRadius ) {
		//we must have location coordinates to properly spawn the object. All else may be set later.
		setXCoordinate(passedXCoordinate);
		setYCoordinate(passedYCoordinate);
		setSpatialRadius(pSpacialRadius);
	}
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	public int getxCoordinateAsInt() { return (int) Math.floor(getXCoordinate()); }
	public int getyCoordinateAsInt() { return (int) Math.floor(getYCoordinate()); }


	/**
	 * Sets x coordinate with width of screen as a parameter.
	 */
	public void setxCoordinateWithBoundsCorrection( double pCoordinate, int boardWidth ) {
		int spatialMargin = getSpatialRadius();				//assume that all such objects are round.
		int leftBoardMargin = 0 - spatialMargin;
		int rightBoardMargin = boardWidth + spatialMargin;
		double calculatedPosition = pCoordinate;
		int widthRelocationDistance = rightBoardMargin + spatialMargin;		// 2 radii plus board width.
		
		if ( calculatedPosition > rightBoardMargin )	{ calculatedPosition -= widthRelocationDistance; }
		else if ( calculatedPosition < leftBoardMargin )		{ calculatedPosition += widthRelocationDistance; }
		
		setXCoordinate(calculatedPosition);
	}  
	
	/**
	 * Sets y coordinate with height of screen as parameter.
	 */
	public void setyCoordinateWithBoundsCorrection( double pCoordinate, int boardHeight ) {
		int spatialMargin = getSpatialRadius();				//assume that all such objects are round.
		int topBoardMargin = 0 - spatialMargin;
		int bottomBoardMargin = boardHeight + spatialMargin;
		double calculatedPosition = pCoordinate;
		int heightRelocationDistance = bottomBoardMargin + spatialMargin;		// 2 radii plus board width.
		
		if ( calculatedPosition > bottomBoardMargin )	{ calculatedPosition -= heightRelocationDistance; }
		else if ( calculatedPosition < topBoardMargin )		{ calculatedPosition += heightRelocationDistance; }
		
		setYCoordinate(calculatedPosition);
	}  
	
	public void setRandomSpawnCoordinates ( ) {
		//can spawn anywhere along top or right edge.
			int locationRange = GameConstants.GAME_BOARD_WIDTH + GameConstants.GAME_BOARD_HEIGHT;
			int randomLocation = (int) Math.floor( Math.random() * locationRange );
			
			if ( randomLocation < GameConstants.GAME_BOARD_WIDTH ) {
				//then it's a top edge troll.
				setXCoordinate( randomLocation );
				setYCoordinate( 0 );	
			} else	{
				//else it's a right edge troll
				setXCoordinate ( GameConstants.GAME_BOARD_WIDTH );
				setYCoordinate( randomLocation - GameConstants.GAME_BOARD_WIDTH );
			} 
	} 
	
	/**
	 * Setter for movement angle degrees corrects argument to be in 0-359 range.
	 */
	public void setMovementAngleDegrees(int movementAngleDegrees) {	
		this.movementAngleDegrees = ThetaCorrector.correctThetaRange(movementAngleDegrees);	
	} 
	
	/**
	 * Setter for facing angle degrees corrects argument to be in 0-359 range.
	 */
	public void setFacingAngleDegrees(int facingAngleDegrees) {	
		this.facingAngleDegrees = ThetaCorrector.correctThetaRange(facingAngleDegrees);	
	}
	
	/**
	 * Sets spatial radius. Must be at least 0. If not, sets to 0 automatically.
	 */
	public void setSpatialRadius(int spatialRadius)	{
		//make sure the radius is at least 0.
		this.spatialRadius = (spatialRadius >= 0) ? spatialRadius : 0;
	}
	
	/**
	 * Sets the velocity, taking into consideration any given max acceleration.
	 */
	public void setMovementVelocity(double movementVelocity)	{	
		this.movementVelocity = movementVelocity;	
	}
	
	/**
	 * Draws the object to the game board.
	 * Override for custom drawing. Otherwise, uses the simpleSprite draw method.
	 */
	public void paintObject ( Graphics g ) {
		//if not overridden, use the simple sprite draw method.
		simpleSpriteDraw( g );
	} 
	
	/**
	 * Performs one update of position, velocity, and facing angle.
	 */
	public void moveAndRotate ( int boardWidth, int boardHeight, boolean gravityNetActive )	{
		//move the object, and wrap.
		rotateObject ();
		applyAcceleration();
		moveObject ( boardWidth, boardHeight, gravityNetActive );
	
	} 
	
	protected void moveObject (  int boardWidth, int boardHeight, boolean gravityNetActive ) {
		//calculate the new position from the velocity and the angle of movement.
		double currentXPosition = getXCoordinate();
		double currentYPosition = getYCoordinate();
		double currentVelocity = getMovementVelocity();
		boolean objectAffectedByGravityNet = checkAffectedByGravityNet();
		double movementAngle = getMovementAngleDegrees();
		
		double effectiveVelocity = ( objectAffectedByGravityNet && gravityNetActive ) ?
				(currentVelocity * GravityNetEffect.GRAVITY_NET_SLOWDOWN_COOEFFICIENT) : currentVelocity;
		
		double deltaX = Math.sin( Math.toRadians(movementAngle)) * effectiveVelocity;
		double deltaY = Math.cos( Math.toRadians(movementAngle)) * effectiveVelocity * -1;		//adjust sign for trig applied to game board angle.
		double calculatedXPosition = currentXPosition + deltaX;
		double calculatedYPosition = currentYPosition + deltaY;
		
		//set the new position.
		setxCoordinateWithBoundsCorrection(calculatedXPosition, boardWidth);
		setyCoordinateWithBoundsCorrection(calculatedYPosition, boardHeight);
	} 
	
	protected void rotateObject() {
		//calculate the new facing angle, and correct for range of degrees.
		int currentFacingAngle = getFacingAngleDegrees();
		int rotationVelocity = getRotationalVelocity();
		int calculatedAngle = currentFacingAngle + rotationVelocity;
		setFacingAngleDegrees( ThetaCorrector.correctThetaRange(calculatedAngle));
	} 
	
	private void applyAcceleration() {
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
		if ( 0.0 == accelerationRate ) {
			return;	//perform no calculations. Return now.
		} else if ( 0 > accelerationRate ) 	{
			calculatedVelocity = currentVelocity + accelerationRate;
		} else {
			//factor in the acceleration + facing angle with the 
			//current velocity + movement angle.
			double currentXVelocity = Math.sin( Math.toRadians(movementDirection) ) * currentVelocity;
			double currentYVelocity = Math.cos( Math.toRadians(movementDirection) ) * currentVelocity;
			
			double deltaXVelocity = Math.sin( Math.toRadians(facingDirection) ) * accelerationRate;
			double deltaYVelocity = Math.cos( Math.toRadians(facingDirection) ) * accelerationRate;
			
			double cumulativeXVelocity = currentXVelocity + deltaXVelocity;
			double cumulativeYVelocity = currentYVelocity + deltaYVelocity;
			
			log.trace(" xcur: {} ycur: {} xdel: {} ydel: {} Xcum: {} Ycum: {}",
					currentXVelocity, currentYVelocity, deltaXVelocity, deltaYVelocity,
					cumulativeXVelocity, cumulativeYVelocity);
			
			double squaredSum = Math.pow(cumulativeXVelocity, 2) + Math.pow(cumulativeYVelocity, 2);
			calculatedVelocity = Math.sqrt( squaredSum );
			
			//calculate the new angle of movement using ArcTangent, with the calculated velocities forming the triangle sides.
			int calculatedNewMovementAngle = 
					(int) Math.round (Math.toDegrees( Math.atan2(cumulativeXVelocity, cumulativeYVelocity) ) );		//adjust for gameboard trig discrepency
			setMovementAngleDegrees(calculatedNewMovementAngle);
		} 
	
		//check new velocity boundaries.
		if ( 0 > calculatedVelocity ) { calculatedVelocity = 0; }	//do not go below 0.
		if ( calculatedVelocity > maximumVelocity ) { calculatedVelocity = maximumVelocity; }
		
		setMovementVelocity(calculatedVelocity);
	} 
	
	/**
	 * Checks to see if another space object has collided with this one.
	 */
	public boolean checkCollision ( SpaceObject otherObject ) {
		//Compare squared distances to save time on squareroot calculations. Error occuring at less than 1 distance is acceptable.
		
		//first calculate the threshold distance.
		int firstSpatialRadius = getSpatialRadius();
		int secondSpatialRadius = otherObject.getSpatialRadius();
		double thresholdSquared = Math.pow( (firstSpatialRadius + secondSpatialRadius), 2);
		
		double firstXCoordinate = getXCoordinate();
		double firstYCoordinate = getYCoordinate();
		double secondXCoordinate = otherObject.getXCoordinate();
		double secondYCoordinate = otherObject.getYCoordinate();
		
		double distanceSquared =	Math.pow((firstXCoordinate-secondXCoordinate), 2) + 
									Math.pow((firstYCoordinate-secondYCoordinate), 2);
		
		return ( distanceSquared < thresholdSquared );		//return same as if distance is less than threshold
	} 
	
	//abstract methods pushed off to respective sub-classes
	//*****************************************************

	protected abstract BufferedImage getSimpleSprite ();		
	protected abstract double getMaxVelocity();
	protected abstract boolean checkAffectedByGravityNet();	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/**
	 * Draw the space object to the game board using its simple sprite, if it has one.
	 */
	protected void simpleSpriteDraw ( Graphics g ) {
		//convert to g2d object for rotational capabilities.
		Graphics2D g2d = (Graphics2D) g;
		
		//get the sprite and draw it to the location, calculating offset from center depending on how big the image is.
		BufferedImage objectSprite = getSimpleSprite();
		if ( null == objectSprite )	{
			return;	//can't draw the sprite if there is none.
		} 
		
		int x_offset = objectSprite.getWidth()/2;
		int y_offset = objectSprite.getHeight()/2;
		double rotationRequired = Math.toRadians( getFacingAngleDegrees() );
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, x_offset, y_offset);
		AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_BILINEAR );
		
		g2d.drawImage(	op.filter( objectSprite, null),
						( getxCoordinateAsInt() - x_offset),
						( getyCoordinateAsInt() - y_offset ),
						null ); 
		
	} 
	
	/**
	 * Common service method to set the object moving in a random direction, and traveling at
	 * a speed from half max to full max.
	 */
	protected void randomizeStartingVelocityAndDirection() {
		double maxVelocity = getMaxVelocity();
		
		int randomizedTravelDirection = (int) Math.floor ( Math.random() * 360);
		double randomizedVelocity = ( .5 * maxVelocity ) +
									( .5 * maxVelocity * Math.random() );
		
		//make sure angle is not 0, 90, 180, or 270, as we don't want asteroids or ships strafing the edge of the map
		//practically invisible.
		if ( 0 == randomizedTravelDirection % 90 ) { randomizedTravelDirection += 1; }
		
		setMovementAngleDegrees(randomizedTravelDirection);
		setMovementVelocity(randomizedVelocity);
	}
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	
} 
