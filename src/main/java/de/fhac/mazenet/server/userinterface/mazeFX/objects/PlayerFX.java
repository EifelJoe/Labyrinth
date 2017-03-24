package de.fhac.mazenet.server.userinterface.mazeFX.objects;

import de.fhac.mazenet.server.userinterface.mazeFX.util.FakeTranslateBinding;
import de.fhac.mazenet.server.userinterface.mazeFX.data.Translate3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

/**
 * Created by Richard on 01.06.2016.
 */
public class PlayerFX extends Sphere {

    private static final double RADIUS = 0.15;
    private static final double OFFSET_Y = -0.4;
    private static final double PLAYER_SPECIFIC_OFFSET = 0.15;
	public final int playerId;
	public final Translate3D playerSpecificOffset;

    private CardFX boundCard;
    private FakeTranslateBinding binding;
    private PhongMaterial material;

    private PlayerFX(int id){
        super(RADIUS);
        material = new PhongMaterial(Color.WHITE);
        setMaterial(material);
        Color c = playerIdToColor(id);
        material.setDiffuseColor(c);
	    this.playerId = id;
	    this.playerSpecificOffset = playerIdToOffset(id);
    }

    public PlayerFX(int id, CardFX card){
        this(id);
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
        return new Translate3D(0,OFFSET_Y,0).translate(playerSpecificOffset);
    }

    public static Color playerIdToColor(int id){
        switch (id) {
            case 1:
                return Color.LIMEGREEN;
            case 2:
                return Color.grayRgb(64);
            case 3:
                return Color.RED;
            case 4:
                return Color.DODGERBLUE;
            default:
                return Color.WHITESMOKE;
        }
    }

    public static Translate3D playerIdToOffset(int id){

        switch (id) {
            case 1:
                return new Translate3D(-PLAYER_SPECIFIC_OFFSET,0, -PLAYER_SPECIFIC_OFFSET);
            case 2:
                return new Translate3D(+PLAYER_SPECIFIC_OFFSET,0, +PLAYER_SPECIFIC_OFFSET);
            case 3:
                return new Translate3D(-PLAYER_SPECIFIC_OFFSET,0,+PLAYER_SPECIFIC_OFFSET);
            case 4:
                return new Translate3D(+PLAYER_SPECIFIC_OFFSET,0, -PLAYER_SPECIFIC_OFFSET);
            default:
                return new Translate3D(0,0, 0);
        }
    }
}
