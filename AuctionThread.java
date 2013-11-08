import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import java.util.*;
import java.io.Serializable;

public class AuctionThread 
		extends UnicastRemoteObject
		implements Serializable, RmiAuctionThreadIntf, Runnable {

	private volatile Auction auction;
	private List<User> bidders;
	private User highestBidder;
	private boolean open;

	public AuctionThread (Auction auction, CentralServer server) throws RemoteException {
		super(0);
		this.auction = auction; // not referencing the map value
		bidders = new ArrayList<User>();
		highestBidder = null;
		open = true;
		// create the timeout and removal thread
		new Thread(new AuctionTimeoutThread(this, server, auction.getClosingDate(), auction.getRemovalDate())).start();

	}

	public synchronized boolean bid (float value, User bidder) throws RemoteException {
		
		if (open) {
			// add bidder to the bidders list only if it isn't already there
			if (findUser(bidder) == null)
				bidders.add(bidder);

			// check if the bid is valid
			if (value > auction.getCurrentValue()) {
				// if valid, update the value and notify (who do I notify?)
				auction.updateCurrentValue(value);
				highestBidder = bidder;
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

		return false;
	}

	public void	notifyUserLogin (User user) throws RemoteException, Exception {
		if (user.equals(auction.getOwner()))
			auction.getOwner().connect(user.getClient());
	}

	public void	notifyUserLogout (User user) throws RemoteException, Exception {
		if (user.equals(auction.getOwner()))
			auction.getOwner().disconnect();
	}

	public Auction closeAuction () throws RemoteException {

		synchronized (this) { // does this guarantee that a bid cannot happen when the auction is closing?
			String[] auctionResult = {"Auction no " + Integer.toString(auction.getId()) + " is closed", 
					"Item no" + Integer.toString(auction.getItem().getId()) + ": " + auction.getItem().getName(),
					"Owner: " + auction.getOwner().getName(),
					"Original value: " + Float.toString(auction.getMinimumValue()),
					"Item sold for " + Float.toString(auction.getCurrentValue()) + " to " + highestBidder.getName()};
			String auctionWinner = "Congratulations, you are the auction no" + Integer.toString(auction.getId()) + " winner";
			String noWinners = "No one bid on your item";

			open = false;
			UnicastRemoteObject.unexportObject(this, true);

			if (highestBidder == null) {
				auction.getOwner().getClient().auctionClosed(noWinners);
			} else {
				for (User u : bidders) {
					for (String s : auctionResult) {
						u.getClient().auctionClosed(s);
					}
				}
				highestBidder.getClient().auctionClosed(auctionWinner);
			}
			return auction;
		}

	}

	public void run () {}

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