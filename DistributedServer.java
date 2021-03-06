import java.util.*;
import java.lang.Thread.*;
import java.util.concurrent.ConcurrentHashMap;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*; 

public class DistributedServer 
	extends UnicastRemoteObject 
    implements RmiServerIntf {
	
	private List<User> users;
	
	/* volatile Map<Auction, RmiAuctionThreadIntf> auctions
	 * This variable have two fields, the auction object and the thread 
	 * reference for RMI.
	 * The thread reference can only be set by the main thread (this, 
	 * the central server), so, it do not need any type of lock to access it
	 * since this is the only thread that can make insertions and deletions, 
	 * no lock is needed to any crd (crud minus the update) operation.
	 * The only lock that would be needed is the one to guarantee that no 
	 * two threads would access the same auction at the same time.
	 * The central server only access the auctions on the map to return the 
	 * list of auctions to the client, and the threads updates the auctions
	 * However, since the only update (so far...) is the min value update, 
	 * that is done atomicly (is it?), no lock is needed there as well.
	 * Concluding: auctions is safe and don't need any lock (yet...).
	 */
	private volatile Map<Auction, RmiAuctionThreadIntf> auctions;
	//private volatile List<Auction> openAuctions; // references the auction list
	//private volatile List<Auction> closedAuctions; // references the auction list: auctions = openAuctions + closedAuctions : do it later
	private int auctionIdCounter; // TODO: migrate the auction and item creation to a factory

	public DistributedServer () throws RemoteException {
		super(0);
		users = new ArrayList<User>();
		auctions = new HashMap<Auction, RmiAuctionThreadIntf>();
		auctionIdCounter = 0;
	}

	/*************************************/
	/** 			RMI Methods			**/
	/*************************************/

	// log an user in the server by instanciating a new User or connection an old one
	public User login (String username, RmiClientCallbackIntf client) throws Exception, RemoteException {
		
		// search for the user to check if it needs either to be signed in or created
		User user = findUser(username);

		if (user == null) {
			// if the user is a new one, create it
			System.out.println("[DistributedServer.login] new user: " + username);
			user = new User(username, client);
			users.add(user);
		} else {
			// if the user is just loggin in
			System.out.println("[DistributedServer.login] user exists");
			user.connect(client);
			// broadcast to all auctions threads that the user is connected, so callback is needed and shoud be ennabled again
			for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : auctions.entrySet())
				// check if the thread reference still exists before notifying
				// e.g. auction is closed but not removed
				if (entry.getValue() != null)
					entry.getValue().notifyUserLogin(user);
		}

		return user;
	}

	// log an user out, which means that no callback is needed
	public void logout (User user) throws Exception, RemoteException {
		
		// find the user and update its state to disconnected
		User u = findUser(user);
		System.out.println("[DistributedServer.logout] " + u.getName());
		u.disconnect();

		// broadcast to all auctions threads that the user is disconected, so no callback shoud be disabled
		for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : auctions.entrySet())
			// check if the thread reference still exists before notifying
			// e.g. auction is closed but not removed
			if (entry.getValue() != null)
				entry.getValue().notifyUserLogout(u);

	}

	// create a new item, an auction for the item and the needed threads to serve it
	public void createAuctionItem (User user, String name, float minimumValue, Calendar closingDatetime, Calendar removalDatetime) throws RemoteException {
		
		// create an item
		Item item  = new Item(name, minimumValue);

		// create an auction
		Auction auction = new Auction(getUniqueAuctionId(), item, user, closingDatetime, removalDatetime);

		// start auction thread
		//Runnable auctionThread = new AuctionThread(auction, this);
		//new Thread(auctionThread).start();

		// insert the auction into the list
		//auctions.put(auction, (RmiAuctionThreadIntf)auctionThread);

	}

// force the refresh funciton to check if the users are still online
	public void refreshUsersList () throws RemoteException {
		
	}

	// return all auctions
	public List<Auction> getAllAuctions () throws RemoteException {
		
		List<Auction> auctions = new ArrayList<Auction>();
		
		for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : this.auctions.entrySet())
			auctions.add(entry.getKey());

		return auctions;
	}

	// return the open auctions that haven't been removed
	public List<Auction> getOpenAuctions () throws RemoteException {
		
		List<Auction> openAuctions = new ArrayList<Auction>();

		for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : this.auctions.entrySet())
			if (!entry.getKey().isClosed())
				openAuctions.add(entry.getKey());

		return openAuctions;
	}	

	// return the thread responsable for the auction represented by the auctionId. 
	// this method can (and will) be called concurrently
	public RmiAuctionThreadIntf getAuctionThread (int auctionId) throws RemoteException {
		
		for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : this.auctions.entrySet())
			if (entry.getKey().getId() == auctionId && !entry.getKey().isClosed())
				return entry.getValue();

		return null;

	}

	// remove the reference to the auction thread (this is an attempt to finish the auction thread)
	public void closeAuction (Auction auction) throws RemoteException {
		auctions.put(auction, null);
	}

	// remove an auction from the auction list
	public void removeAuction (Auction auction) throws RemoteException {
		synchronized (auctions) {
			auctions.remove(auction);
		}
	}

	/*************************************/
	/**		 	Helper Methods			**/
	/*************************************/

	// return an user given his username
	private User findUser (String username) {
		for (User u : users)
			if (u.getName().equals(username)) {
				System.out.println("[DistributedServer.findUser] found: " + u.getName());
				return u;
			}
		
		return null;
	}
	
	private int getUniqueAuctionId () {
		return auctionIdCounter++;
	}

	// return an user using the equals as the comparison algorithm
	private User findUser (User user) {
		for (User u : users) 
			if (u.equals(user)) {
				System.out.println("[DistributedServer.findUser] found: " + u.getName());
				return u;
			}
		
		return null;
	}

	// this will probably be merged with find user to create a generic functions (yes, with generics)
	/*
	private Auction findAuction (Auction auction) {
		for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : this.auctions.entrySet())
			if (entry.getKey().equals(auction)) {
				System.out.println("[findAuction] found: " + entry.getKey().toString());
				return entry.getKey();
			}
		
		return null;
	}

	private Auction findAuction (Item item, User owner) {
		for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : this.auctions.entrySet())
			if (entry.getKey().equals(item, owner)) {
				System.out.println("[findAuction] found: " + entry.getKey().toString());
				return entry.getKey();
			}
		
		return null;
	}
	*/


	/*************************************/
	/** 	Main Server Methods 		**/
	/*************************************/

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
        DistributedServer obj = new DistributedServer();
 
        // Bind this object instance to the name "RmiServer"
        Naming.rebind("//" + TypesNConst.serverIp + "/DistributedServer", obj);
        System.out.println("PeerServer bound in registry");

	}

}