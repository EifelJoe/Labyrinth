package de.fhac.mazenet.server.userinterface.mazeFX.data;

import de.fhac.mazenet.server.generated.PositionType;
import javafx.scene.Node;

/**
 * Created by Richard on 26.02.2017.
 */
public class VectorInt2 {
    public final int x;
    public final int y;

    public VectorInt2(int x, int y){
        this.x=x;
        this.y=y;
    }

    public void applyTo(Node node){
        node.setTranslateX(x);
        node.setTranslateY(y);
    }

    public VectorInt2 translateX(int dx){
        return new VectorInt2(x+dx,y);
    }
    public VectorInt2 translateY(int dy){
        return new VectorInt2(x,y+dy);
    }
    public VectorInt2 translate(VectorInt2 d){
        return new VectorInt2(x+d.x,y+d.y);
    }
    public VectorInt2 invert(){
        return new VectorInt2(-x,-y);
    }

    public static VectorInt2 copy(PositionType pt){
        return new VectorInt2(pt.getCol(), pt.getRow());
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other instanceof VectorInt2 && equals((VectorInt2) other);
    }

    private boolean equals(VectorInt2 other){
        return other!=null && other.x==this.x && other.y==this.y;
    }

}
