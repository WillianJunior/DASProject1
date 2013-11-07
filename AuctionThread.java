import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import java.util.*;
import java.io.Serializable;

public class AuctionThread 
		extends UnicastRemoteObject
		implements Serializable, RmiAuctionThreadIntf, Runnable {

	private volatile Auction auction;
	private List<User> bidders;

	public AuctionThread (Auction auction) throws RemoteException {
		super(0);
		this.auction = auction;
		bidders = new ArrayList<User>();

		// create the timeout and removal threads (1 or 2?)
	}

	public synchronized boolean bid (float value, User bidder) throws RemoteException {
		
		// add bidder to the bidders list
		
		if (findUser(bidder) == null)
			bidders.add(bidder);

		// check if the bid is valid
		if (value > auction.getCurrentValue()) {
			// if valid, update the value and notify (who do I notify?)
			auction.updateCurrentValue(value);
			// notify the owner only if he is online
			if (auction.getOwner().isConnected())
				auction.getOwner().getClient().auctionBiddingUpdate(auction.getItem());
			System.out.println("[AuctionThread.bid] bid is valid for item " + Integer.toString(auction.getItem().getId()) + ". new value: " + Float.toString(auction.getCurrentValue()));
			return true;
		} else {
			System.out.println("[AuctionThread.bid] bid not valid, lower than the minimum value");
			return false;
		}
	}

	public void	notifyUserLogin (User user) throws RemoteException, Exception {
		if (user.equals(auction.getOwner()))
			auction.getOwner().connect(user.getClient());

	}

	public void	notifyUserLogout (User user) throws RemoteException, Exception {
		if (user.equals(auction.getOwner()))
			auction.getOwner().disconnect();

	}

	public void run () {

	}

	private User findUser (User user) {
		for (User u : bidders) {
			if (u.equals(user)) {
				System.out.println("[AuctionThread.findUser] found: " + u.getName());
				return u;
			}
		}
		return null;
	}


}