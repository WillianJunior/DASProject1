import java.util.*;
import java.lang.Thread.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*; 

public class CentralServer 
	extends UnicastRemoteObject 
    implements RmiServerIntf {
	
	private List<User> users;
	private volatile List<Auction> auctions;
	//private volatile List<Auction> openAuctions; // references the auction list
	//private volatile List<Auction> closedAuctions; // references the auction list: auctions = openAuctions + closedAuctions : do it later
	private int auctionIdCounter; // TODO: migrate the auction and item creation to a factory
	private int itemIdCounter;

	public CentralServer () throws RemoteException {
		super(0);
		users = new ArrayList<User>();
		auctions = new ArrayList<Auction>();
		//openAuctions = new ArrayList<Auction>();
		//closedAuctions = new ArrayList<Auction>();
		itemIdCounter = 0;
		auctionIdCounter = 0;
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

	public void createAuction (Item item, User user, Calendar closingDatetime, Calendar removalDatetime) throws Exception {
		
		synchronized (auctions) { // is there a better way to be sure that there will never be two auctions access at the same time?
			Auction auction = findAuction(item, user);

			if (auction == null) {
				System.out.println("[createAuction] new auction");
				auction = new Auction(getUniqueAuctionId(), item, user, closingDatetime, removalDatetime);
				auctions.add(auction);
				//openAuctions.add(auction);

				// this was supposed to pass the reference of the recently created auction
				new Thread(new ServerAuctionThread(auctions.get(auctions.getIndex(auction))));
				
			} else
				throw new Exception ("This auction already exists");
		}

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

	// this will probably be merged with find user to create a generic functions (yes, with generics)
	private Auction findAuction (Auction auction) {
		for (Auction a : auctions) {
			if (a.equals(auction)) {
				System.out.println("[findAuction] found: " + a.prettyPrint());
				return a;
			}
		}
		return null;
	}

	private Auction findAuction (Item item, User owner) {
		for (Auction a : auctions) {
			if (a.equals(item, owner)) {
				System.out.println("[findAuction] found: " + a.prettyPrint());
				return a;
			}
		}
		return null;
	}

	private int getUniqueAuctionId () {
		return auctionIdCounter++;
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