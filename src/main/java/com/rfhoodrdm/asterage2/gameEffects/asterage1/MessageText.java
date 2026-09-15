package gameEffects.asterage1;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.ConcurrentLinkedQueue;
import gameObjects.asterage1.SpaceObject;
import java.awt.Font;
import java.awt.FontMetrics;
import state.Asterage1State;
import utility.GameConstants;

/**
 *
 */
public class MessageText
extends SpaceEffect
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	String theMessage;
	Color myColor;
	
	public static final int messageTextLifeSpan = GameConstants.FRAMES_PER_SECOND;
	public static final Font messageFont = new Font ( "Courier", Font.BOLD, 24);
	
	Asterage1State asterage1State;
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	public MessageText(String passedMessage, MessageText.MESSAGE_COLOR whatColor, Asterage1State passedState)
	{
		super ( 600.0, 300.0, messageTextLifeSpan, null );
		//Get the color chosen.
		
		this.myColor = whatColor.getColor();
		this.theMessage = passedMessage;
		this.asterage1State = passedState;
		
		//if there is already a message attached to game state, kill it, and take its place.
		MessageText oldMessage = asterage1State.getOfficialMessage();
		if ( oldMessage != null )
		{
			oldMessage.endLifespan();
		} //end function 
		asterage1State.setOfficialMessage ( this );
	} //end function MessageText
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	@Override
	public void paintToBoard ( Graphics g )
	{
		//if the message is already half dead, then don't draw it. This is how we get messages to blink.
		if (this.currentLifespan < (messageTextLifeSpan / 2) )
		{
			return;		//don't draw it. 
		} //end if block to see if message is half dead
		
		//set the color to the one representing this message, and draw the string to the board.
		g.setColor(myColor);
		g.setFont( messageFont );
		
		//calculate the horizontal offset.
		FontMetrics myFontMetrics = g.getFontMetrics(messageFont);
		int messageWidth = myFontMetrics.stringWidth(theMessage);
		int horizontalPosition = 600 - (messageWidth / 2);
		
		//draw the message.
		g.drawString(	theMessage, 
						horizontalPosition,
						300		);

	} //end function paintComponent.
	
	@Override
	public void killObject ( ConcurrentLinkedQueue<SpaceObject> spaceObjectList )
	{
		//remove the object from the space object list, but also detach it from the Asterage 1 state.
		spaceObjectList.remove ( this );
		if ( this == asterage1State.getOfficialMessage() )
		{
			asterage1State.setOfficialMessage(null);
		}
	} //end function killObject
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Inner Classes			******************
		********************************************************************** */
	public static enum MESSAGE_COLOR
	{
		WARNING(200, 0, 0),
		INFO(50,50,200),
		REWARD(0,200,0);
		
		Color myColor;
		
		MESSAGE_COLOR( int red, int green, int blue )
		{
			this.myColor = new Color ( red, green, blue );
		} //end constructor
		
		public Color getColor ()
		{
			return this.myColor;
		} //end function getColor
		
		
	} //end enum message_color definition
}
