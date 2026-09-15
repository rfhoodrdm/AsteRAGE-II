package gui.templates;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Color;

/**
 * IconButtonTemplate holds the characteristics common to all icon buttons used in the application.
 * The are designed to be instantiated by passing an Image to the constructor, just as their JButton predecessors.
 */
public class IconButtonTemplate
extends JButton
{
	/**
	 * Constructor sets up common attributes for all IconButtons
	 * @param imageIcon 
	 */
	public IconButtonTemplate ( ImageIcon imageIcon)
	{
		super ( imageIcon );				//invoke superclass constructor with image icon.
		
		this.setFocusable( false );			//make not focusable.
		this.setBorderPainted(false);		//dont show the border, so clear the background too. Just the icon.
		
		this.setBackground( new Color ( 0,0,0,0) );
		this.setForeground( new Color ( 0,0,0,0) );
		
	} //end constructor
	
}
