import java.rmi.Remote;
import java.rmi.RemoteException;
 
public interface RmiAuctionThreadIntf extends Remote {
    public boolean bid (float value, User bidder) throws RemoteException;
}