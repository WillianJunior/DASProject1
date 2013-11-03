import java.util.*;
import java.io.Serializable;

public class User implements Serializable {
	
	private String name;
	private boolean connected;
	private Map<Item, TypesNConst.UserType> items;
	private Client client;

	public User (String name, Client client) {
		this.name = name;
		this.client = client;
		items = new HashMap<Item, TypesNConst.UserType>();
		connected = true;
	}

	public boolean equals (User user) {
		return (user.getName().equals(name));
	}

	public boolean isConnected () {
		return connected;
	}

	public void connect (Client client) throws Exception {
		if (!connected) {
			this.client = client;
			connected = true;
		} else
			throw new Exception ("Client already connected");
	}

	public void disconnect () throws Exception {
		if (connected) {
			client = null;
			connected = false;
		} else
			throw new Exception ("Client already disconnected");
	}

	public Client getClient () {
		return client;
	}

	public String getName () {
		return name;
	}

	// item that the user is bidding
	public void newBidableItem (Item item) {
		items.put(item, TypesNConst.UserType.BIDDER);
	}

	// item that the user want to sell
	public void newSellingItem (Item item) {
		items.put(item, TypesNConst.UserType.OWNER);
	}



}