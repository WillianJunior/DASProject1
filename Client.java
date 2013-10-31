public class Client {
	
	User me;

	public static void main(String[] args) {
		
		Client client = new Client();
		String name;
		TypesNConst.UserOptions option;

		// connect to the server

		// try to log the user in until success or user quits
		do {
			System.out.print("Username: ");
			name = System.console().readLine();
		} while (!name.equals(TypesNConst.EXIT_CODE) && !client.loggin(name));

		for (;;) {
			// show the options
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
						client.loggout();
						return;
				}
			} else
				System.out.println("Inexistent option, try again");
		}

	}

	public boolean loggin (String name) {

		// send name to server

		/*
		if ((me = serverLogin(name)) == null) {
			// login failed
			return false;
		}
		*/
		
		// mockup simulating success
		me = new User(name);

		return true;

	}

	public void loggout () {

	}

	public void newItem () {
		System.out.println("New item");
	}

	public void bid () {
		System.out.println("Bid");
	}

	public void listAll () {
		System.out.println("listAll");
	}

	public void listAvailable () {
		System.out.println("listAvailable");
	}

	public void listMy () {
		System.out.println("listMy");
	}

}