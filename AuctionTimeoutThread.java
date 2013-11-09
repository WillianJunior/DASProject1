import java.util.*;
import java.rmi.RemoteException;

public class AuctionTimeoutThread implements Runnable {

	private RmiAuctionThreadIntf auctionThread;
	private RmiServerIntf server;
	private Calendar closingTime;
	private Calendar removalTime;

	public AuctionTimeoutThread (RmiAuctionThreadIntf auctionThread, RmiServerIntf server, Calendar closingTime, Calendar removalTime) {
		this.auctionThread = auctionThread;
		this.server = server;
		this.closingTime = closingTime;
		this.removalTime = removalTime;
	}

	public void run () {
		try {
			System.out.println("[AuctionTimeoutThread] millis to first sleep: " + Long.toString(closingTime.getTimeInMillis() - GregorianCalendar.getInstance().getTimeInMillis()));
			try {
				Thread.sleep((closingTime.getTimeInMillis() - GregorianCalendar.getInstance().getTimeInMillis())/10);
			} catch (InterruptedException e) {}
			Auction auction = auctionThread.closeAuction();
			server.closeAuction(auction);
			System.out.println("[AuctionTimeoutThread] millis to second sleep: " + Long.toString(removalTime.getTimeInMillis() - GregorianCalendar.getInstance().getTimeInMillis()));
			try {
				Thread.sleep((removalTime.getTimeInMillis() - GregorianCalendar.getInstance().getTimeInMillis())/10);
			} catch (InterruptedException e) {}
			System.out.println("[AuctionTimeoutThread] i'm out");
			server.removeAuction(auction);
		} catch (RemoteException e) { /* need to find out wht to do with this exeptions*/ }

	}

}