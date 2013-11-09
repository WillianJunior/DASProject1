import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import java.util.*;
import java.io.Serializable;

public class AuctionThread 
		extends UnicastRemoteObject
		implements Serializable, RmiAuctionThreadIntf, Runnable {

	private Auction auction;
	private List<User> bidders;
	private User highestBidder;
	private CentralServer server;
	private boolean open;

	public AuctionThread (Auction auction, CentralServer server) throws RemoteException {
		super(0);
		this.auction = auction;
		this.server = server;
		bidders = new ArrayList<User>();
		highestBidder = null;
		open = true;
		// create the timeout and removal thread
		new Thread(new AuctionTimeoutThread(this, server, auction.getClosingDate(), auction.getRemovalDate())).start();

	}

	public synchronized TypesNConst.BiddingReturns bid (float value, User bidder) throws RemoteException {
		
		try {
			Thread.sleep(2000); // simulate the delay
		} catch (Exception e) {System.out.println(e.getMessage());}
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
				System.out.println("[AuctionThread.bid] bid is valid for item " + auction.getItem().getName() + ". new value: " + Float.toString(auction.getCurrentValue()));
				return TypesNConst.BiddingReturns.NO_ERROR;
			} else {
				System.out.println("[AuctionThread.bid] bid not valid, lower than the minimum value");
				return TypesNConst.BiddingReturns.VALUE_LOWER;
			}
		}

		return TypesNConst.BiddingReturns.AUCTION_CLOSED;
	}

	public void	notifyUserLogin (User user) throws RemoteException, Exception {
		if (user.equals(auction.getOwner()))
			auction.getOwner().connect(user.getClient());
	}

	public void	notifyUserLogout (User user) throws RemoteException, Exception {
		if (user.equals(auction.getOwner()))
			auction.getOwner().disconnect();
	}

	public synchronized Auction closeAuction () throws RemoteException {

		String[] auctionResult = {"Auction no" + Integer.toString(auction.getId()) + " is closed", 
				"Item: " + auction.getItem().getName(),
				"Owner: " + auction.getOwner().getName(),
				"Original value: " + Float.toString(auction.getMinimumValue()),
				"Item sold for " + Float.toString(auction.getCurrentValue()) + " to " + highestBidder.getName()};
		String auctionWinner = "Congratulations, you are the auction no" + Integer.toString(auction.getId()) + " winner";
		String noWinners = "No one bid on your item";

		if (highestBidder == null) {
			auction.getOwner().getClient().auctionClosed(noWinners);
		} else {
			for (User u : bidders) {
				for (String s : auctionResult) {
					u.getClient().auctionClosed(s);
				}
			}
			highestBidder.getClient().auctionClosed(auctionWinner);
			for (String s : auctionResult) {
				auction.getOwner().getClient().auctionClosed(s);
			}
		}

		open = false;
		try {
			auction.closeAuction();
		} catch (Exception e) {System.out.println(e.getMessage());}

		server.closeAuction(auction);
		UnicastRemoteObject.unexportObject(this, true);

		return auction;

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