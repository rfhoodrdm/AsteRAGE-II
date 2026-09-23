package com.rfhoodrdm.asterage2.dataloading.delegate;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.rfhoodrdm.asterage2.common.constants.GameConstants;
import com.rfhoodrdm.asterage2.state.highscore.HighScoreEntry;

@Slf4j
public class HighScoreXMLHandler {
	
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
	 * Loads a high score xml file and returns a Collection of high score entries.
	 */
	public static ArrayList<HighScoreEntry> loadXML( String filenameToLoad ) {
		//Initialize our return object.
		ArrayList<HighScoreEntry> highScoreList = new ArrayList<>();
		
		//Try to read in the file.
		try	{
			File highScoresFile = new File(filenameToLoad);
			if(!highScoresFile.exists()) {
				log.warn("High score file not found: {}", filenameToLoad);
				return highScoreList;
			}
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.parse(highScoresFile);
			
			NodeList highScoreEntryNodeList = doc.getElementsByTagName( GameConstants.HIGH_SCORE_ENTRY_NODE );
			
			//iterate through the node list, pick out each piece of information, make an entry, and add it to the list.
			for ( int index = 0; index < highScoreEntryNodeList.getLength();	++index ) {
				Node currentNode = highScoreEntryNodeList.item ( index );
				NamedNodeMap attributes = currentNode.getAttributes();
				String name = attributes.getNamedItem( GameConstants.HIGH_SCORE_NAME_ATTRIBUTE ).getNodeValue();
				int level = Integer.parseInt(attributes.getNamedItem( GameConstants.HIGH_SCORE_LEVEL_ATTRIBUTE ).getNodeValue() );
				long points = Long.parseLong(attributes.getNamedItem( GameConstants.HIGH_SCORE_POINTS_ATTRIBUTE ).getNodeValue() );
				
				HighScoreEntry newEntry = new HighScoreEntry(name, level, points);
				highScoreList.add( newEntry );
				log.debug("Loaded high score entry: {} / {} / {}", name, level, points);
			} 			
		} catch (ParserConfigurationException e) {
			log.warn("ParserConfigurationException thrown when trying to load high score file: {}", filenameToLoad, e);
			return new ArrayList<>();	
		} catch ( SAXException e ) {
			log.warn("SAXException thrown when trying to load high score file: {}", filenameToLoad, e);
			return new ArrayList<>();	
		} catch ( IOException e ) {
			log.warn("IOException thrown when trying to load high score file: {}", filenameToLoad, e);
			return new ArrayList<>();
		} 

		return highScoreList;
	} 
	
	
	/**
	 * Takes a high score list, and the name of the file to save to, and creates a new XML file representing
	 * the high score list.
	 */
	public static void saveXML ( ArrayList<HighScoreEntry> highScoreList, String fileNameToSave ) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.newDocument();
			
			//Create the document root.
			Element rootElement = doc.createElement( GameConstants.HIGH_SCORE_ROOT_NODE_NAME );
			doc.appendChild( rootElement );
			
			//create the high score entries.
			for ( HighScoreEntry currentEntry: highScoreList ) {
				String name = currentEntry.getHighScoreName();
				String level = "" + currentEntry.getHighScoreLevel();
				String points = "" + currentEntry.getHighScorePoints();
				
				Element highScoreElement = doc.createElement( GameConstants.HIGH_SCORE_ENTRY_NODE);
				highScoreElement.setAttribute(GameConstants.HIGH_SCORE_NAME_ATTRIBUTE, name);
				highScoreElement.setAttribute(GameConstants.HIGH_SCORE_LEVEL_ATTRIBUTE, level);
				highScoreElement.setAttribute(GameConstants.HIGH_SCORE_POINTS_ATTRIBUTE, points);
				
				rootElement.appendChild(highScoreElement);
			} 
			
			//Create the XML data, and write to a file.
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult ( new File (fileNameToSave ) );
			
			transformer.transform(domSource, streamResult);
			
			log.debug("Save to file: {} completed!", fileNameToSave);
			
		} catch (ParserConfigurationException e) {
			log.warn("ParserConfigurationException thrown when trying to save high score file: {}", fileNameToSave, e);
		} catch ( TransformerException e ) {
			log.warn("TransformerException thrown when trying to save high score file: {}", fileNameToSave, e);
		}
	} 
	
	
	/*	**********************************************************************
		********************		Functionality			******************
		********************************************************************** */
}
