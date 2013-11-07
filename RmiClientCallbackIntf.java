import java.rmi.Remote;
import java.rmi.RemoteException;
 
public interface RmiClientCallbackIntf extends Remote {
    public void newItem () throws RemoteException;
    public void auctionBiddingUpdate (Item item) throws RemoteException;
}