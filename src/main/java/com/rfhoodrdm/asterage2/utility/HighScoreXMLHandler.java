package utility;

import java.util.ArrayList;
import state.HighScoreEntry;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException; 
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;


/**
 * Loads and saves the high score lists.
 * @author roberthood
 */
public class HighScoreXMLHandler
{
	/*	**********************************************************************
		*******************			Data Members			******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Constructor				******************
		********************************************************************** */
	
	/*	**********************************************************************
		********************		Class Interface			******************
		********************************************************************** */
	
	/**
	 * Loads a high score xml file and returns a ConcurrentLinkedQueue of high score entries.
	 * @param filenameToLoad
	 * @return 
	 */
	public static ArrayList<HighScoreEntry> loadXML( String filenameToLoad )
	{
		//Initialize our return object.
		ArrayList<HighScoreEntry> highScoreList = new ArrayList<>();
		
		//Try to read in the file.
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.parse(new File("data/" + filenameToLoad));
			
			NodeList highScoreEntryNodeList = doc.getElementsByTagName( GameConstants.HIGH_SCORE_ENTRY_NODE );
			
			//iterate through the node list, pick out each piece of information, make an entry, and add it to the list.
			for ( int index = 0; index < highScoreEntryNodeList.getLength();	++index )
			{
				Node currentNode = highScoreEntryNodeList.item ( index );
				NamedNodeMap attributes = currentNode.getAttributes();
				String name = attributes.getNamedItem( GameConstants.HIGH_SCORE_NAME_ATTRIBUTE ).getNodeValue();
				int level = Integer.parseInt(attributes.getNamedItem( GameConstants.HIGH_SCORE_LEVEL_ATTRIBUTE ).getNodeValue() );
				long points = Long.parseLong(attributes.getNamedItem( GameConstants.HIGH_SCORE_POINTS_ATTRIBUTE ).getNodeValue() );
				
				HighScoreEntry newEntry = new HighScoreEntry(name, level, points);
				highScoreList.add( newEntry );
				DebugManager.logMessage(5, "Loaded high score entry: " + name + " / " + level + " / " + points);
				
			} //end for loop iterating through the high score list.
			
		} //end try block
		catch (ParserConfigurationException e) 
		{
			DebugManager.logMessage( 3, "ParserConfigurationException thrown when trying to load high score file: " 
					+ filenameToLoad + "\n" + e.getStackTrace() );
			return new ArrayList<>();	//returns an empty list. 
		}
		catch ( SAXException e )
		{
			DebugManager.logMessage( 3, "SAXException thrown when trying to load high score file: " 
					+ filenameToLoad + "\n" + e.getStackTrace() );
			return new ArrayList<>();	//returns an empty list. 
		}
		catch ( IOException e )
		{
			DebugManager.logMessage( 3, "IOException thrown when trying to load high score file: " 
					+ filenameToLoad + "\n" + e.getStackTrace() );
			return new ArrayList<>();	//returns an empty list. 
		} 
		
		//If we have reached this far, return the high score list we have made.
		return highScoreList;
	} //end loadXML function definition
	
	
	/**
	 * Takes a high score list, and the name of the file to save to, and creates a new XML file representing
	 * the high score list.
	 * @param highScoreList
	 * @param fileName 
	 */
	public static void saveXML ( ArrayList<HighScoreEntry> highScoreList, String fileNameToSave )
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.newDocument();
			
			//Create the document root.
			Element rootElement = doc.createElement( GameConstants.HIGH_SCORE_ROOT_NODE_NAME );
			doc.appendChild( rootElement );
			
			//create the high score entries.
			for ( HighScoreEntry currentEntry: highScoreList )
			{
				String name = currentEntry.getHighScoreName();
				String level = "" + currentEntry.getHighScoreLevel();
				String points = "" + currentEntry.getHighScorePoints();
				
				Element highScoreElement = doc.createElement( GameConstants.HIGH_SCORE_ENTRY_NODE);
				highScoreElement.setAttribute(GameConstants.HIGH_SCORE_NAME_ATTRIBUTE, name);
				highScoreElement.setAttribute(GameConstants.HIGH_SCORE_LEVEL_ATTRIBUTE, level);
				highScoreElement.setAttribute(GameConstants.HIGH_SCORE_POINTS_ATTRIBUTE, points);
				
				rootElement.appendChild(highScoreElement);
			} //end for loop iterating through high score entries.
			
			//Create the XML data, and write to a file.
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult ( new File ("data/" + fileNameToSave ) );
			
			transformer.transform(domSource, streamResult);
			
			DebugManager.logMessage(5, "Save to file: " + fileNameToSave + " completed!");
			
		} //end try block
		catch (ParserConfigurationException e) 
		{
			DebugManager.logMessage( 3, "ParserConfigurationException thrown when trying to save high score file: " 
					+ fileNameToSave + "\n" + e.getStackTrace() );
		}
		catch ( TransformerException e )
		{
			DebugManager.logMessage( 3, "TransformerException thrown when trying to save high score file: " 
					+ fileNameToSave + "\n" + e.getStackTrace() );
		}
	} //end function saveXML definition
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
}
