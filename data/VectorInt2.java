package de.fhaachen.mazenet.server.userInterface.mazeFX.data;

import de.fhaachen.mazenet.generated.PositionType;
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

}
