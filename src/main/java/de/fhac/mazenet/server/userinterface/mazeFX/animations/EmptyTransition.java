package de.fhac.mazenet.server.userinterface.mazeFX.animations;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.util.Duration;

public class EmptyTransition extends Transition {

    public EmptyTransition(){
        super();
        Duration dur = Duration.millis(0);
        setCycleDuration(dur);
        setCycleCount(1);
        setInterpolator(Interpolator.LINEAR);
    }

    @Override
    protected void interpolate(double frac) {}

}
