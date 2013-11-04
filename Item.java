import java.util.Calendar;
import java.util.GregorianCalendar;

public class Item {
	
	private int id; // the server should be responsable for items id generation
	private String name;
	private float minimumValue;
	private float currentValue;

	public Item (int id, String name, float minimumValue) {
		this.id = id;
		this.name = name;
		this.minimumValue = minimumValue;
		currentValue = minimumValue;
	}

	public void updateCurrentValue (float currentValue) {
		this.currentValue = currentValue;
	}

	public int getId () {
		return id;
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

}