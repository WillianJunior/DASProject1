import java.util.*;

public class User {
	
	private String name;
	private boolean connected;
	private Map<Item, TypesNConst.UserType> items;

	public User (String name) {
		this.name = name;
		connected = true;
	}

	public boolean isConnected () {
		return connected;
	}

}