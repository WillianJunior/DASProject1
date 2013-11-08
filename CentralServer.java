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
	private int itemIdCounter;

	public CentralServer () throws RemoteException {
		super(0);
		users = new ArrayList<User>();
		auctions = new HashMap<Auction, RmiAuctionThreadIntf>();
		//openAuctions = new ArrayList<Auction>();
		//closedAuctions = new ArrayList<Auction>();
		itemIdCounter = 0;
		auctionIdCounter = 0;
	}

	// log an user in the server by instanciating a new User
	public User login (String username, RmiClientCallbackIntf client) throws Exception, RemoteException {
		
		User user = findUser(username);

		if (user == null) {
			System.out.println("[login] new user: " + username);
			user = new User(username, client);
			users.add(user);
		} else {
			System.out.println("[login] user exists");
			user.connect(client);
		}

		// broadcast to all auctions threads that the user is connected, so callback is needed and shoud be ennabled again
		for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : auctions.entrySet()) {
			entry.getValue().notifyUserLogin(user);
		}

		return user;
	}

	public void logout (User user) throws Exception, RemoteException {
		User u = findUser(user);
		System.out.println("[logout] " + u.getName());
		u.disconnect();

		// broadcast to all auctions threads that the user is disconected, so no callback is needed
		for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : auctions.entrySet()) {
			entry.getValue().notifyUserLogout(u);
		}
	}

	public void createAuctionItem (User user, String name, float minimumValue, Calendar closingDatetime, Calendar removalDatetime) throws RemoteException {
		
		// create an item
		Item item  = new Item(getUniqueItemId(), name, minimumValue);

		// create an auction
		Auction auction = new Auction(getUniqueAuctionId(), item, user, closingDatetime, removalDatetime);

		// start auction thread
		Runnable auctionThread = new AuctionThread(auction, this);
		new Thread(auctionThread).start();

		// insert the auction into the list
		auctions.put(auction, (RmiAuctionThreadIntf)auctionThread);

		/*
		synchronized (auctions) { // is there a better way to be sure that there will never be two auctions access at the same time?
			Auction auction = findAuction(item, user);

			if (auction == null) {
				System.out.println("[createAuction] new auction");
				auction = new Auction(getUniqueAuctionId(), item, user, closingDatetime, removalDatetime);
				auctions.add(auction);
				//openAuctions.add(auction);

				// this was supposed to pass the reference of the recently created auction
				//new Thread(new ServerAuctionThread(auctions.get(auctions.getIndex(auction))));
				
			} else
				throw new Exception ("This auction already exists");
		}*/

	}

	public List<Auction> getAllAuctions () throws RemoteException {
		
		List<Auction> auctions = new ArrayList<Auction>();
		
		for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : this.auctions.entrySet())
			auctions.add(entry.getKey());

		return auctions;
	}

	public List<Auction> getOpenAuctions () throws RemoteException {
		
		List<Auction> openAuctions = new ArrayList<Auction>();

		for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : this.auctions.entrySet())
			if (!entry.getKey().isClosed())
				openAuctions.add(entry.getKey());

		return openAuctions;
	}	

	public RmiAuctionThreadIntf getAuctionThread (int auctionId) throws RemoteException {
		
		for (Map.Entry<Auction, RmiAuctionThreadIntf> entry : this.auctions.entrySet()) {
			if (entry.getKey().getId() == auctionId && !entry.getKey().isClosed()) {
				return entry.getValue();
			}
		}

		return null;

	}

	public void removeAuction (Auction auction) throws RemoteException {
		auctions.remove(auction);
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

	private int getUniqueItemId () {
		return itemIdCounter++;
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
        CentralServer obj = new CentralServer();
 
        // Bind this object instance to the name "RmiServer"
        Naming.rebind("//localhost/CentralServer", obj);
        System.out.println("PeerServer bound in registry");

	}

}