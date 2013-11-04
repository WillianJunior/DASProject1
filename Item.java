import java.util.Calendar;
import java.util.GregorianCalendar;

public class Item {
	
	private int id; // the server should be responsable for items id generation
	private String name;
	private float minimumValue;

	public Item (int id, String name, float minimumValue) {
		this.id = id;
		this.name = name;
		this.minimumValue = minimumValue;
	}

	public void updateMinValue (float minimumValue) {
		this.minimumValue = minimumValue;
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

}