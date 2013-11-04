import java.util.*;

public class Auction {
	
	private int id;
	private Item item;
	private User owner;
	private boolean closed;
	private Calendar closingDatetime;
	private Calendar removalDatetime;

	public Auction (int id, Item item, User owner, Calendar closingDatetime, Calendar removalDatetime) {
		this.id = id;
		this.item = item;
		this.owner = owner;
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

	public String prettyPrint () {
		return ("Auction number " + Integer.toString(id) + ". Item: " + item.getName() + ". Owner: " + owner.getName() + ".");
	}

}