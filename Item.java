import java.util.Calendar;
import java.util.GregorianCalendar;

public class Item {
	
	private int id; // the server should be responsable for items id generation
	private String name;
	private float minimumValue;
	private Calendar closingDatetime;
	private Calendar removalDatetime;

	public Item (int id, String name, float minimumValue, Calendar closingDatetime) {
		this.id = id;
		this.name = name;
		this.minimumValue = minimumValue;
		this.closingDatetime = closingDatetime;
		removalDatetime = GregorianCalendar.getInstance();
		removalDatetime.add(Calendar.MINUTE, TypesNConst.MAX_ITEM_REMOVAL_TIME);
	}

	public Item (int id, String name, float minimumValue, Calendar closingDatetime, Calendar removalDatetime) {
		this.id = id;
		this.name = name;
		this.minimumValue = minimumValue;
		this.closingDatetime = closingDatetime;
		this.removalDatetime = removalDatetime;
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

	public Calendar getClosingDate () {
		return closingDatetime;
	}

	public boolean isClosed (Calendar now) {
		return closingDatetime.after(now);
	}


}