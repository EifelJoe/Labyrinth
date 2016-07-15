package de.fhaachen.mazenet.server.userInterface.mazeFX.util;

import java.net.URL;
import java.util.HashMap;

import de.fhaachen.mazenet.config.Settings;
import javafx.scene.image.Image;

public class ImageResourcesFX {
	private static HashMap<String, Image> images = new HashMap<String, Image>();

	private ImageResourcesFX() {
	}

	public static Image getImage(String name) {
		if (images.containsKey(name)) {
			return images.get(name);
		}
		URL u = ImageResourcesFX.class.getResource(Settings.IMAGEPATH + name + Settings.IMAGEFILEEXTENSION);
		Image img = new Image(u.toString());
		images.put(name, img);
		return img;
	}

	public static void reset() {
		images = new HashMap<String, Image>();
	}

	public static void treasureFound(String treasure) {
		if (!treasure.startsWith("Start")) { //$NON-NLS-1$
			URL u = ImageResourcesFX.class.getResource(Settings.IMAGEPATH + "found" //$NON-NLS-1$
					+ Settings.IMAGEFILEEXTENSION);
			images.put(treasure, new Image(u.toString()));
		}
	}
}
