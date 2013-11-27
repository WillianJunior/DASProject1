import java.util.Calendar;

public class TypesNConst {

	// Numeric Constants
	public static final int MAX_ITEM_REMOVAL_TIME = 10; // in minutes
	public static final long LIVE_CLIENT_CHECKER_PERIOD = 60000; // in milliseconds
	public static final long RECONECTION_WAITING_TIME = 5000; // in milliseconds

	// String Constants
	public static final String serverIp = "localhost";//"172.30.49.227";

	// Enums
	public static enum UserType {
		OWNER, 
		BIDDER
	}

	public static enum BiddingReturns {
		VALUE_LOWER,
		IS_OWNER,
		SUCCESS
	}

	public static enum UserOptions {
		
		CREATE_AUCTION_ITEM,
		BID_ITEM,
		LIST_ALL_ITEMS,
		LIST_AVAILABLE_ITEMS,
		QUIT;

		static UserOptions fromInt (int option) {
			switch (option) {
				case 1:
					return CREATE_AUCTION_ITEM;
				case 2:
					return BID_ITEM;
				case 3:
					return LIST_ALL_ITEMS;
				case 4:
					return LIST_AVAILABLE_ITEMS;
				case 5:
					return QUIT;
				default:
					return null;
			}
		}

		static UserOptions fromString (String option) {
			try {
				return UserOptions.valueOf(option.toUpperCase());
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
	}

}