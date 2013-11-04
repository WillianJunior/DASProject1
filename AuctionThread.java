import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import java.io.Serializable;

public class AuctionThread 
		extends UnicastRemoteObject
		implements Serializable, RmiAuctionThreadIntf {

	public AuctionThread() throws RemoteException {
		super(0);
	}

	public void bid () throws RemoteException {

	}

}