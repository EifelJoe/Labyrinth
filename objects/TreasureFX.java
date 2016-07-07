package de.fhaachen.mazenet.server.userInterface.mazeFX.objects;

import java.net.URL;

import de.fhaachen.mazenet.config.Settings;
import de.fhaachen.mazenet.generated.TreasureType;
import de.fhaachen.mazenet.server.userInterface.mazeFX.util.ImageResourcesFX;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;

/**
 * Created by Richard on 01.06.2016.
 */
public class TreasureFX extends Cylinder {

	private static final double RADIUS = 0.2;
	private static final double HEIGHT = 0.025;

	private TreasureType treasureType;
	private Image image;
	private PhongMaterial material;

	private TreasureFX() {
		super(RADIUS, HEIGHT);
		material = new PhongMaterial(Color.WHITE);
		setMaterial(material);
		setCullFace(CullFace.NONE);
	}

	public TreasureFX(TreasureType tt) {
		this();
		image = ImageResourcesFX.getImage(tt.value());
		material.setDiffuseMap(image);
		treasureType = tt;
	}

	public TreasureType getTreasureType() {
		return treasureType;
	}

	public void treasureFound() {
			URL u = ImageResourcesFX.class.getResource(Settings.IMAGEPATH + "found" //$NON-NLS-1$
					+ Settings.IMAGEFILEEXTENSION);
			image= new Image(u.toString());
			material.setDiffuseMap(image);
	}
}
