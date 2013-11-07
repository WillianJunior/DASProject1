import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import java.util.*;
import java.io.Serializable;

public class AuctionThread 
		extends UnicastRemoteObject
		implements Serializable, RmiAuctionThreadIntf, Runnable {

	private Auction auction;
	private List<User> bidders;
	private RmiClientCallbackIntf owner;

	public AuctionThread (Auction auction) throws RemoteException {
		super(0);
		this.auction = auction;
		owner = auction.getOwnerCallback();
		bidders = new ArrayList<User>();
	}

	public synchronized boolean bid (float value, User bidder) throws RemoteException {
		
		// add bidder to the bidders list
		bidders.add(bidder);

		// check if the bid is valid
		if (value > auction.getCurrentValue()) {
			// if valid, update the value and notify (who do I notify?)
			auction.updateCurrentValue(value);
			owner.auctionBiddingUpdate(auction.getItem());
			return true;
		} else {
			return false;
		}
	}

	public void run () {
		
	}

}