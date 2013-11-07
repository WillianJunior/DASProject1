import java.util.*;
import java.io.Serializable;

public class Auction implements Serializable {
	
	private int id;
	private Item item;
	private RmiClientCallbackIntf clientCallback;
	private User owner;
	private boolean closed;
	private Calendar closingDatetime;
	private Calendar removalDatetime;

	public Auction (int id, Item item, RmiClientCallbackIntf clientCallback, User owner, Calendar closingDatetime, Calendar removalDatetime) {
		this.id = id;
		this.item = item;
		this.owner = owner;
		this.clientCallback = clientCallback;
		closed = false;
		this.closingDatetime = closingDatetime;
		if (removalDatetime == null) {
			removalDatetime = GregorianCalendar.getInstance();
			removalDatetime.add(Calendar.MINUTE, TypesNConst.MAX_ITEM_REMOVAL_TIME);
		} else
			this.removalDatetime = removalDatetime;
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

	public boolean isClosed (Calendar now) {
		return closingDatetime.after(now);
	}

	public Calendar getClosingDate () {
		return closingDatetime;
	}

	public boolean equals (Auction auction) {
		return id == auction.getId();
	}

	public boolean equals (Item item, User owner) {
		return (item.equals(this.item) && owner.equals(this.owner));
	}

	public String toString () {
		return ("Auction number " + Integer.toString(id) + ". Item: " + item.getName() + ". Owner: " + owner.getName() + ".");
	}

	public String getStatus () {
		return ("Current value: " + item.getCurrentValue() + ". Auction closes at " + closingDatetime.toString());
	}

	public float getCurrentValue () {
		return item.getCurrentValue();
	}

	public void updateCurrentValue (float newValue) {
		item.updateCurrentValue(newValue);
	}

	public RmiClientCallbackIntf getOwnerCallback() {
		return clientCallback;
	}

	public Item getItem () {
		return item;
	}

}