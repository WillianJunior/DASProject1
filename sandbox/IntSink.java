import java.rmi.RemoteException;
import java.rmi.Remote;

public interface IntSink extends Remote {
    /**
     * a single method with a single int argument used to time RMI calls
     *
     * @param localParam the single parameter to ignore
     */
    public void ignore(int localParam) throws RemoteException;
}
