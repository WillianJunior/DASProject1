import java.util.*;
import java.io.Serializable;
import java.rmi.RemoteException;

public class User implements Serializable {
	
	private String name;
	private boolean connected;
	private RmiClientCallbackIntf client;

	public User (String name, RmiClientCallbackIntf client) {
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

	public void connect (RmiClientCallbackIntf client) throws Exception {
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

	public RmiClientCallbackIntf getClient () {
		return client;
	}

	public String getName () {
		return name;
	}

}