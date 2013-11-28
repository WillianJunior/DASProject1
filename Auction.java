import java.util.*;
import java.text.SimpleDateFormat;
import java.io.Serializable;

public class Auction implements Serializable {
	
	private int id;
	private Item item;
	private User owner;
	private boolean closed;
	private long closingDatetime;
	private long removalDatetime;

	public Auction (int id, Item item, User owner, Calendar closingDatetime, Calendar removalDatetime) {
		this.id = id;
		this.item = item;
		this.owner = owner;
		closed = false;
		this.closingDatetime = closingDatetime.getTimeInMillis();
		if (removalDatetime == null || ((removalDatetime.getTimeInMillis() - GregorianCalendar.getInstance().getTimeInMillis()) / 60000) > TypesNConst.MAX_ITEM_REMOVAL_TIME) {
			this.removalDatetime = GregorianCalendar.getInstance().getTimeInMillis();
			this.removalDatetime +=  TypesNConst.MAX_ITEM_REMOVAL_TIME*60000;
		} else
			this.removalDatetime = removalDatetime.getTimeInMillis();
		System.out.println("[Auction.Auction] auction created: " + toString());
	}

	public int getId () {
		return id;
	}

	public void closeAuction () throws Exception {
		if (closed)
			throw new Exception ("Auction already closed");
		closed = true;
	}

	public boolean isClosed () {
		return closed;
	}

	public boolean isClosed (long now) {
		return closingDatetime < now;
	}

	public long getClosingDate () {
		return closingDatetime;
	}

	public long getRemovalDate () {
		return removalDatetime;
	}

	public boolean equals (Auction auction) {
		return id == auction.getId();
	}

	public boolean equals (Item item, User owner) {
		return (item.equals(this.item) && owner.equals(this.owner));
	}

	public String toString () {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar dt = GregorianCalendar.getInstance();
		dt.setTimeInMillis(closingDatetime);
		String dateTime = formatter.format(dt.getTime());
		return ("Auction number " + Integer.toString(id) + 
				". Item: " + item.getName() + 
				". Owner: " + owner.getName() + 
				". Current value: " + Float.toString(item.getCurrentValue()) + 
				". open until: " + dateTime);
	}

	public float getCurrentValue () {
		return item.getCurrentValue();
	}

	public float getMinimumValue () {
		return item.getMinimumValue();
	}

	public void updateCurrentValue (float newValue) {
		item.updateCurrentValue(newValue);
	}

	public Item getItem () {
		return item;
	}

	public User getOwner () {
		return owner;
	}

}