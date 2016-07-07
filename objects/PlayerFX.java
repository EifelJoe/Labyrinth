package de.fhaachen.mazenet.server.userInterface.mazeFX.objects;

import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;
import de.fhaachen.mazenet.server.userInterface.mazeFX.util.FakeTranslateBinding;
import de.fhaachen.mazenet.server.userInterface.mazeFX.util.Translate3D;

/**
 * Created by Richard on 01.06.2016.
 */
public class PlayerFX extends Sphere {

    private static final double RADIUS = 0.15;
    private static final double OFFSET_Y = -0.4;

    private int id;
    private CardFX boundCard;
    private FakeTranslateBinding binding;
    private PhongMaterial material;

    private PlayerFX(){
        super(RADIUS);
        material = new PhongMaterial(Color.WHITE);
        setMaterial(material);
    }

    public PlayerFX(int id, CardFX card){
        this();
        this.id = id;
        material.setDiffuseColor(playerIdToColor(id));
        bindToCard(card);
    }

    public CardFX getBoundCard(){
        return boundCard;
    }

    public void bindToCard(CardFX card){
        /*translateXProperty().bind(card.translateXProperty());
        translateYProperty().bind(Bindings.add(OFFSET_Y,card.translateYProperty()));
        translateZProperty().bind(card.translateZProperty());*/
        if(binding!=null) unbindFromCard();
        binding = new FakeTranslateBinding(this,card,getOffset()).bind();
        boundCard=card;
    }

    public void unbindFromCard(){
        boundCard = null;
        binding.unbind();
        binding = null;
        /*translateXProperty().unbind();
        translateYProperty().unbind();
        translateZProperty().unbind();/**/
        /*setTranslateX(getTranslateX());
        setTranslateY(getTranslateY());
        setTranslateZ(getTranslateZ());/**/
    }

    public Translate3D getOffset(){
        return new Translate3D(0,OFFSET_Y,0);
    }

    public static Color playerIdToColor(int id){
        switch (id) {
            case 0:
                return Color.YELLOW;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.BLACK;
            case 3:
                return Color.RED;
            case 4:
                return Color.BLUE;
            default:
                return Color.WHITESMOKE;
        }
    }
}
