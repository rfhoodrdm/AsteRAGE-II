package com.rfhoodrdm.asterage2.gui.titlescreen.components;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StoryPane 
	extends JTextPane {
	
	private static final long serialVersionUID = -4837538419776472565L;

	public StoryPane() {

		setVisible(false); 		//initially invisible and uneditable.
		setEditable(false);
		//setBackground( Color.black);
		setOpaque(false);
		
		//set up the contents.
		StyledDocument storyDoc = this.getStyledDocument();		//get document (content) reference.
		
		SimpleAttributeSet storyHeaderStyle = new SimpleAttributeSet();
		StyleConstants.setAlignment(storyHeaderStyle, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontSize(storyHeaderStyle, 48);
		StyleConstants.setBold(storyHeaderStyle, true);
		StyleConstants.setForeground(storyHeaderStyle, new Color ( 0xFF, 0xBB, 0x00) );
		
		SimpleAttributeSet storyBodyStyle = new SimpleAttributeSet();
		StyleConstants.setAlignment(storyBodyStyle, StyleConstants.ALIGN_JUSTIFIED);
		StyleConstants.setFontSize(storyBodyStyle, 28);
		StyleConstants.setBold(storyBodyStyle, true);
		StyleConstants.setItalic(storyBodyStyle, true);
		StyleConstants.setForeground(storyBodyStyle, new Color ( 0xFF, 0xFF, 0x99) );
		
		String titleString = "The AsteRAGE Mission: \n\n";
		
		String bodyString = "You are the captain of the mining ship, Trololo. You have been sent " +
							"to a distant sector of the quadrant in search of the valuable--and coveted--mineral " +
							"known as Mythicite. \n\n" + 
							"Unfortunately, this region of space is also claimed by the Space Troll Empire: " +
							"a hostile species that will undoubtly attack you at every opportunity. " + 
							"Exercise the utmost of caution and collect as much Mythicite as you can. \n\n" + 
							"Good luck, captain!";

		//TODO: Prefer text block, but make it display correctly.
//		String bodyString = """
//				You are the captain of the mining ship, Trololo. You have been sent 
//				to a distant sector of the quadrant in search of the valuable--and coveted--mineral
//				known as Mythicite. 
//				Unfortunately, this region of space is also claimed by the Space Troll Empire: 
//				a hostile species that will undoubtly attack you at every opportunity.
//				Exercise the utmost of caution and collect as much Mythicite as you can.
//				Good luck, captain!
//				""";
		
		try {
			storyDoc.setParagraphAttributes(0, titleString.length(), 
												storyHeaderStyle, false);
			storyDoc.insertString( storyDoc.getLength(), 
					titleString, 
					storyHeaderStyle );
			
			storyDoc.setParagraphAttributes(titleString.length(), titleString.length() + bodyString.length(), 
												storyBodyStyle, true);
			storyDoc.insertString( storyDoc.getLength(), 
					bodyString, 
					storyBodyStyle );
		} catch (BadLocationException e)		{
			setText("And so the legacy of AsteRAGE was committed to the neither void of troll space...");
		} 
	} 
}
