import java.util.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*; 

public class Server 
	extends UnicastRemoteObject 
    implements RmiServerIntf {
	
	static private List<User> users;
	static private List<Auction> auctions;

	public Server () throws RemoteException {
		super(0);
		users = new ArrayList<User>();
		auctions = new ArrayList<Auction>();
	}

	// log an user in the server by instanciating a new User
	public User login (String username, Client client) throws Exception{
		
		User user = findUser(username);

		if (user == null) {
			System.out.println("[login] new user: " + username);
			user = new User(username, client);
			users.add(user);
			return user;
		} else {
			System.out.println("[login] user exists");
			user.connect(client);
			return user;
		}
		
	}

	public void logout (User user) throws Exception {
		User u = findUser(user);
		System.out.println("[logout] " + u.getName());
		u.disconnect();
	}

	public void createAuction (Item item, User user) {

	}

	private User findUser (String username) {
		for (User u : users) {
			if (u.getName().equals(username)) {
				System.out.println("[findUser] found: " + u.getName());
				return u;
			}
		}
		return null;
	}

	private User findUser (User user) {
		for (User u : users) {
			if (u.equals(user)) {
				System.out.println("[findUser] found: " + u.getName());
				return u;
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		
		// bootstrap the server, if needed

		// start network protocol
		System.out.println("RMI server started");
 
        try { //special exception handler for registry creation
            LocateRegistry.createRegistry(1099); 
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
        }
 
        //Instantiate RmiServer
        Server obj = new Server();
 
        // Bind this object instance to the name "RmiServer"
        Naming.rebind("//localhost/Server", obj);
        System.out.println("PeerServer bound in registry");

	}

}