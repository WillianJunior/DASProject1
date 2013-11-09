import java.util.Calendar;
import java.util.GregorianCalendar;

import java.io.Serializable;

public class Item implements Serializable {
	
	private String name;
	private float minimumValue;
	private float currentValue;

	public Item (String name, float minimumValue) {
		this.name = name;
		this.minimumValue = minimumValue;
		currentValue = minimumValue;
	}

	public void updateCurrentValue (float currentValue) {
		this.currentValue = currentValue;
	}

	public String getName () {
		return name;
	}

	public float getMinimumValue () {
		return minimumValue;
	}
	
	public float getCurrentValue () {
		return currentValue;
	}

	public String toString () {
		return ("Item: " + name + 
			". Initial value: " + Float.toString(minimumValue) + 
			". Current Value: " + Float.toString(currentValue) + ".");
	}

}