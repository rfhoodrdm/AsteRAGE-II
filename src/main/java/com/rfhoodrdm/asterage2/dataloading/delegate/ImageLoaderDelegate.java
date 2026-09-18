package com.rfhoodrdm.asterage2.dataloading.delegate;

import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Optional;

import javax.imageio.ImageIO;

import com.rfhoodrdm.asterage2.common.exceptions.DataLoaderException;

/**
 * Delegate responsible for loading BufferedImage assets.
 */



@Slf4j
public class ImageLoaderDelegate {
	
	private static final String IMAGE_RESOURCE_DIRECTORY = "images";

	/**
	 * Loads one Image into memory and returns it.
	 * Order of preference
	 * 	1) Images in /config/data folder. (custom overrides)  TODO!
	 *  2) Images from the resource loader, bundled with the game.
	 *  3) Default image, if any. TODO!
	 * @throws DataLoaderException if no image can be found in any location, including the default.
	 */
	public BufferedImage loadImage(String imageName)
		throws DataLoaderException {
	
		Optional<BufferedImage> imageMaybe = loadImageWithResourceLoader(imageName);
		if(imageMaybe.isPresent()) {
			log.debug("Successfully loaded from resource path: {}", imageName);
			return imageMaybe.get();
		}

		throw new DataLoaderException (imageName);
	}

	private Optional<BufferedImage> loadImageWithResourceLoader(String imageName) {
		
		String imagePath = IMAGE_RESOURCE_DIRECTORY + "/" + imageName;

		try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(imagePath)) {
			if(inputStream != null) {
				BufferedImage loadedImage = ImageIO.read(inputStream);
				return Optional.ofNullable(loadedImage);
			}
		} catch (Exception ex) {
			log.warn("No image resource found with name: {} At Path: {}", imageName, imagePath);
		}
		
		return Optional.empty();
	}
	
}
