

package com.rfhoodrdm.asterage2.gameObjects.asterage1;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;

import lombok.Getter;
import lombok.Setter;

public abstract class SpaceObject {
	
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	@Getter protected double xPosition;			//x coordinate on game board
	@Getter protected double yPosition;			//y coordinate on game board
	@Getter protected int diameter;				//size from one edge to another
	
	@Getter protected int velocityAngle;	//which direction is the object travelling
	@Getter protected int angleFacing;	//which direction is the object facing?
	@Getter protected int rotationalSpeed;		//how many degrees per tick is the object rotating? Can be positive or negative.
	@Getter protected double velocity;	//how fast is it going in that direction.
	@Getter protected double maxVelocity;			//what is the maximum speed of the object.
	@Getter protected double currentAcceleration;	//how fast is this object accelerating?
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */

	public SpaceObject ( double passedXPosition, double passedYPosition, int passedDiameter, 
						int passedVelocityAngle, int passedAngleFacing, int passedRotationSpeed,
						double passedVelocity, double passedMaxVelocity, double passedAcceleration)	{
		//initialize instance variables.
		this.xPosition = passedXPosition;
		this.yPosition = passedYPosition;
		this.diameter = passedDiameter;
		this.velocityAngle = passedVelocityAngle;
		this.angleFacing = passedAngleFacing;
		this.rotationalSpeed = passedRotationSpeed;
		this.velocity = passedVelocity;
		this.maxVelocity = passedMaxVelocity;
		this.currentAcceleration = passedAcceleration;
	} 
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */

	public void setPosition ( double newXPosition, double newYPosition )
	{
		this.xPosition = newXPosition;
		this.yPosition = newYPosition;
	} 

	
	/**
	 * The basic version of this function checks the angle and velocity in which the
	 * object is traveling. It then extrapolates the x and y position adjustments,
	 * and adds or decrements them from the current object position. 
	 * After that, it invokes checkAndCorrectLocation to reposition the object if it flies
	 * off the board.
	 */
	public void moveObject () {
		if ( (this.getCurrentAcceleration() > 0.0) || ( this.getCurrentAcceleration() < 0.0) ) {
			applyAcceleration();
		}
		
		//calculate new adjustments along the axes, accounting for translation between game board and conventional trig.
		double x_move_adjustment = this.velocity * Math.sin(Math.toRadians(velocityAngle) );
		double y_move_adjustment = -1 * this.velocity * Math.cos(Math.toRadians(velocityAngle) );
		
		this.xPosition =  ( Math.round((this.xPosition + x_move_adjustment) * 1000) ) / 1000.0;
		this.yPosition =  ( Math.round((this.yPosition + y_move_adjustment) * 1000) ) / 1000.0;
		
		//rotate the object if it has a rotational speed
		this.angleFacing += this.rotationalSpeed;
		
		//check for out of bounds.
		checkAndCorrectLocation();
		checkAndCorrectAngleFacing();
	} 
	
	
	/**
	 * The paint method called is a generic one that paints the current sprite to the game board.
	 * It takes into consideration the size of the object, as well as the angle it is facing.
	 * The object itself must supply its own sprite, which is presumably decided based on its state.
	 */
	public void paintToBoard ( Graphics g )	{

		Graphics2D g2d = (Graphics2D) g;
		
		int offset = this.diameter/2;
		double rotationRequired = Math.toRadians(this.angleFacing);
		
		//this transform rotates the sprite accordingly.
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, offset, offset);
		AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_BILINEAR );
		
		g2d.drawImage(	op.filter(selectSprite(), null),
						((int)Math.round(this.xPosition) - offset),
						((int)Math.round(this.yPosition) - offset ),
						null ); 
	} 
	
	/**
	 * Takes in as argument a second space object, and determines whether the second object has collided with the first.
	 * This is defined as some point of their spaces overlapping, measured by the radii from their respective centers.
	 */
	public boolean checkForCollision( SpaceObject secondObject ) {
		double x1 = this.xPosition;
		double y1 = this.yPosition;
		double x2 = secondObject.getXPosition();
		double y2 = secondObject.getYPosition();
		
		int collisionThreshold = (this.diameter + secondObject.getDiameter() )/ 2;
		
		//a^2 + b^2 = c^2
		double hypoteneuse = Math.sqrt(	Math.pow( (x1 - x2), 2) + 
										Math.pow( (y1 - y2), 2)		);
		
		//if we've come closer than the threshold, then we have collided.
		return (collisionThreshold > hypoteneuse);
	} 
	
	//Functions that must be implemented, or at least attended to, by sub-classes.
	protected abstract BufferedImage selectSprite();
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	/**
	 * Apply positive acceleration in the facing direction, or reduce acceleration by braking.
	 */
	private void applyAcceleration() {
		if ( getCurrentAcceleration() >= 0.0 ) {
			applyPositiveAcceleration();
			this.velocity = Math.min(this.velocity, this.maxVelocity); //limit velocity to max 
		} else {
			this.velocity += getCurrentAcceleration(); 
			this.velocity = Math.max(this.velocity, 0);	//if we go below 0, then set to 0.
		}	
	}
	
	private void applyPositiveAcceleration()	{
		double angleFacingRadians = Math.toRadians( this.angleFacing );
		double velocityAngleRadians = Math.toRadians( this.velocityAngle );
		
		//break down the acceleration vector into x and y components.
		double accelerationXVector = this.currentAcceleration * Math.sin(angleFacingRadians);
		double accelerationYVector = 1 * this.currentAcceleration * Math.cos(angleFacingRadians);
		
		//break down the current velocity into x and y components.
		double velocityXVector = this.velocity * Math.sin(velocityAngleRadians);
		double velocityYVector = this.velocity * Math.cos(velocityAngleRadians);
		
		//add x and y components of our two vectors.
		double sumXVector = accelerationXVector + velocityXVector;
		double sumYVector = accelerationYVector + velocityYVector;
		
		//derive the new total velocity.
		double rawVelocity = Math.sqrt(Math.pow(sumXVector, 2.0) + Math.pow(sumYVector, 2.0) );
		
		//derive the new velocity angle.
		int rawAngle = (int) Math.round (Math.toDegrees( Math.atan2(sumXVector, sumYVector) ) );
		
		this.velocity = rawVelocity;
		this.velocityAngle = rawAngle;
	} 
	
	/**
	 * Check for space object being possibly out of bounds. If so, move it to opposite side of board, 
	 * so it can fly onto the screen.
	 */
	private void checkAndCorrectLocation()	{
		int bottomEdge = GameConstants.GAME_BOARD_HEIGHT;
		int leftEdge = GameConstants.GAME_BOARD_WIDTH;
		
		//If too far above the top edge of the screen, move to bottom.
		if ( this.yPosition < ( 0 - diameter/2 ) ) {
			this.yPosition += (bottomEdge + diameter);
		}
		
		//if too far below the bottom edge of the screen, move to top.
		if ( this.yPosition > ( bottomEdge + diameter/2 ) ) {
			this.yPosition -= (bottomEdge + diameter);
		}
		
		//if too far to the left, move to the right.
		if ( this.xPosition < ( 0 - diameter/2 ) ) {
			this.xPosition += (leftEdge + diameter );
		}
		
		//if too far to the right, move to the left.
		if ( this.xPosition > ( leftEdge + diameter/2 ) ) {
			this.xPosition -= (leftEdge + diameter );
		}
	} 
	
	/**
	 * Angle = angle % 360. Made protected because sub classes may use it. 
	 * It is safe, as it doesn't significantly change state, game play-wise.
	 */
	protected void checkAndCorrectAngleFacing() {
		if ( this.angleFacing < 0) {
			this.angleFacing += 360;
		}
		
		if ( this.angleFacing > 360 ) {
			this.angleFacing -= 360;
		} 
	} 
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}
