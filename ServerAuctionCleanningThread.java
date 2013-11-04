import java.util.*;

public class ServerAuctionCleanningThread implements Runnable {
	
	private volatile List<Auction> auctions; // references the auctions list from Server
	//private volatile List<Auction> openAuctions; // references the openAuctions list from Server
	//private volatile List<Auction> closedAuctions; // references the closedAuctions list from Server

	private TreeMap<Integer, Auction> endAuctionTime;

	public ServerAuctionCleanningThread (List<Auction> auctions, List<Auction> openAuctions, List<Auction> closedAuctions) {
		this.auctions = auctions;
		//this.openAuctions = openAuctions;
		//this.closedAuctions = closedAuctions;
		endAuctionTime = new TreeMap<Integer, Auction>();
	}

	@Override
	public void run () {

		Integer timeout;
		Auction auctionCloseToEnd;
		
		while (true) {                  // what happens if the thread is not sleeping when it is notifyed
			while (endAuctionTime.isEmpty()) // is it possible to lock waitTimes before and unlock it after isEmpty? NOW: this can cause inconsistency
				try {
					this.wait();
				} catch (InterruptedException e) {}

			synchronized (endAuctionTime) {
				timeout = endAuctionTime.firstEntry().getKey();
				auctionCloseToEnd = endAuctionTime.firstEntry().getValue();
				endAuctionTime.remove(timeout);

			}

			try {
				Thread.sleep(timeout); // how to make a sleep that wakes up at the signal
			} catch (InterruptedException e) {}
			
			// update the auction status
			synchronized (auctions) {
				try {
					auctions.get(auctions.indexOf(auctionCloseToEnd)).closeAuction();
				} catch (Exception e) {}
			}



		}

	}

	//public addWaitTime
}