import java.util.*;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.Naming;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.UnmarshalException;
import java.net.MalformedURLException;

import java.io.Serializable;

public class Client 
		extends UnicastRemoteObject
		implements Serializable, RmiClientCallbackIntf {
	
	private User me;
	private RmiServerIntf server;

	public Client () throws Exception {
		super(0);
		me = null;
		connectToServer();
	}

	/*************************************/
	/** 			RMI Methods			**/
	/*************************************/

	// print a message to the user updating him about an item that he is interested
	// this method is called via RMI by the auction thread
	public void auctionBiddingUpdate (Item item) throws RemoteException {
		System.out.println(item.toString());
	}

	// print a message to the user stating that an auction in which he was interested is closed and showing the result
	// this method is called via RMI by the auction thread
	public void auctionClosed (String message) throws RemoteException {
		System.out.println(message);
	}

	public boolean isAlive () throws RemoteException {
		return true;
	}

	/*************************************/
	/**		 	Helper Methods			**/
	/*************************************/

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

	// keep trying to connect to the server until it succeeds
	protected void connectToServer () throws Exception {

		try {
			// try connection with the server
			server = (RmiServerIntf)Naming.lookup("//" + TypesNConst.serverIp + "/CentralServer");
		} catch (ConnectException e) {
			// if it is inaccessible sleep for a while...
			System.out.println("The server is inaccessible now. Trying again in 5 seconds");
			try {
				Thread.sleep(TypesNConst.RECONECTION_WAITING_TIME);
			} catch (InterruptedException ie) {}
		} catch (Exception e) {
			throw e;
		} finally {
			long waitingTime = TypesNConst.RECONECTION_WAITING_TIME;
			while (true) {
				try {
					// ...and keep trying again until succeed
					server = (RmiServerIntf)Naming.lookup("//" + TypesNConst.serverIp + "/CentralServer");
					System.out.println("Connected to server");
					break;
				} catch (ConnectException e) {
					// double the waiting time is optional
					waitingTime *= 2;
					System.out.println("The server is still inaccessible. Trying again in " + Long.toString(waitingTime/1000) + " seconds");
					// go to sleep every time it fails to connect to the server
					try {
						Thread.sleep(waitingTime);
					} catch (InterruptedException ie) {}
				} catch (Exception e) {
					throw e;
				}
			}
		}

	}

	// log a user in with the server (calling an RMI method)
	protected boolean login (String name) {

		try {
			// send name to server for the first time
			me = server.login(name, this);
		} catch (ConnectException ce) {
			// server is disconnected
			// connect and try again
			connectToServer();
			me = server.login(name, this);
		} catch (Exception e) {
			// if there is an already logged in user refresh the users list...
			server.refreshUsersList(); // TODO: can this throw an exception?
			try {
				// ...and try logging in again
				me = server.login(name, this);
				// this can fail again (there really is an user with this name already logged in)
			} catch (ConnectException ce) {
				// server is disconnected
				connectToServer();
				me = server.login(name, this);
			} catch (Exception ee) {}
		} finally {
			// return if the login was successful or not
			if (me != null)
				return true;
			return false;
		}

	}

	// log a user out with the server (calling an RMI method)
	protected void logout () throws Exception {
		try {
			server.logout(me);
		} catch (UnmarshalException ue) {
			System.out.println("There was an error while processing your request");
		}
	}

	// for testing purposes only
	protected void newItem (String name, float minimumValue, Calendar closingDatetime, Calendar removalDatetime) throws RemoteException {
		try {
			if (me.isConnected())
				try {
					me.disconnect();
				} catch (Exception e) {}
			server.createAuctionItem (me, name, minimumValue, closingDatetime, removalDatetime);
		} catch (UnmarshalException ue) {}
	}

	// create new item for auction
	private void newItem () throws RemoteException {
		
		//*
		String input;

		// get the item and auction fields
		System.out.println("Enter the item name: ");
		while ((input = System.console().readLine()).length() == 0) {
			System.out.println("This field is mandatory");
			System.out.println("Enter the item name: ");
		}
		String itemName = input;

		System.out.println("Enter the item's start value: ");
		while ((input = System.console().readLine()).length() == 0) {
			System.out.println("This field is mandatory");
			System.out.println("Enter the item's start value: ");
		}
		float minimumValue = Float.parseFloat(input);

		System.out.println("Enter the duration of the auction in minutes: ");
		while ((input = System.console().readLine()).length() == 0) {
			System.out.println("This field is mandatory");
			System.out.println("Enter the duration of the auction in minutes: ");
		}
		int time = Integer.parseInt(input);
		Calendar closingDatetime = GregorianCalendar.getInstance();
		closingDatetime.add(Calendar.MINUTE, time);

		System.out.println("Enter the max removal time of the auction in minutes(blank for the standard 2 min): ");
		input = System.console().readLine();
		Calendar removalDatetime = null;
		if (input.length() > 0) {
			time = Integer.parseInt(input);
			removalDatetime = GregorianCalendar.getInstance();
			removalDatetime.add(Calendar.MINUTE, time);
		}
		
		// call the server RMI method to create a new auction
		try {
			server.createAuctionItem (me, itemName, minimumValue, closingDatetime, removalDatetime);
		} catch (UnmarshalException ue) {
			System.out.println("There was an error while processing your request");
		}
	}

	protected void bid (int auctionId, float value) throws RemoteException {
		
		try {
			RmiAuctionThreadIntf auctionThread = server.getAuctionThread(auctionId);
			if (auctionThread == null)
				return;
			auctionThread.bid(value, me);
		} catch (UnmarshalException ue) {}
	}
	
	private void bid () throws RemoteException {

		String input;

		System.out.println("Enter the auction number you want to bid: ");
		while ((input = System.console().readLine()).length() == 0) {
			System.out.println("This field is mandatory");
			System.out.println("Enter the auction number you want to bid: ");
		}
		int auctionId = Integer.parseInt(input);

		System.out.println("Enter your bid: ");
		while ((input = System.console().readLine()).length() == 0) {
			System.out.println("This field is mandatory");
			System.out.println("Enter your bid: ");
		}
		float value = Float.parseFloat(input);

		try {
			RmiAuctionThreadIntf auctionThread = server.getAuctionThread(auctionId);
			if (auctionThread == null) {
				System.out.print("This auction doesn't exist (did you type the auction number correctly?)");
				return;
			}
			
			switch (auctionThread.bid(value, me)) {
				case SUCCESS:
					System.out.print("You just bid " + Float.toString(value));
					break;
				case VALUE_LOWER: 
					System.out.print("The bid value is lower or equal to the current item value");
					break;
				case IS_OWNER:
					System.out.print("You can't bid on your own item");
					break;
			} 
		} catch (UnmarshalException ue) {
			System.out.println("There was an error while processing your request");
		}
	}

	protected void listAllNoPrint () throws RemoteException {
		
		List<Auction> auctions;
		try {
			auctions = server.getAllAuctions();
		} catch (UnmarshalException ue) {
			return;
		}
		
		if (auctions.size() > 0)
			for (Auction a : auctions)
				a.toString();

	}

	private void listAll () throws RemoteException {
		
		List<Auction> auctions;
		try {
			auctions = server.getAllAuctions();
		} catch (UnmarshalException ue) {
			System.out.println("There was an error while processing your request");
			return;
		}
		
		if (auctions.size() > 0)
			for (Auction a : auctions)
				System.out.println(a.toString());
		else
			System.out.println("There are no auctions at the moment");

	}

	protected void listAvailableNoPrint () throws RemoteException {
		
		List<Auction> auctions;
		try {
			auctions = server.getOpenAuctions();
		} catch (UnmarshalException ue) {
			return;
		}
		
		if (auctions.size() > 0)
			for (Auction a : auctions)
				a.toString();
	}

	private void listAvailable () throws RemoteException {
		
		List<Auction> auctions;
		try {
			auctions = server.getOpenAuctions();
		} catch (UnmarshalException ue) {
			System.out.println("There was an error while processing your request");
			return;
		}
		
		if (auctions.size() > 0)
			for (Auction a : auctions)
				System.out.println(a.toString());
		else
			System.out.println("There are no open auctions at the moment");
	}

	public static void main(String[] args) throws Exception {
		
		Client client = new Client();
		String name;
		TypesNConst.UserOptions option;
		boolean isLoggedIn = false;

		// connect to the server

		// try to log the user in until success or user quits
		do {
			System.out.print("Username: ");
			name = System.console().readLine();

			if (!client.login(name))
				System.out.println("Login failed. There already is someone logged in with this username.");
			else
				isLoggedIn = true;
			
			if (name == null || !isLoggedIn)
				continue;

			try {
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
			} catch (ConnectException ce) {
				System.out.println("The server is inaccessible. Trying to reconnect");
				client.connectToServer();
			}

		} while (true);

	}

}