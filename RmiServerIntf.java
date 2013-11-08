import java.util.*;
import java.util.Calendar;

import java.rmi.Remote;
import java.rmi.RemoteException;
 
public interface RmiServerIntf extends Remote {
    // log an user in the server by instanciating a new User
	public User login (String username, RmiClientCallbackIntf client) throws Exception, RemoteException;
	public void logout (User user) throws Exception, RemoteException;
	public void createAuctionItem (User user, String name, float minimumValue, Calendar closingDatetime, Calendar removalDatetime) throws RemoteException;
	public List<Auction> getAllAuctions () throws RemoteException;
	public List<Auction> getOpenAuctions () throws RemoteException;
	public RmiAuctionThreadIntf getAuctionThread (int auctionId) throws RemoteException;
	public void removeAuction (Auction auction) throws RemoteException;

}