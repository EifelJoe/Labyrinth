package de.fhaachen.mazenet.server.userInterface.mazeFX.objects;

import de.fhaachen.mazenet.generated.TreasureType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;

/**
 * Created by Richard on 01.06.2016.
 */
public class TreasureFX extends Cylinder {
    private static final String IMG_PATH = "/server/userInterface/resources/";
    private static final String IMG_EXT = ".png";

    private static final double RADIUS = 0.2;
    private static final double HEIGHT = 0.025;

    private TreasureType treasureType;
    private Image image;
    private PhongMaterial material;

    private TreasureFX(){
        super(RADIUS,HEIGHT);
        material = new PhongMaterial(Color.WHITE);
        setMaterial(material);
        setCullFace(CullFace.NONE);
    }

    public TreasureFX(TreasureType tt){
        this();
        image = new Image(IMG_PATH+tt.value()+IMG_EXT);
        material.setDiffuseMap(image);
        treasureType = tt;
    }

    public TreasureType getTreasureType(){
        return treasureType;
    }
}
