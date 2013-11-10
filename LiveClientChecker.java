import java.util.*;
import java.rmi.RemoteException;

public class LiveClientChecker extends TimerTask {
	
	private CentralServer server;
	private volatile List<User> users;

	public LiveClientChecker (List<User> users, CentralServer server) {
		this.server = server;
		this.users = users;
	}

	public void run () {
		System.out.println("[LiveClientChecker] gonna check the users");
		for (User u : users) {
			try {
				if (u.isConnected())
					u.getClient().isAlive();
			} catch (RemoteException e) {
				System.out.println("[LiveClientChecker] " + e.getMessage());
				System.out.println("[LiveClientChecker] user " + u.getName() + " is offline");
				try {
					server.logout(u);
				} catch (Exception ee) {}
			}
		}
	}

}