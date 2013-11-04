import java.rmi.Remote;
import java.rmi.RemoteException;
 
public interface RmiAuctionThreadIntf extends Remote {
    public void bid () throws RemoteException;
}