import java.util.*;

public class Auction {
	
	private Item item;
	private User owner;
	private List<User> bidders;
	private boolean closed;

	public Auction (Item item, User owner) {
		this.item = item;
		this.owner = owner;
		closed = false;
	}

	public void closeAuction () {
		closed = true;
	}

	public boolean isClosed () {
		return closed;
	}

}