import java.util.*;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.Naming;

import java.io.Serializable;

public class Client 
		extends UnicastRemoteObject
		implements Serializable, RmiClientCallbackIntf {
	
	private User me;
	private RmiServerIntf server;

	public Client () throws Exception, RemoteException {
		super(0);
		me = null;
		server = (RmiServerIntf)Naming.lookup("//localhost/CentralServer");
	}

	private static void print_options () {
		System.out.println();
		System.out.println("*************************************");
		System.out.println("* 1 - Create Auction Item           *");
		System.out.println("* 2 - Bid Item                      *");
		System.out.println("* 3 - List All Items                *");
		System.out.println("* 4 - List Available Items          *");
		System.out.println("* 5 - Quit                          *");
		System.out.println("*************************************");

	}

	private boolean login (String name) throws Exception {

		// send name to server

		if ((me = server.login(name, this)) == null) {
			// login failed
			return false;
		}
		return true;

	}

	private void logout () throws Exception {
		server.logout(me);
	}

	public void newItem () throws RemoteException {
		
		//System.out.println("New item");
		String itemName = "test";
		float minimumValue = 100;
		Calendar closingDatetime = GregorianCalendar.getInstance();
		closingDatetime.add(Calendar.MINUTE, 2);
		Calendar removalDatetime = GregorianCalendar.getInstance();
		removalDatetime.add(Calendar.MINUTE, 5);
		server.createAuctionItem (me, itemName, minimumValue, closingDatetime, removalDatetime);
	}

	private void bid () throws RemoteException {
		//System.out.println("Bid");
		int auctionId = 0;
		float value = 120;
		System.out.print("bid: ");
		String bid = System.console().readLine();
		value = Float.parseFloat(bid);
		System.out.println();
		RmiAuctionThreadIntf auctionThread = server.getAuctionThread(auctionId);
		
		switch (auctionThread.bid(value, me)) {
			case NO_ERROR:
				System.out.print("You just bid " + Float.toString(value));
				break;
			case AUCTION_CLOSED:
				System.out.print("This auction is already closed");
				break;
			case VALUE_LOWER: 
				System.out.print("The bid value is lower or equal to the current item value");
				break;
		} 
	}

	private void listAll () throws RemoteException {
		
		List<Auction> auctions = server.getAllAuctions();	
		
		if (auctions.size() > 0)
			for (Auction a : auctions)
				System.out.println(a.toString());
		else
			System.out.println("There are no auctions at the moment");

	}

	public void listAvailable () throws RemoteException {
		
		List<Auction> auctions = server.getOpenAuctions();
		
		if (auctions.size() > 0)
			for (Auction a : auctions)
				System.out.println(a.toString());
		else
			System.out.println("There are no open auctions at the moment");
	}

	public void auctionBiddingUpdate (Item item) throws RemoteException {
		System.out.println(item.toString());
	}

	public void auctionClosed (String message) throws RemoteException {
		System.out.println(message);
	}

	public static void main(String[] args) throws Exception {
		
		Client client = new Client();
		String name;
		TypesNConst.UserOptions option;

		// connect to the server

		// try to log the user in until success or user quits
		do {
			System.out.print("Username: ");
			name = System.console().readLine();

			//try {
				client.login(name);
			//} catch (Exception e) {
			//	System.out.println(e.getMessage());
			//	name = null; // probably there is a better way to signal inside the while that an exception just occured
			//}
			
		} while (name == null);

		while (true) {
			// show the options
			print_options();
			System.out.println("Enter option: ");

			String opt = System.console().readLine();
			if ((option = TypesNConst.UserOptions.fromString(opt)) == null) {
				try {
					option = TypesNConst.UserOptions.fromInt(Integer.parseInt(opt));
				} catch (NumberFormatException e) {option = null;}
			}
			
			if (option != null) {
				switch (option) {
					case CREATE_AUCTION_ITEM:
						client.newItem();
						break;
					case BID_ITEM:
						client.bid();
						break;
					case LIST_ALL_ITEMS:
						client.listAll();
						break;
					case LIST_AVAILABLE_ITEMS:
						client.listAvailable();
						break;
					case QUIT:
						// close the conection with the server and quits
						System.out.println("Bye");
						client.logout();
						UnicastRemoteObject.unexportObject(client, true);
						return;
				}
			} else
				System.out.println("Inexistent option, try again");
		}

	}

}