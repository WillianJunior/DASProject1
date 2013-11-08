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
			System.out.println("[AuctionTimeoutThread] run");
			System.out.println("[AuctionTimeoutThread] mill to first sleep: " + Long.toString(closingTime.getTimeInMillis() - GregorianCalendar.getInstance().getTimeInMillis()));
			try {
				System.out.println("[AuctionTimeoutThread] first sleep");
				Thread.sleep(closingTime.getTimeInMillis() - GregorianCalendar.getInstance().getTimeInMillis());
				System.out.println("[AuctionTimeoutThread] done");
			} catch (InterruptedException e) {}
			Auction auction = auctionThread.closeAuction();
			System.out.println("[AuctionTimeoutThread] mill to second sleep: " + Long.toString(removalTime.getTimeInMillis() - GregorianCalendar.getInstance().getTimeInMillis()));
			try {
				System.out.println("[AuctionTimeoutThread] second sleep");
				Thread.sleep(removalTime.getTimeInMillis() - GregorianCalendar.getInstance().getTimeInMillis());
				System.out.println("[AuctionTimeoutThread] done");
			} catch (InterruptedException e) {}
			server.removeAuction(auction);
		} catch (RemoteException e) { /* need to fid out wht to do with this exeptions*/ }

		System.out.println("[AuctionTimeoutThread] i'm out");
	}

}