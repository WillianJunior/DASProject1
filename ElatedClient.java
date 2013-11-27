import java.util.*;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.Naming;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;

import java.io.Serializable;

/*
 * This class is for stress test purposes only
 * usage: java ElatedClient [OPTION] DELAY USERNAME
 * [OPTION] = 
 * 			0 - random operations
 * 			1 - auctions creation only
 * 			2 - bidding only
 * 			3 - listing of all items only
 * 			4 - listing of all available items only
 */

public class ElatedClient extends Client {

	public static final String usage =  "usage: java ElatedClient [OPTION] DELAY USERNAME\n"
										+ "[OPTION] = \n"
										+ " 		0 - random operations\n"
										+ " 		1 - auctions creation only\n"
										+ " 		2 - bidding only\n"
										+ " 		3 - listing of all items only\n"
										+ " 		4 - listing of all available items only\n";

	public ElatedClient () throws Exception {
		super();
	}

	public static void main(String[] args) throws Exception {
		
		if (args.length != 3) {
			System.out.println(usage);
			return;
		}

		Client client = new Client();
		boolean isLoggedIn = false;

		// connect to the server
		System.out.println("let the stress begin!!!");
		int argOption = Integer.parseInt(args[0]);
		int delay = Integer.parseInt(args[1]);
		String name = args[2];
		TypesNConst.UserOptions option;

		option = TypesNConst.UserOptions.fromInt(argOption);
		// try to log the user in until success or user quits
		do {
			if (!client.login(name))
				System.out.println("Login failed. There already is someone logged in with this username.");
			else
				isLoggedIn = true;
			
			if (name == null || !isLoggedIn)
				continue;

			try {
				while (true) {
					if (argOption == 0)
						option = TypesNConst.UserOptions.fromInt((int) (1 + Math.random() * (3)));
					Thread.sleep(delay);
					switch (option) {
						case CREATE_AUCTION_ITEM:
							String itemName = "test";
							float minimumValue = 100;
							Calendar closingDatetime = GregorianCalendar.getInstance();
							closingDatetime.add(Calendar.MINUTE, 10);
							Calendar removalDatetime = GregorianCalendar.getInstance();
							removalDatetime.add(Calendar.MINUTE, 20);
							long startTime = System.currentTimeMillis();
							client.newItem(itemName, minimumValue, closingDatetime, removalDatetime);
							long stopTime = System.currentTimeMillis();
							System.out.println((stopTime - startTime));
							break;
						case BID_ITEM:
							int auctionId = (int) (Math.random() * 2000);
							float value = (float) (Math.random() * 30000);
							startTime = System.currentTimeMillis();
							client.bid(auctionId, value);
							stopTime = System.currentTimeMillis();
							System.out.println((stopTime - startTime));
							break;
						case LIST_ALL_ITEMS:
							startTime = System.currentTimeMillis();
							client.listAllNoPrint();
							stopTime = System.currentTimeMillis();
							System.out.println((stopTime - startTime));
							break;
						case LIST_AVAILABLE_ITEMS:
							startTime = System.currentTimeMillis();
							client.listAvailableNoPrint();
							stopTime = System.currentTimeMillis();
							System.out.println((stopTime - startTime));
							break;
						case QUIT:
							// close the conection with the server and quits
							System.out.println("Bye");
							client.logout();
							UnicastRemoteObject.unexportObject(client, true);
							return;
					}
				} 
			} catch (ConnectException ce) {
				System.out.println("The server is inaccessible. Trying to reconnect");
				client.connectToServer();
			}

		} while (true);

	}

}