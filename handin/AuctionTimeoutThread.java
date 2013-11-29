import java.util.*;
import java.rmi.RemoteException;

public class AuctionTimeoutThread implements Runnable {

	private RmiAuctionThreadIntf auctionThread;
	private RmiServerIntf server;
	private long closingTime;
	private long removalTime;

	public AuctionTimeoutThread (RmiAuctionThreadIntf auctionThread, RmiServerIntf server, long closingTime, long removalTime) {
		this.auctionThread = auctionThread;
		this.server = server;
		this.closingTime = closingTime;
		this.removalTime = removalTime;
	}

	public void run () {
		try {
			long sleepTime;
			System.out.println("[AuctionTimeoutThread] millis to first sleep: " + Long.toString(closingTime - GregorianCalendar.getInstance().getTimeInMillis()));
			try {
				sleepTime = closingTime - GregorianCalendar.getInstance().getTimeInMillis();
				if (sleepTime > 0)
					Thread.sleep(sleepTime);
			} catch (InterruptedException e) {}
			Auction auction = auctionThread.closeAuction();
			server.closeAuction(auction);
			System.out.println("[AuctionTimeoutThread] millis to second sleep: " + Long.toString(removalTime - GregorianCalendar.getInstance().getTimeInMillis()));
			try {
				sleepTime = removalTime - GregorianCalendar.getInstance().getTimeInMillis();
				if (sleepTime > 0)
					Thread.sleep(sleepTime);
			} catch (InterruptedException e) {}
			System.out.println("[AuctionTimeoutThread] i'm out");
			server.removeAuction(auction);
		} catch (RemoteException e) {}

	}

}