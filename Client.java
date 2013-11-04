import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.rmi.RemoteException;

import java.io.Serializable;

public class Client 
	extends UnicastRemoteObject
	implements Serializable, RmiClientCallbackIntf 
			{
	
	private User me;
	private RmiServerIntf server;

	public Client () throws Exception {
		super(0);
		me = null;
		server = (RmiServerIntf)Naming.lookup("//localhost/CentralServer");
	}

	private static void print_options () {
		System.out.println();
		System.out.println("*************************************");
		System.out.println("* 1 - Create Auction Item           *");
		System.out.println("* 2 - Bid Item                      *");
		System.out.println("* 3 - List All Items                *");
		System.out.println("* 4 - List Available Items          *");
		System.out.println("* 5 - List My Biddings              *");
		System.out.println("* 6 - Quit                          *");
		System.out.println("*************************************");

	}

	private boolean login (String name) throws Exception {

		// send name to server

		if ((me = server.login(name, this)) == null) {
			// login failed
			return false;
		}
		return true;

	}

	private void logout () throws Exception {
		server.logout(me);
	}

	public void newItem () throws RemoteException {
		//System.out.println("New item");
		System.out.println("callback test");

	}

	private void bid () {
		System.out.println("Bid");
	}

	private void listAll () {
		System.out.println("listAll");
	}

	public void listAvailable () {
		System.out.println("listAvailable");
	}

	public void listMy () {
		System.out.println("listMy");
	}

	public static void main(String[] args) throws Exception {
		
		Client client = new Client();
		String name;
		TypesNConst.UserOptions option;

		// connect to the server

		// try to log the user in until success or user quits
		do {
			System.out.print("Username: ");
			name = System.console().readLine();

			//try {
				client.login(name);
			//} catch (Exception e) {
			//	System.out.println(e.getMessage());
			//	name = null; // probably there is a better way to signal inside the while that an exception just occured
			//}
			
		} while (name == null);

		while (true) {
			// show the options
			print_options();
			System.out.println("Enter option: ");

			String opt = System.console().readLine();
			if ((option = TypesNConst.UserOptions.fromString(opt)) == null) {
				try {
					option = TypesNConst.UserOptions.fromInt(Integer.parseInt(opt));
				} catch (NumberFormatException e) {option = null;}
			}
			
			if (option != null) {
				switch (option) {
					case CREATE_AUCTION_ITEM:
						client.newItem();
						break;
					case BID_ITEM:
						client.bid();
						break;
					case LIST_ALL_ITEMS:
						client.listAll();
						break;
					case LIST_AVAILABLE_ITEMS:
						client.listAvailable();
						break;
					case LIST_MY_BIDDINGS:
						client.listMy();
						break;
					case QUIT:
						// close the conection with the server and quits
						System.out.println("Bye");
						client.logout();
						UnicastRemoteObject.unexportObject(client, true);
						return;
				}
			} else
				System.out.println("Inexistent option, try again");
		}

	}

}