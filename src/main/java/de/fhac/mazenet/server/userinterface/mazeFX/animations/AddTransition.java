package de.fhac.mazenet.server.userinterface.mazeFX.animations;

import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.util.Duration;

/**
 * This Transition implementation adds a specified number to a
 * DoubleProperty per animation cycle.
 */
public class AddTransition extends Transition {
	/**
	 * Property to change
	 */
	private DoubleProperty target;
	/**
	 * Amount to add (per cycle)
	 */
	private double increasePerCycle;
	/**
	 * Last fraction, used for delta calculation
	 */
	private double lastFrac = 0;

	/**
	 * Upper (total) limit.
	 * In case of a none finite value, no limit is assumed.
	 *
	 * (Default: NaN)
	 */
	private double upperLimit = Double.NaN;
	/**
	 * Lower (total) limit.
	 * In case of a none finite value, no limit is assumed.
	 *
	 * (Default: NaN)
	 */
	private double lowerLimit = Double.NaN;

	/**
	 * Constructor
	 *
	 * @param cycleDuration     duration of one animation cycle
	 * @param target            property to change
	 * @param increasePerCycle  amount to add (per cycle)
	 */
	public AddTransition(Duration cycleDuration, DoubleProperty target, double increasePerCycle){
		setCycleDuration(cycleDuration);
		this.target = target;
		this.increasePerCycle = increasePerCycle;
		this.statusProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue == Status.STOPPED){
				lastFrac = 0;
			}
		});
	}

	/**
	 * Getter for {@link AddTransition::upperLimit}
	 *
	 * @return (total) upper limit
	 */
	public double getUpperLimit() {
		return upperLimit;
	}

	/**
	 * Setter for {@link AddTransition::upperLimit}
	 *
	 * @param upperLimit new (total) upper limit
	 */
	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}

	/**
	 * Getter for {@link AddTransition::lowerLimit}
	 *
	 * @return (total) lower limit
	 */
	public double getLowerLimit() {
		return lowerLimit;
	}

	/**
	 * Setter for {@link AddTransition::lowerLimit}
	 *
	 * @param lowerLimit new (total) lower limit
	 */
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	@Override
	protected void interpolate(double frac) {
		double fracDelta = frac-lastFrac;

		// check for new cycle
		if(fracDelta<0 && !isAutoReverse()) {
			fracDelta = 1 - fracDelta;
		}

		double add = fracDelta*increasePerCycle;
		double newVal = target.getValue()+add;

		// apply limits
		if(Double.isFinite(upperLimit) && newVal>upperLimit){
			newVal = upperLimit;
		}
		if(Double.isFinite(lowerLimit) && newVal<lowerLimit){
			newVal = lowerLimit;
		}

		target.setValue(newVal);

		lastFrac = frac;
	}
}
