import java.util.*;
import java.io.Serializable;

public class User implements Serializable {
	
	private String name;
	private boolean connected;
	private Client client;

	public User (String name, Client client) {
		this.name = name;
		this.client = client;
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
	
}