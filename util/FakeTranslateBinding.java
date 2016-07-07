package de.fhaachen.mazenet.server.userInterface.mazeFX.util;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.transform.Translate;

/**
 * Created by Richard on 04.06.2016.
 */
public class FakeTranslateBinding {
    private final Node bind;
    private final Node source;

    private final FakeDoubleBinding xBind, yBind, zBind;

    public FakeTranslateBinding(Node bind, Node source){
        this.bind = bind;
        this.source = source;

        xBind = new FakeDoubleBinding(bind.translateXProperty(),source.translateXProperty());
        yBind = new FakeDoubleBinding(bind.translateYProperty(),source.translateYProperty());
        zBind = new FakeDoubleBinding(bind.translateZProperty(),source.translateZProperty());
    }

    public FakeTranslateBinding(Node bind, Node source, Translate3D offset){
        this.bind = bind;
        this.source = source;

        xBind = new FakeDoubleBinding(bind.translateXProperty(),Bindings.add(offset.x,source.translateXProperty()));
        yBind = new FakeDoubleBinding(bind.translateYProperty(),Bindings.add(offset.y,source.translateYProperty()));
        zBind = new FakeDoubleBinding(bind.translateZProperty(),Bindings.add(offset.z,source.translateZProperty()));
    }

    public FakeTranslateBinding bind(){
        xBind.bind();
        yBind.bind();
        zBind.bind();
        return this;
    }

    public FakeTranslateBinding unbind(){
        xBind.unbind();
        yBind.unbind();
        zBind.unbind();
        return this;
    }
}
