import java.util.Calendar;
import java.util.GregorianCalendar;

public class Item {
	
	private String name;
	private float minimumValue;
	private Calendar closingDatetime;
	private Calendar removalDatetime;

	public Item (String name, float minimumValue, Calendar closingDatetime) {
		this.name = name;
		this.minimumValue = minimumValue;
		this.closingDatetime = closingDatetime;
		removalDatetime = GregorianCalendar.getInstance();
		removalDatetime.add(Calendar.MINUTE, TypesNConst.MAX_ITEM_REMOVAL_TIME);
	}

	public Item (String name, float minimumValue, Calendar closingDatetime, Calendar removalDatetime) {
		this.name = name;
		this.minimumValue = minimumValue;
		this.closingDatetime = closingDatetime;
		this.removalDatetime = removalDatetime;
	}

	public void updateMinValue (float minimumValue) {
		this.minimumValue = minimumValue;
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