

package com.rfhoodrdm.asterage2.gameObjects.asterage1;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public abstract class SpaceObject {
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	double xPosition;			//x coordinate on game board
	double yPosition;			//y coordinate on game board
	int diameter;				//size from one edge to another
	
	int velocityAngle;			//which direction is the object travelling
	int angleFacing;			//which direction is the object facing?
	int rotationalSpeed;		//how many degrees per tick is the object rotating? Can be positive or negative.
	double velocity;			//how fast is it going in that direction.
	double maxVelocity;			//what is the maximum speed of the object.
	double currentAcceleration;	//how fast is this object accelerating?
	
	
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	/**
	 * 
	 * @param passedXPosition
	 * @param passedYPosition
	 * @param passedDiameter
	 * @param passedVelocityAngle
	 * @param passedAngleFacing
	 * @param passedRotationSpeed
	 * @param passedVelocity
	 * @param passedMaxVelocity
	 * @param passedAcceleration 
	 */
	public SpaceObject ( double passedXPosition, double passedYPosition, int passedDiameter, 
						int passedVelocityAngle, int passedAngleFacing, int passedRotationSpeed,
						double passedVelocity, double passedMaxVelocity, double passedAcceleration)
	{
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
	} //end constructor
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	public double getXPosition() 
	{
		return this.xPosition;
	} //end function getXPosition
	public double getYPosition() 
	{
		return this.yPosition;
	} //end function getXPosition
	public int getDiameter ()
	{
		return this.diameter;
	} //end function getDiameter
	public int getVelocityAngle ()
	{
		return this.velocityAngle;
	} //end function getVelocityAngle 
	public void setVelocityAngle ( int newAngle )
	{
		this.velocityAngle = newAngle;
	} 
	public double getVelocity()
	{
		return this.velocity;
	} //end function getVelocity
	public void setVelocity ( double newVelocity )
	{
		this.velocity = newVelocity;
	} //end function setVelocity
	
	public void setPosition ( double newXPosition, double newYPosition )
	{
		this.xPosition = newXPosition;
		this.yPosition = newYPosition;
	} //end function setPosition
	public int getRotationSpeed ()
	{
		return this.rotationalSpeed;
	} //end function getRotationSpeed
	public int getAngleFacing ()
	{
		return this.angleFacing;
	} //end function getAngleFacing
	
	public double getCurrentAcceleration ()
	{
		return this.currentAcceleration;
	} //end function getAcceleration
	
	/**
	 * The basic version of this function checks the angle and velocity in which the
	 * object is traveling. It then extrapolates the x and y position adjustments,
	 * and adds or decrements them from the current object position. 
	 * After that, it invokes checkAndCorrectLocation to reposition the object if it flies
	 * off the board.
	 */
	public void moveObject ()
	{
		if ( (this.getCurrentAcceleration() > 0.0) || ( this.getCurrentAcceleration() < 0.0) )
		{
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
	} //end function moveObject
	
	
	/**
	 * The paint method called is a generic one that paints the current sprite to the game board.
	 * It takes into consideration the size of the object, as well as the angle it is facing.
	 * The object itself must supply its own sprite, which is presumably decided based on its state.
	 * @param g 
	 */
	public void paintToBoard ( Graphics g )
	{
		//downcast the graphics handle so we may use advanced features.
		Graphics2D g2d = (Graphics2D) g;
		
		/*
		//rotate the graphics object on the point of the ship's location. This allows us
		//to show rotation.
		g2d.rotate ( Math.toRadians ( this.angleFacing), this.xPosition, this.yPosition );
		
		//draw the ship's sprite
		//first calculate the offset ( diameter / 2 ), the ship graphic's displacement compared to its central position.
		int offset = this.diameter / 2;
		g2d.drawImage( selectSprite() , ((int)Math.round(this.xPosition) - offset) , ((int)Math.round(this.yPosition) - offset ), null);
		
		//after we're done, rotate the screen back around, to go back to normal.
		g2d.rotate ( Math.toRadians ( -1 * this.angleFacing), this.xPosition, this.yPosition );
		*/
		
		int offset = this.diameter/2;
		double rotationRequired = Math.toRadians(this.angleFacing);
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, offset, offset);
		AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_BILINEAR );
		
		g2d.drawImage(	op.filter(selectSprite(), null),
						((int)Math.round(this.xPosition) - offset),
						((int)Math.round(this.yPosition) - offset ),
						null ); 
		
		
	} //end function paintToBoard;
	
	/**
	 * Takes in as argument a second space object, and determines whether the second object has collided with the first.
	 * This is defined as some point of their spaces overlapping, measured by the radii from their respective centers.
	 * @param secondObject
	 * @return 
	 */
	public boolean checkForCollision( SpaceObject secondObject )
	{
		//Get the positions of each object.
		double x1 = this.xPosition;
		double y1 = this.yPosition;
		double x2 = secondObject.getXPosition();
		double y2 = secondObject.getYPosition();
		
		int collisionThreshold = (this.diameter + secondObject.getDiameter() )/ 2;
		
		//a^2 + b^2 = c^2
		double hypoteneuse = Math.sqrt(	Math.pow( (x1 - x2), 2) + 
										Math.pow( (y1 - y2), 2)		);
		
		//if we've come closer than the threshold, then we have collided.
		if ( collisionThreshold > hypoteneuse )
		{
			return true;
		}
		//else
		return false;
	} //end function 
	
	//Functions that must be implemented, or at least attended to, by sub-classes.
	protected abstract BufferedImage selectSprite();
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	/**
	 * Apply positive acceleration in the facing direction, or reduce acceleration by braking.
	 */
	private void applyAcceleration()
	{
		if ( getCurrentAcceleration() >= 0.0 )
		{
			applyPositiveAcceleration();
			
			//check if the object is going too fast. If so, set its speed at max velocity.
			if ( this.velocity > this.maxVelocity )
			{
				this.velocity = this.maxVelocity;
			} 
		} //end if for positive acceleration
		else 
		{
			this.velocity += getCurrentAcceleration(); 
			//if we go below 0, then set to 0.
			if ( this.velocity < 0.0 )
			{
				this.velocity = 0.0;
			}
			
		} //end else for braking in the case of negative acceleration.
		
	} //end function applyAcceleration
	
	private void applyPositiveAcceleration()
	{
		double angleFacingRadians = Math.toRadians( this.angleFacing );
		double velocityAngleRadians = Math.toRadians( this.velocityAngle );
		//System.out.println("AngleFacing: " + angleFacing + " velocityAngle " + velocityAngle );
		//System.out.println("currentAcceleration : " + currentAcceleration + " velocity " + velocity);
		
		//break down the acceleration vector into x and y components.
		double accelerationXVector = this.currentAcceleration * Math.sin(angleFacingRadians);
		double accelerationYVector = 1 * this.currentAcceleration * Math.cos(angleFacingRadians);
		//System.out.println("accelerationXVector: " + accelerationXVector + " accelerationYVector" + accelerationYVector );
		
		//break down the current velocity into x and y components.
		double velocityXVector = this.velocity * Math.sin(velocityAngleRadians);
		double velocityYVector = this.velocity * Math.cos(velocityAngleRadians);
		//System.out.println("velocityXVector " + velocityXVector + " velocityYVector" + velocityYVector );
		
		//add x and y components of our two vectors.
		double sumXVector = accelerationXVector + velocityXVector;
		double sumYVector = accelerationYVector + velocityYVector;
		//System.out.println("sumXVector " + sumXVector + "sumYVector " +  sumYVector );
		
		//derive the new total velocity.
		
		double rawVelocity = Math.sqrt(Math.pow(sumXVector, 2.0) + Math.pow(sumYVector, 2.0) );
		
		//derive the new velocity angle.
		int rawAngle = (int) Math.round (Math.toDegrees( Math.atan2(sumXVector, sumYVector) ) );
		//System.out.println("rawVelocity" + rawVelocity + "rawAngle" + rawAngle);
		//System.out.println("");
		
		this.velocity = rawVelocity;
		this.velocityAngle = rawAngle;
		
		
	} //end function applyPositiveAcceleration
	
	/**
	 * Check for space object being possibly out of bounds. If so, move it to opposite side of board, 
	 * so it can fly onto the screen.
	 */
	private void checkAndCorrectLocation()
	{
		//If too far above the top edge of the screen, move to bottom.
		if ( this.yPosition < ( 0 - diameter/2 ) )
		{
			this.yPosition += (600+ diameter);
		}
		//if too far below the bottom edge of the screen, move to top.
		if ( this.yPosition > ( 600 + diameter/2 ) )
		{
			this.yPosition -= (600+ diameter);
		}
		//if too far to the left, move to the right.
		if ( this.xPosition < ( 0 - diameter/2 ) )
		{
			this.xPosition += (1200 + diameter );
		}
		//if too far to the right, move to the left.
		if ( this.xPosition > ( 1200 + diameter/2 ) )
		{
			this.xPosition -= (1200 + diameter );
		}
		
	} //end function checkAndCorrectLocation
	
	/**
	 * Angle = angle % 360. Made protected because sub classes may use it. 
	 * It is safe, as it doesn't significantly change state, game play-wise.
	 */
	protected void checkAndCorrectAngleFacing()
	{
		if ( this.angleFacing < 0)
		{
			this.angleFacing += 360;
		} //end if for below 0 
		if ( this.angleFacing > 360 )
		{
			this.angleFacing -= 360;
		} //end if for over 360
	} //end function checkAndCorrectAngleFacing
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
}
