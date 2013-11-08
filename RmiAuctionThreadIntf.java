import java.rmi.Remote;
import java.rmi.RemoteException;
 
public interface RmiAuctionThreadIntf extends Remote {
    public boolean bid (float value, User bidder) throws RemoteException;
	public void	notifyUserLogin (User user) throws RemoteException, Exception;
    public void	notifyUserLogout (User user) throws RemoteException, Exception;
    public Auction closeAuction () throws RemoteException;
}