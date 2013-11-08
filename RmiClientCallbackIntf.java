import java.rmi.Remote;
import java.rmi.RemoteException;
 
public interface RmiClientCallbackIntf extends Remote {
    public void auctionBiddingUpdate (Item item) throws RemoteException;
    public void auctionClosed (String message) throws RemoteException;
}