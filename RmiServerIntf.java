import java.rmi.Remote;
import java.rmi.RemoteException;
 
public interface RmiServerIntf extends Remote {
    // log an user in the server by instanciating a new User
	public User login (String username, Client client) throws Exception, RemoteException;
	public void logout (User user) throws Exception, RemoteException;
}