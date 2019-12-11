import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.text.ParseException;


/**
 * Class that tracks, updates, edits data involved in the ValleyBike Share system
 * This is our model in the MVC
 * We make the assumption in this bike that our system only has
 * pedelecs. Every time we use the word 'bike' in this code, we mean
 * pedelecs/electric bikes.
 */
public class ValleyBikeSim {
	/**
	 * data structure for keeping track of stations
	 */
	private static Map<Integer, Station> stationsMap = new HashMap<>();

	/**
	 * data structure for keeping track of bikes
	 */
	private static Map<Integer, Bike> bikesMap = new HashMap<>();

	/**
	 * list for storing bike ids of bikes that require maintenance
	 */
	private static Map<Integer, String> mntReqs = new HashMap<>();

	/**
	 * data structure for keeping track of customer accounts
	 */
	private static Map<String, CustomerAccount> customerAccountMap = new HashMap<>();

	/**
	 * data structure for keeping track of internal accounts
	 */
	private static Map<String, InternalAccount> internalAccountMap = new HashMap<>();

	/**
	 * data structure for keeping track of rides
	 */
	private static Map<UUID, Ride> rideMap = new HashMap<>();

	/**
	 * this data formatter is used for local date parsing
	 */
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * Reads in the stations csv file data and parses it into station objects
	 * mapped by id for easy access and manipulation throughout program
	 *
	 * Then outputs welcome message and menu selector
	 * @throws IOException failure during reading, writing and searching file or directory operations
	 * @throws ParseException fail to parse a String that is ought to have a special format
	 * @throws InterruptedException when a thread that is sleeping, waiting, or is occupied is interrupted
	 * @throws SQLException for database access error
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 * @throws NoSuchAlgorithmException when a particular cryptographic algorithm is requested but is not available in the environment.
	 */
	public static void main(String[] args) throws IOException, ParseException, InterruptedException, SQLException, ClassNotFoundException, NoSuchAlgorithmException {
		// connect to sqlite database and read all required tables
		Connection conn = connectToDatabase();
		//read in data from database to data structures
		if (conn != null) {
			Statement stmt = conn.createStatement();
			readCustomerAccountData(stmt);
			readInternalAccountData(stmt);
			readStationData(stmt);
			readBikeData(stmt);
			readRideData(stmt);
			conn.close();
		} else {
			System.out.println("Sorry, something went wrong connecting to the ValleyBike Database.");
		}

		// start the initial menu
		System.out.print("Welcome to ValleyBike Share! ");
		ValleyBikeController.initialMenu();
	}

	/**
	 * Method to create connection to SQL database
	 *
	 * @return connection to SQL database
	 * @throws SQLException for database access error
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	private static Connection connectToDatabase() throws SQLException, ClassNotFoundException {
		//we are using a sqlite database
		//name is database is ValleyBike.db
		Class.forName("org.sqlite.JDBC");
		String dbURL = "jdbc:sqlite:ValleyBike.db";
		return DriverManager.getConnection(dbURL);
	}

	/**
	 * Reads in info from database and converts to customer objects that get stored in data structure
	 *
	 * @param stmt statement necessary to run sql query
	 * @throws SQLException for database access error
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	private static void readCustomerAccountData(Statement stmt) throws SQLException, ClassNotFoundException {
		ResultSet rs = stmt.executeQuery("SELECT * FROM Customer_Account");
		//get each value from each line in database
		while (rs.next()) {
			String username = rs.getString("username");
			String password = rs.getString("password");
			String emailAddress = rs.getString("email_address");
			String creditCard = rs.getString("credit_card");
			Membership membership = readMembershipData(username);
			int lastRideIsReturned = rs.getInt("last_ride_is_returned");
			int enabled = rs.getInt("enabled");
			double balance = rs.getDouble("balance");
			String rideIdString = rs.getString("ride_id_string");
			ArrayList<UUID> rideIdList = new ArrayList<>();

			if (rideIdString != null && rideIdString.length() > 0){
				for (String ride : rideIdString.split(",")) {
					UUID uuid = UUID.fromString(ride.replaceAll(" ", ""));
					rideIdList.add(uuid);
				}
			}
			//create customer account from info
			CustomerAccount customerAccount = new CustomerAccount(username, password, emailAddress, creditCard, membership, balance, lastRideIsReturned == 1, enabled == 1, rideIdList);

			// add to the customer account map
			customerAccountMap.put(username, customerAccount);
		}
	}

	/**
	 * reads in membership data from database
	 * @param username username who has membership
	 * @return the membership object associated with the given user
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 * @throws SQLException for database access error
	 */
	private static Membership readMembershipData(String username) throws ClassNotFoundException, SQLException{
		String sql = "SELECT * FROM Membership WHERE username = ?";

		//initialize all variables associated with membership obj
		int totalRidesLeft = 0;
		int type = 0;
		LocalDate lastPayment = null;
		LocalDate memberSince = null;
		//update sql database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)){
			// set the corresponding param
			pstmt.setString(1, username);
			ResultSet rows = pstmt.executeQuery();
			while(rows.next())
			{
				totalRidesLeft = rows.getInt("total_rides_left");
				type = rows.getInt("type");
				lastPayment = LocalDate.parse(rows.getString("last_payment"), formatter);
				memberSince = LocalDate.parse(rows.getString("membership_since"), formatter);
			}
		}
		//checkMembershipType creates and returns membership obj of specific type i.e. monthly, yearly, PAYG
		return checkMembershipType(type, totalRidesLeft, lastPayment, memberSince);
	}

	/**
	 * Reads in info from database and converts to internal account objects that get stored in data structure
	 *
	 * @param stmt allows execution of SQL queries
	 * @throws SQLException for database access error
	 */
	private static void readInternalAccountData(Statement stmt) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT * FROM Internal_Account");
		//get each value from line in table
		while (rs.next()) {
			String username = rs.getString("username");
			String password = rs.getString("password");
			String emailAddress = rs.getString("email_address");
			//create internal account instance
			InternalAccount internalAccount = new InternalAccount(username, password, emailAddress);

			// add to the internal account map
			internalAccountMap.put(username, internalAccount);
		}
	}

	/**
	 * Reads in info from database and converts to station objects that get stored in data structure
	 *
	 * @param stmt allows execution of SQL queries
	 * @throws SQLException for database access error
	 */
	private static void readStationData(Statement stmt) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT * FROM Station");
		//get each value from line in table
		while (rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			int reqMnt = rs.getInt("req_mnt");
			int capacity = rs.getInt("capacity");
			int kiosk = rs.getInt("kiosk");
			String address = rs.getString("address");
			String bikeString = rs.getString("bike_string");
			LinkedList<Integer> bikeList = new LinkedList<>();

			//if no bike in station, bike string might be null or empty string
			if (bikeString != null) {
				// loop through bike ids in string
				for (String bikeId : bikeString.replaceAll(" ", "").split(",")) {
					// if there are bike ids in the string
					if (bikeString.length() > 0) {
						//if bike string does have bike, then add to bike list
						bikeList.add(Integer.parseInt(bikeId));
					}
				}
			}

			//create new station instance
			Station station = new Station(name, reqMnt, capacity, intToBoolean(kiosk), address, bikeList);

			// add to the station tree
			stationsMap.put(id, station);
		}
	}

	/**
	 * Reads in info from database and converts to bike objects that get stored in data structure
	 *
	 * @param stmt allows execution of SQL queries
	 * @throws SQLException for database access error
	 */
	private static void readBikeData(Statement stmt) throws SQLException{
		ResultSet rs = stmt.executeQuery("SELECT * FROM Bike");

		//get values from each line in table
		while (rs.next()) {
			int id = rs.getInt("id");
			int location = rs.getInt("location");
			int stationId = rs.getInt("station_id");
			int reqMnt = rs.getInt("req_mnt");

			//default for maintenance
			String maintenance = "n";

			//if maintenance is needed
			if (reqMnt == 1) {
				maintenance = "y";

				addToMntRqs(id, rs.getString("mnt_report"));
			}

			String mntReport = rs.getString("mnt_report");

			//create instance of bike object
			Bike bike = new Bike(id, location, stationId, maintenance, mntReport);

			// add to the bike tree
			bikesMap.put(id, bike);
		}
	}


	/**
	 * Reads in info from database and converts to ride objects that get stored in data structure
	 *
	 * @param stmt allows execution of SQL queries
	 * @throws SQLException for database access error
	 * @throws ParseException fail to parse a String that is ought to have a special format
	 */
	private static void readRideData(Statement stmt) throws SQLException, ParseException {
		ResultSet rs = stmt.executeQuery("SELECT * FROM Ride");
		//get each value from each line in table
		while (rs.next()) {
			String id = rs.getString("ride_id");
			int bike_id = rs.getInt("bike_id");
			String username = rs.getString("username");
			int is_returned = rs.getInt("is_returned");
			long rideLength = rs.getLong("ride_length");
			String start_time_stamp = rs.getString("start_time_stamp");
			String end_time_stamp = rs.getString("end_time_stamp");
			double payment = rs.getDouble("payment");
			int station_from = rs.getInt("station_from");
			int station_to = rs.getInt("station_to");

			// change string to unique UUID
			UUID uuid_id = UUID.fromString(id);

			// change binary to boolean
			boolean is_returned_bool = intToBoolean(is_returned);

			//parse time stamps as readable instants
			Instant start_time_stamp_instant = Instant.parse(start_time_stamp);
			Instant end_time_stamp_instant = Instant.parse(end_time_stamp);

			// create new ride object with fields
			Ride ride = new Ride(uuid_id, bike_id, username,
					is_returned_bool, start_time_stamp_instant,
					end_time_stamp_instant, station_from, station_to);

			// set the ride length and payment as well
			ride.setRideLength(rideLength);
			ride.setPayment(payment);

			// add to the bike tree
			rideMap.put(uuid_id, ride);
		}
	}

	/**
	 * Updates the number of maintenance requests at a station
	 *
	 * @param stationId the station id that will get updated
	 * @param mntRqsts  the number of maintenance requests the station should have
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateStationMntRqsts(int stationId, int mntRqsts) throws ClassNotFoundException {
		String sql = "UPDATE Station SET req_mnt = ? "
		+ "WHERE id = ?";

		//update sql database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			// set the corresponding param
			pstmt.setInt(1, mntRqsts);
			pstmt.setInt(2, stationId);
			// update
			pstmt.executeUpdate();

			//update station data in map
			stationsMap.get(stationId).setMaintenanceRequest(mntRqsts);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update station maintenance requests in the database at this time.");
			return false;
		}

	}

	//TODO

	/**
	 * updates membership rides left
	 * @param username username of account membership to update
	 * @param ridesLeft number of rides left to set membership to
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	static void updateMembershipRidesLeft(String username, int ridesLeft) throws SQLException, ClassNotFoundException {
		String sql = "UPDATE Membership SET total_rides_left = ? WHERE username = ?";
		System.out.println("updating ridesleft to" + ridesLeft);
		try(Connection conn = connectToDatabase();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, ridesLeft);
			pstmt.setString(2, username);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Sorry, could not update rides left in database at this time.");
		}
	}

	/**
	 * Adds a bike to a station
	 *
	 * @param stationId the station id that will get updated
	 * @param bikeId    the bike to be added to the station
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean addBikeToStation(int stationId, int bikeId) throws ClassNotFoundException {
		// set sql query
		String sql = "UPDATE Station SET bike_string = ? "
				+ "WHERE id = ?";

		// get station bike list string
		String bikeIdsString = stationsMap.get(stationId).getBikeListToString();

		// try connection
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			// set the corresponding param
			pstmt.setString(1, bikeIdsString);
			pstmt.setInt(2, bikeId);
			// update
			pstmt.executeUpdate();

			//add bike to station in database
			stationsMap.get(stationId).addToBikeList(bikeId);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not increment number of bikes in station in database at this time.");

			return false;
		}
	}

	/**
	 * updae number of bikes at station
	 * @param stationId station to update
	 * @param bikes number of bikes at station
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateStationBikesNum(int stationId, int bikes) throws ClassNotFoundException{
		String sql = "UPDATE Station SET bikes = ? "
				+ "WHERE id = ?";

		//update station in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setInt(1, bikes);
			pstmt.setInt(2, stationId);

			// update
			pstmt.executeUpdate();

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update station's bikes nums in database at this time.");

			return false;
		}
	}

	/**
	 * update actual bike object at station
	 * @param stationId station to update
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateStationBikeList(int stationId) throws ClassNotFoundException{
		String sql = "UPDATE Station SET bike_string = ? "
				+ "WHERE id = ?";

		// get bike list to string
		String bikeIdsString = stationsMap.get(stationId).getBikeListToString();

		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, bikeIdsString);
			pstmt.setInt(2, stationId);

			// update
			pstmt.executeUpdate();

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update station's bike list in database");
			return false;
		}
	}


	/**
	 * Update the station id that the bike is registered at
	 *
	 * @param bikeId       the bike to update
	 * @param newStationId the station id to assign to the bike
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateBikeStationId(int bikeId, int newStationId) throws ClassNotFoundException {
		String sql = "UPDATE Bike SET station_id = ? "
				+ "WHERE id = ?";

		//set new station id in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			// set the corresponding param
			pstmt.setInt(1, newStationId);
			pstmt.setInt(2, bikeId);
			// update
			pstmt.executeUpdate();

			// update in map as well
			bikesMap.get(bikeId).setStation(newStationId);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update bike's station id in database at this time.");
			return true;
		}
	}

	/**
	 * Update whether bike is available at station, currently rented, or not available
	 *
	 * @param bikeId          the bike to receive a new location
	 * @param newBikeLocation integer representing bike location
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateBikeLocation(int bikeId, int newBikeLocation) throws ClassNotFoundException {
		String sql = "UPDATE Bike SET location = ? "
				+ "WHERE id = ?";

		//update location in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setInt(1, newBikeLocation);
			pstmt.setInt(2, bikeId);

			// update
			pstmt.executeUpdate();

			// update in map
			bikesMap.get(bikeId).setBikeLocation(newBikeLocation);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update bike location in database at this time.");
			return false;
		}
	}

	/**
	 * Update whether a bike has a maintenance request along with its
	 * maintenance report if it does
	 *
	 * @param bikeId  bike id to  update
	 * @param req_mnt boolean whether bike has a maintenance request
	 * @param new_mnt_report the maintenance report if bike requires maintenance
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateBikeRqMnt(int bikeId, boolean req_mnt, String new_mnt_report) throws ClassNotFoundException {
		String sql = "UPDATE Bike SET req_mnt = ?, mnt_report = ?"
				+ "WHERE id = ?";

		//update maintenance request in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// change boolean to binary
			int req_mnt_int = 0;
			if (req_mnt) req_mnt_int = 1;

			// set the corresponding param
			pstmt.setInt(1, req_mnt_int);
			pstmt.setString(2, new_mnt_report);
			pstmt.setInt(3, bikeId);

			// update
			pstmt.executeUpdate();

			//update maintenance request in bike map
			bikesMap.get(bikeId).setMnt(req_mnt);
			bikesMap.get(bikeId).setMntReport(new_mnt_report);

			// if bike requires maintenance
			if(req_mnt){
				// add bike id and report to map of maintenance requests
				mntReqs.put(bikeId, new_mnt_report);
			}

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update bike maintenance in database at this time.");
			return false;
		}
	}

	/**
	 * Update whether a ride has been returned to a station
	 *
	 * @param rideId     the ride object being updated
	 * @param isReturned boolean representing whether the ride has been returned
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateRideIsReturned(UUID rideId, Boolean isReturned) throws ClassNotFoundException {
		String sql = "UPDATE Ride SET is_returned = ? "
				+ "WHERE ride_id = ?";

		//update ride in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, rideId.toString());

			if (isReturned) pstmt.setInt(2, 1);
			else pstmt.setInt(2, 0);

			// update
			pstmt.executeUpdate();

			//update ride in ride map
			rideMap.get(rideId).setIsReturned(isReturned);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
			return false;
		}
	}

	/**
	 * update the time the ride was returned
	 *
	 * @param rideId         the ride object being updated
	 * @param end_time_stamp the ride end time
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateRideEndTimeStamp(UUID rideId, Instant end_time_stamp) throws ClassNotFoundException {
		String sql = "UPDATE Ride SET end_time_stamp = ? "
				+ "WHERE ride_id = ?";

		//update ride end time in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, rideId.toString());

			pstmt.setString(2, end_time_stamp.toString());

			// update
			pstmt.executeUpdate();

			//update ride end timestamp in ride map
			rideMap.get(rideId).setEndTimeStamp(end_time_stamp);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
			return false;
		}
	}

	/**
	 * Updates cost of ride
	 *
	 * @param rideId  ride being updated
	 * @param payment cost of ride
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateRidePayment(UUID rideId, double payment) throws ClassNotFoundException {
		String sql = "UPDATE Ride SET payment = ? "
				+ "WHERE ride_id = ?";

		//update ride payment in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setDouble(1, payment);

			// set the corresponding param
			pstmt.setString(2, rideId.toString());

			// update
			pstmt.executeUpdate();

			//update payment for ride in ride map
			rideMap.get(rideId).setPayment(payment);

			return true;

		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
			return false;
		}
	}

	/**
	 * Updates cost of ride
	 *
	 * @param rideId ride being updated
	 * @param station_to is the station id for the station the ride is being returned to
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateRideStationTo(UUID rideId, int station_to) throws ClassNotFoundException {
		String sql = "UPDATE Ride SET station_to = ? "
				+ "WHERE ride_id = ?";

		//update ride payment in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, station_to);
			pstmt.setString(2, rideId.toString());

			// update
			pstmt.executeUpdate();

			//update payment for ride in ride map
			rideMap.get(rideId).setStationTo(station_to);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
			return false;
		}
	}

	/**
	 * Updates cost of ride
	 *
	 * @param rideId ride being updated
	 * @param station_to is the station id for the station the ride is being returned to
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateRideLength(UUID rideId, long ride_length) throws ClassNotFoundException {
		String sql = "UPDATE Ride SET ride_length = ? "
				+ "WHERE ride_id = ?";

		//update ride payment in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, ride_length);
			pstmt.setString(2, rideId.toString());

			// update
			pstmt.executeUpdate();

			//update payment for ride in ride map
			rideMap.get(rideId).setRideLength(ride_length);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
			return false;
		}
	}


	/**
	 * update customer's email address
	 *
	 * @param username        username of account to be updated
	 * @param newEmailAddress new email address
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateCustomerEmailAddress(String username, String newEmailAddress) throws ClassNotFoundException {
		String sql = "UPDATE Customer_Account SET email_address = ? "
				+ "WHERE username = ?";

		//update customer account in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, newEmailAddress);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			//update customer email address in customer map
			customerAccountMap.get(username).setEmailAddress(newEmailAddress);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
			return false;
		}
	}

	/**
	 * if customer deletes their account, disable account so cannot be logged into but preserve data
	 *
	 * @param username username of account to disable
	 * @throws NoSuchAlgorithmException when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static void disableCustomerAccount(String username) throws NoSuchAlgorithmException, ClassNotFoundException {
		CustomerAccount customerAccount = customerAccountMap.get(username);
		MessageDigest md = MessageDigest.getInstance("MD5");
		//update password to random hashcode
		StringBuilder sb = new StringBuilder();
		byte[] hashInBytesPassword = md.digest(customerAccount.getPassword().getBytes(StandardCharsets.UTF_8));
		for (byte b : hashInBytesPassword) {
			sb.append(String.format("%02x", b));
		}

		if(!updateCustomerPassword(username, sb.toString())){
			return;
		}

		//update email address to random hashcode
		sb.setLength(0);
		byte[] hashInBytesEmailAddress = md.digest(customerAccount.getEmailAddress().getBytes(StandardCharsets.UTF_8));
		for (byte b : hashInBytesEmailAddress) {
			sb.append(String.format("%02x", b));
		}

		if(!updateCustomerEmailAddress(username, sb.toString())){
			return;
		}

		//update username to random hashcode
		sb.setLength(0);
		byte[] hashInBytesUsername = md.digest(customerAccount.getUsername().getBytes(StandardCharsets.UTF_8));
		for (byte b : hashInBytesUsername) {
			sb.append(String.format("%02x", b));
		}

		if(!updateCustomerUsername(username, sb.toString())){
			return;
		}

		//set customer account field to disabled
		if(!updateCustomerDisabled(sb.toString())){
			return;
		}
	}

	/**
	 * Set account to disabled to customer cannot log in
	 *
	 * @param username username of account to be disabled
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateCustomerDisabled(String username) throws ClassNotFoundException {
		String sql = "UPDATE Customer_Account SET enabled = ? "
				+ "WHERE username = ?";

		//update account in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setInt(1, 0);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			//update account in customer map
			customerAccountMap.get(username).setEnabled(false);
			System.out.println("Your account has been successfully deleted.");

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not delete account at this time.");
			return false;
		}
	}

	/**
	 * update internal employee's email address
	 *
	 * @param username        username of account to be updated
	 * @param newEmailAddress new email address for account
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateInternalEmailAddress(String username, String newEmailAddress) throws ClassNotFoundException {
		String sql = "UPDATE Internal_Account SET email_address = ? "
				+ "WHERE username = ?";

		//update customer email address in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, newEmailAddress);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			//update email address in internal account map
			internalAccountMap.get(username).setEmailAddress(newEmailAddress);
			System.out.println("Your email address has been successfully updated to " + newEmailAddress);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
			return false;
		}
	}

	/**
	 * update username for customer account
	 *
	 * @param username    username of account to be updated
	 * @param newUsername new username for account
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateCustomerUsername(String username, String newUsername) throws ClassNotFoundException {
		String sql = "UPDATE Customer_Account SET username = ? "
				+ "WHERE username = ?";

		//update customer username in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, newUsername);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			//update customer username in customer account map
			CustomerAccount customerAccount = customerAccountMap.get(username);
			customerAccount.setUsername(newUsername);
			customerAccountMap.remove(username);
			customerAccountMap.put(newUsername, customerAccount);
			//System.out.println("Your username has been successfully updated to " + newUsername);
			updateRideUsername(username, newUsername);
			updateMembershipUsername(username, newUsername);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update username in database at this time.");
			return false;
		}
	}

	/**
	 * update username in ride table in database and in ride map
	 * @param username is the old username for the customer
	 * @param newUsername is the new username for the customer
	 * @return false if database updating failed
	 */
	static Boolean updateRideUsername(String username, String newUsername){
		String sql = "UPDATE Ride SET username = ? "
				+ "WHERE username = ?";

		//update customer username in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, newUsername);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			for (Map.Entry<UUID, Ride> ride: rideMap.entrySet()){
				if (ride.getValue().getUsername().equals(username)){
					ride.getValue().setUsername(newUsername);
				}
			}

			return true;
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Sorry, could not update username in database at this time.");
			return false;
		}
	}

	/**
	 * updates username in the membership table in the database
	 * @param username original username
	 * @param newUsername new username
	 * @return false if database updating failed
	 */
	private static Boolean updateMembershipUsername(String username, String newUsername){

		String sql = "UPDATE Membership SET username = ? "
				+ "WHERE username = ?";

		//update customer username in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, newUsername);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			return true;
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Sorry, could not update username in database at this time.");
			return false;
		}
	}

	/**
	 * update customer's list of rides they've taken
	 *
	 * @param username username of account to be updated
	 * @param rideId   new ride to add to list
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateRideIdList(String username, UUID rideId) throws ClassNotFoundException {
		String sql = "UPDATE Customer_Account SET ride_id_string = ? "
				+ "WHERE username = ?";

		//add new ride to customer's ride list
		customerAccountMap.get(username).addNewRide(rideId);

		String rideIdString = customerAccountMap.get(username).getRideIdListToString();

		//update customer's ride list in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, rideIdString);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			//add new ride to customer's ride list
			customerAccountMap.get(username).addNewRide(rideId);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not add ride id to list in database at this time.");
			return false;
		}
	}

	/**
	 * update field in customer account that states whether they currently have a rental
	 *
	 * @param username           username of account to update
	 * @param lastRideisReturned boolean representing whether last bike was returned
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateCustomerLastRideisReturned(String username, boolean lastRideisReturned) throws ClassNotFoundException {
		String sql = "UPDATE Customer_Account SET last_ride_is_returned = ? "
				+ "WHERE username = ?";

		// change boolean to binary, where 0 is false and 1 is true
		int lastRideReturnedInt = 0;
		if (lastRideisReturned) {
			lastRideReturnedInt = 1;
		}

		//update field in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setInt(1, lastRideReturnedInt);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			//update field in customer account map
			customerAccountMap.get(username).setLastRideIsReturned(lastRideisReturned);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not add ride id to list in database at this time.");
			return false;
		}
	}

	/**
	 * update customer account password
	 *
	 * @param username    username of account to update
	 * @param newPassword new password for account
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateCustomerPassword(String username, String newPassword) throws ClassNotFoundException {
		String sql = "UPDATE Customer_Account SET password = ? "
				+ "WHERE username = ?";

		//update customer password in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, newPassword);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			//update customer password in customer map
			customerAccountMap.get(username).setPassword(newPassword);

			return true;

		} catch (SQLException e) {
			System.out.println("Sorry, could not update password in database at this time.");
			return false;
		}
	}

	/**
	 * update credit card for customer account
	 *
	 * @param username      username of account to update
	 * @param newCreditCard new credit card
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateCustomerCreditCard(String username, String newCreditCard) throws ClassNotFoundException {
		String sql = "UPDATE Customer_Account SET credit_card = ? "
				+ "WHERE username = ?";

		//update credit card for customer in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, newCreditCard);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			//update customer credit card in customer account map
			customerAccountMap.get(username).setCreditCard(newCreditCard);
			System.out.println("Your credit card information has been successfully updated to " + newCreditCard);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update credit card information in database at this time.");
			return false;
		}
	}

	/**
	 * update membership type for customer
	 *
	 * @param username      username of account to update
	 * @param newMembership new membership type for account
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	//TODO neamat fix
	static Boolean updateCustomerMembership(String username, int newMembership) throws ClassNotFoundException {
		if (customerAccountMap.get(username).getMembership().getMembershipInt() == newMembership){
			System.out.println("Your current membership is the same type as the one you are trying to update to. Membership change failed.");
			return false;
		} else {
			Membership membershipType = checkMembershipType(newMembership);
			String sql = "UPDATE Membership SET type = ? , total_rides_left = ? , last_payment = ? , membership_since = ?"
					+ "WHERE username = ?";

			//update membership type in database
			try (Connection conn = connectToDatabase();
				 PreparedStatement pstmt = conn.prepareStatement(sql)) {

				// set the corresponding param
				pstmt.setInt(1, newMembership);
				pstmt.setInt(2, membershipType.getTotalRidesLeft());
				pstmt.setString(3, membershipType.getLastPayment().toString());
				pstmt.setString(4, membershipType.getMemberSince().toString());
				pstmt.setString(5, username);
				// update
				pstmt.executeUpdate();

				//update membership type associated with user and date representing start of membership
				customerAccountMap.get(username).setMembership(checkMembershipType(newMembership));
				System.out.println("Your membership has been successfully updated to " + Objects.requireNonNull(checkMembershipType(newMembership)).getMembershipString());

				//inform user of the charge for their new membership
				if (newMembership == 2) {
					System.out.println("You have been charged $20 for your monthly membership. Your membership will auto-renew each month, \n" +
							" and you will get an email notification when your card is charged. \n" +
							" If your credit card ever expires or becomes invalid, you will be switched to a Pay-As-You-Go member " +
							"and notified via email. ");
				} else if (newMembership == 3) {
					System.out.println("You have been charged $90 for your monthly membership. Your membership will auto-renew each month,\n" +
							" and you will get an email notification when your card is charged. \n" +
							"If your credit card ever expires or becomes invalid, you will be switched to a Pay-As-You-Go member " +
							"and notified via email. ");
				}

				return true;
			} catch (SQLException e) {
				System.out.println("Sorry, could not update membership in database at this time.");
				return false;
			}
		}
	}


	/**
	 * updates username of internal account
	 *
	 * @param username    username of account to update
	 * @param newUsername new internal username
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateInternalUsername(String username, String newUsername) throws ClassNotFoundException {
		String sql = "UPDATE Internal_Account SET username = ? "
				+ "WHERE username = ?";

		//update username in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, newUsername);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			//update internal account username in internal account map
			InternalAccount internalAccount = internalAccountMap.get(username);
			internalAccount.setUsername(newUsername);

			// remove the old username and add new username
			internalAccountMap.remove(username);
			internalAccountMap.put(newUsername, internalAccount);
			System.out.println("Your username has been successfully updated to " + newUsername);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update username in database at this time.");
			return false;
		}

	}

	/**
	 * update password for intenal account
	 *
	 * @param username    username for internal account to update
	 * @param newPassword new password for account
	 * @return false if database updating failed
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean updateInternalPassword(String username, String newPassword) throws ClassNotFoundException {
		String sql = "UPDATE Internal_Account SET password = ? "
				+ "WHERE username = ?";

		//update internal password in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, newPassword);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();

			//update password in internal account map
			internalAccountMap.get(username).setPassword(newPassword);
			System.out.println("Your password has been successfully updated to " + newPassword);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, could not update password in database at this time.");
			return false;
		}
	}

	/**
	 * get average length of ride taken by a user
	 *
	 * @param username username of account whose rides to view and average
	 * @return the average ride length for the customer
	 */
	static int viewAverageRideTime(String username) {
		ArrayList<UUID> rideIdList = customerAccountMap.get(username).getRideIdList();
		int totalRideTime = 0;
		//get total ride time
		for (UUID ride : rideIdList) {
			totalRideTime += rideMap.get(ride).getRideLength();
		}
		//divide total ride time by number of rides to get average ride time
		if (rideIdList.size() == 0) {
			return 0;
		} else {
			return totalRideTime / rideIdList.size();
		}
	}

	/**
	 * print formatted list of all customers
	 */
	static void viewAllCustomers() {
		//if there exist customers, print
		if (customerAccountMap.size() > 0) {
			// format table view
			System.out.format("%-20s\n", "List of all customer account username:");

			// while the iterator has a next value
			for (String username : customerAccountMap.keySet()) {
				System.out.format("%-20s\n", username);
			}
		} else { //if no customers, say so
			System.out.println("There are no customers using our services yet.");
		}
	}

    /**
     * helper method to check if username exists in either
	 * the customer account map, the internal account map, or both
	 *
	 * @param username - the username input by user
	 * @param num - specifies which map we will look for the username in
	 *            1 - customer account map
	 *            2 - internal account map
	 *            3 - both customer and internal maps
	 *
	 * @return true if username is in the map(s) specified and false otherwise
	 *
     */
    static boolean accountMapsContain(String username, int num) {
    	switch(num) {
			case 1: //search customer map
				return customerAccountMap.containsKey(username);
			case 2: //search internal map
				return internalAccountMap.containsKey(username);
			case 3: //search both maps
				return customerAccountMap.containsKey(username)||internalAccountMap.containsKey(username);
		}
        return false;
    }

    /**
	 * Method to check whether customer already has a bike rented and whether the rental has
	 * gone on for too long (in which case they are charged)
	 * Credit card was validated when bike was rented so does not need to be validated again to charge them
	 * @param username is the unique username associated with the customer account
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static void checkBikeRented(String username) throws ClassNotFoundException {
		// get customer object
		CustomerAccount customer = ValleyBikeSim.getCustomerObj(username);

		// true if last ride was returned
		Boolean isReturned = customer.getIsReturned();

		if (!isReturned) {
			UUID ride = customer.getLastRideId();
			if (rideMap.get(ride).isRented24Hours()) {
				// if rental exceeds 24 hours, charge account 150 and notify user
				//credit card was pre-validated when bike was rented to ensure charge would be valid
				System.out.println("Your bike rental has exceeded 24 hours. You have been charged a late fee of " +
						"$150 to your credit card.");

				//ASSUMPTION: if bike has been rented over 24 hours, it is probably lost or stolen forever
				//so proceed like ride has been returned, to "station 0" (the checked-out station)
				//this allows user to rent bikes again, and prevents them from being fined again for same bike

				// if statements check for if the updating failed and thus exits
				// before any changes can be made

				if(!updateRideIsReturned(ride, true)){
					return;
				}

				if(!updateRideEndTimeStamp(ride, Instant.now())){
					return;
				}

				if(!updateRideStationTo(ride, 0)){
					return;
				}

				if(!updateCustomerLastRideisReturned(username, true)) {
						return;
				}

			} else {
				//if rental is under 24 hours, just remind them they have a rental
				System.out.println("Reminder that you currently have a bike rented. " +
						"It must be returned within 24 hours of check-out.");
			}
		}
	}

	/**
	 * Whenever the program is running and no one is logged in, check to see whether time to renew memberships
	 * If it is time, renew memberships (charge card, refill rides, reset last paid date)
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static void checkMembershipRenewalTime() throws ClassNotFoundException {
		//check each user's membership to find whether their payment is due
		for (String username : customerAccountMap.keySet()) {
			// initiate key for iterator
			CustomerAccount user = customerAccountMap.get(username);
			if (user.getMembership().checkPaymentDue()) {
				if (ValleyBikeController.isValidCreditCard(user.getCreditCard())) {
					if (user.getMembership().getMembershipInt() == 2) {
						user.getMembership().setTotalRidesLeft(20);
					} else if (user.getMembership().getMembershipInt() == 3) {
						user.getMembership().setTotalRidesLeft(260);
					}

					user.getMembership().setLastPayment(LocalDate.now());

					//ASSUMPTION: In a real system, here emails would be sent out to all members whose memberships
					// have just been renewed, letting them know their card was charged
				} else {
					//if credit card cannot be charged, reset membership to pay-as-you-go
					if(!updateCustomerMembership(username, 1)){
						return;
					}

					//ASSUMPTION: In a real system, here we would send out emails notifying users that
					//they had been switched to a PAYG member because their credit card was not valid
				}
			}
		}
	}

	/**
	 *
	 * @return an int which is the total number of customer accounts in database
	 * @throws SQLException for database access error
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static int viewTotalUsers() throws SQLException, ClassNotFoundException {
		Connection conn = connectToDatabase();
		int count = 0;
		//read in data from database to data structures
		if (conn != null) {
			Statement stmt = conn.createStatement();
			//add the number of customer_accounts together
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Customer_Account");
			while (rs.next()) {
				count = rs.getInt("COUNT(*)");
			}
			conn.close();
		}
		//return count of all customer accounts in database
		return count;
	}

	/**
	 * @return an int which is the sum of all the maintenance requests totaled from all stations in the database
	 * @throws SQLException for database access error
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static int viewTotalMaintenanceRequests() throws SQLException, ClassNotFoundException {
		Connection conn = connectToDatabase();
		int total = 0;
		//read in data from database to data structures
		if (conn != null) {
			Statement stmt = conn.createStatement();
			//total all the maintenance requests together (which is also an int)
			ResultSet rs = stmt.executeQuery("SELECT SUM(req_mnt) FROM Station");
			while (rs.next()) {
				total = rs.getInt("SUM(req_mnt)");
			}
			conn.close();
		}
		return total;
	}

	/**
	 *
	 * @return a map entry consisting of station id and the number of times that station was involved in rides taken
	 * which represents the station found in most number of rides
	 * @throws SQLException for database access error
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Map.Entry<Integer, Integer> viewMostPopularStation() throws SQLException, ClassNotFoundException {
		HashMap<Integer, Integer> stationCountMap = new HashMap<>();
		Connection conn = connectToDatabase();
		//read in data from database to data structures
		if (conn != null) {
			Statement stmt = conn.createStatement();
			//get station to and station from from all the rides
			ResultSet rs = stmt.executeQuery("SELECT station_to, station_from FROM Ride");
			while (rs.next()) {
				int stationTo = rs.getInt("station_to");
				int stationFrom = rs.getInt("station_from");
				//for each station to check if it is in map
				if (!stationCountMap.containsKey(stationTo)){
					//if not, add to map
					stationCountMap.put(stationTo, 1);
				} else {
					//if yes, increase count of station by 1
					stationCountMap.put(stationTo, stationCountMap.get(stationTo)+1);
				}
				//for each station to check if it is in map
				if (!stationCountMap.containsKey(stationFrom)){
					//if not, add to map
					stationCountMap.put(stationFrom, 1);
				} else {
					//if yes, increase count of station by 1
					stationCountMap.put(stationFrom, stationCountMap.get(stationFrom)+1);
				}
			}
			conn.close();
		}
		//this is most popular station did station id and count
		Map.Entry<Integer, Integer> maxEntry = null;
		for (Map.Entry<Integer, Integer> entry : stationCountMap.entrySet())
		{
			//if count is greater than the count for maxEntry, replace maxEntry
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
			{
				maxEntry = entry;
			}
		}
		//most popular station, or most frequently used station
		return maxEntry;
	}

	/**
	 * Iterates through tree map and outputs bike data by ID order
	 * in a nicely formatted table
	 */
	static void viewBikeList() {
		System.out.println("BIKE LIST:");
		// format table view
		System.out.format("%-10s%-10s%-20s%-10s%-10s\n", "ID", "Stat. ID"," Location",
				"Main. Req", "Main. Report");

		// while the iterator has a next value
		for (Integer key : bikesMap.keySet()) {
			// initiate key for iterator
			// use that key to find bike object in bike tree
			Bike bike = bikesMap.get(key);

			// initialize string as empty
			String locString = " ";

			// get bike location and change to string description of that location
			int loc = bike.getBikeLocation();

			if (loc == 0) {
				locString = "available at station  ";
			} else if (loc == 1) {
				locString = "not available for rent";
			} else if (loc == 2) {
				locString = "currently rented      ";
			}

			String station = " ";
			int stat  = bike.getStation();
			if (stat == 0) {
				station = "out";
			} else {
				station = Integer.toString(stat);
			}
			// format the view of the bike object values
			System.out.format("%-10d%-10s%-10s%-10s%-10s\n",
					key,
					station,
					locString,
					bike.getMnt(),
					bike.getMntReport()

			);
		}
	}

	/**
	 * Loops through station objects in stations map data structure
	 * formats them to a table view for the user
	 */
	static void viewStationList() {
		System.out.println("STATION LIST:");

		// format table view
		System.out.format("%-10s%-10s%-10s%-10s%-10s%-10s%-20s\n", "ID", "Bikes",
				"AvDocs", "MainReq", "Capacity", "Kiosk", "Name - Address");

		// while the iterator has a next value
		for (Integer key : stationsMap.keySet()) {
			// initiate key for iterator
			// use that key to find station object in station tree
			Station station = stationsMap.get(key);

			// format the view values of station object
			System.out.format("%-10d%-10d%-10d%-10d%-10d%-10b%-20s\n",
					key, station.getBikes(),
					station.getAvailableDocks(),
					station.getMaintenanceRequest(),
					station.getCapacity(),
					station.getKioskBoolean(),
					station.getStationName() + "-" + station.getAddress());
		}
	}

	/**
	 * the total capacity of all the stations added together
	 * @return the int that represents the total  capacity of all the stations added together
	 */
	static int viewTotalStationsCapacity(){
		//total capacity of all the stations in station map
		int total = 0;
		//for all the stations in station map
		for (int key : stationsMap.keySet()){
			//add their capacity to total
			Station station = stationsMap.get(key);
			total += station.getCapacity();
		}
		return total;
	}

	/**
	 * the total bikes in Valley Bike system
	 * @return the int that is the total count for all the bikes in bike map
	 */
	static int viewTotalBikesCount(){
		//the total number of bikes in bike map
		return bikesMap.size();
	}

	/**
	 * Adds new customer account to customer account map or asks the user to reenter information if account already exists.
	 *
	 * @param customerAccount this is the new customer account object to be added to the map
	 * @throws IOException the initial menu in the controller throws IOException
	 * @throws ParseException the initial menu in the controller throws ParseException
	 * @throws InterruptedException when a thread that is sleeping, waiting, or is occupied is interrupted
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 * @throws NoSuchAlgorithmException when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws SQLException for database access error
	 */
	public static void addCustomerAccount(CustomerAccount customerAccount) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException, SQLException {
		//if the username for the new customer account is already in the customer account map
		if (customerAccountMap.get(customerAccount.getUsername()) != null) {
			//print that the username already exists
			System.out.println("Customer account with this username already exists.\nPlease try again with another username or log in.");
			//prompt the user to input new account information again or log in
			ValleyBikeController.initialMenu();
		} else {
			String sql = "INSERT INTO Customer_Account(username, password, email_address, credit_card, balance, last_ride_is_returned, enabled, ride_id_string) " +
					"VALUES(?,?,?,?,?,?,?,?)";

			//add customer account to database
			try (Connection conn = connectToDatabase();
				 PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, customerAccount.getUsername());
				pstmt.setString(2, customerAccount.getPassword());
				pstmt.setString(3, customerAccount.getEmailAddress());
				pstmt.setString(4, customerAccount.getCreditCard());
				pstmt.setDouble(5, customerAccount.getBalance());
				pstmt.setInt(6, booleanToInt(customerAccount.getIsReturned()));
				pstmt.setInt(7, booleanToInt(customerAccount.isEnabled()));
				pstmt.setString(8, customerAccount.getRideIdListToString());
				pstmt.executeUpdate();

				//add the new customer account object to customer account map
				customerAccountMap.put(customerAccount.getUsername(), customerAccount);
			} catch (SQLException e) {
				System.out.println("Sorry, something went wrong with adding new customer account to database.");
			}
		}
	}

	/**
	 * converts boolean to integer (true = 1, false = 0)
	 * @param myBoolean is the boolean we want to convert
	 * @return is the equivalent int for the boolean
	 */
	private static int booleanToInt(boolean myBoolean) {
		return myBoolean ? 1 : 0;
	}

	/**
	 * converts int to boolean (0 = false)
	 * @param myInt is the int we want to convert
	 * @return is the equivalent boolean for the int
	 */
	private static boolean intToBoolean(int myInt) {
		if (Objects.equals(myInt, 0)){
			return false;
		}
		return true;
	}

	/**
	 * Adds new customer account to customer account map or asks the user to reenter information if account already exists.
	 *
	 * @param internalAccount is the new internal account to be created
	 * @param username is the username for the internal account user who is creating this internal account
	 *                 only existing internal accounts can create new internal accounts
	 * @throws IOException the initial menu in the controller throws IOException
	 * @throws ParseException the initial menu in the controller throws ParseException
	 * @throws InterruptedException when a thread that is sleeping, waiting, or is occupied is interrupted
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 * @throws NoSuchAlgorithmException when a particular cryptographic algorithm is requested but is not available in the environment.
	 * @throws SQLException for database access error
	 */
	public static void addInternalAccount(InternalAccount internalAccount, String username) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException, SQLException {
		//if the username for the new internal account is already in the customer account map
		if (internalAccountMap.get(internalAccount.getUsername()) != null) {
			//print that the username already exists
			System.out.println("Internal account with this username already exists.\nPlease try again with another username or log in.");
			//prompt the user to input new account information again or log in
			ValleyBikeController.internalAccountHome(username);
		} else {
			String sql = "INSERT INTO Internal_Account(username, password, email_address) " +
					"VALUES(?,?,?)";

			//add new internal account to database
			try (Connection conn = connectToDatabase();
				 PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, internalAccount.getUsername());
				pstmt.setString(2, internalAccount.getPassword());
				pstmt.setString(3, internalAccount.getEmailAddress());
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Sorry, something went wrong with adding new internal account to database.");
			}

			//if the username does not already exist
			//add the new internal account object to internal account map
			internalAccountMap.put(internalAccount.getUsername(), internalAccount);
		}
	}

	/**
	 * create customer account object
	 *
	 * @param username     username of new customer account
	 * @param password     password of new customer account
	 * @param emailAddress email address of new customer account
	 * @param creditCard   credit card of new customer account
	 * @param membership   membership type of new customer account
	 * @throws IOException failure during reading, writing and searching file or directory operations
	 * @throws ParseException fail to parse a String that is ought to have a special format
	 * @throws InterruptedException when a thread that is sleeping, waiting, or is occupied is interrupted
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 * @throws NoSuchAlgorithmException when a particular cryptographic algorithm is requested but is not available in the environment.
	 */
	static void createCustomerAccount(String username, String password, String emailAddress, String creditCard, int membership) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException, SQLException {
    	//create new membership instance
		Membership membershipType = checkMembershipType(membership);
		//create instance of customer object
		CustomerAccount customerAccount = new CustomerAccount(username, password, emailAddress, creditCard, membershipType);
		//add customer account to customer account map
		addCustomerAccount(customerAccount);
		addMembership(membershipType, username);
	}

	/**
	 * adds new membership to database
	 * @param membership is the membership object associated with customer
	 * @param username is the username for the customer who signed up for a membership
	 */
	static void addMembership(Membership membership, String username){
		String sql = "INSERT INTO Membership(username, total_rides_left, last_payment, membership_since, type) " +
				"VALUES(?,?,?,?,?)";

		//add customer account to database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, username);
			pstmt.setInt(2, membership.getTotalRidesLeft());
			pstmt.setString(3, membership.getLastPayment().toString());
			pstmt.setString(4, membership.getMemberSince().toString());
			pstmt.setDouble(5, membership.getMembershipInt());
			pstmt.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Sorry, something went wrong with adding new membership to database.");
		}
	}

	/**
	 * returns membership object for the specific int associated with membership type
	 * @param membership is the int for the membership type
	 * @param totalRidesLeft is the rides left for the user for their membership
	 * @param lastPayment is the last time the user made a payment for their membership
	 * @param memberSince is the day since which the user has had this membership type
	 * @return the membership object for the membership type
	 */
	static Membership checkMembershipType(int membership,  int totalRidesLeft, LocalDate lastPayment, LocalDate memberSince){
		if (membership == 1){
			return new PayAsYouGoMembership(totalRidesLeft, lastPayment, memberSince);
		}
		if (membership == 2){
			return new MonthlyMembership(totalRidesLeft, lastPayment, memberSince);
		}
		if (membership == 3){
			return new YearlyMembership(totalRidesLeft, lastPayment, memberSince);
		}
		return null;
	}

	/**
	 * view longest ride made by user
	 * @param username username of account to check
	 * @return longest ride object
	 */
	static Ride viewLongestRide(String username){
		//fetch all rides belonging to customer
		ArrayList<UUID> rideIdList = customerAccountMap.get(username).getRideIdList();
		long longestRideLength = 0;
		Ride longestRide = null;
		//search through every ride checking for longest one
		for (UUID ride: rideIdList){
			if (rideMap.get(ride).getRideLength() > longestRideLength){
				longestRideLength = rideMap.get(ride).getRideLength();
				longestRide = rideMap.get(ride);
			}
		}
		return longestRide;
	}

	/**
	 * add new station to system
	 *
	 * @param station station instance to be added to system
	 * @param id      id of new station
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean addStation(Station station, Integer id) throws ClassNotFoundException {
		String sql = "INSERT INTO Station(id, name, bikes, available_docks, req_mnt, " +
				"capacity, kiosk, address, bike_string) " +
				"VALUES(?,?,?,?,?,?,?,?,?)";

		//add station to database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			pstmt.setString(2, station.getStationName());
			pstmt.setInt(3, station.getBikes());
			pstmt.setInt(4, station.getAvailableDocks());
			pstmt.setInt(5, station.getMaintenanceRequest());
			pstmt.setInt(6, station.getCapacity());
			pstmt.setInt(7, booleanToInt(station.getKioskBoolean()));
			pstmt.setString(8, station.getAddress());
			pstmt.setString(9, station.getBikeListToString());
			pstmt.executeUpdate();

			//add station to station map
			stationsMap.put(id, station);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, something went wrong with adding new station to database.");
			return false;
		}
	}

	/**
	 * add new bike to system
	 *
	 *
	 * @param bike new bike object to add to system
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static Boolean addBike(Bike bike) throws ClassNotFoundException{
		String sql = "INSERT INTO Bike(id, location, station_id, req_mnt, mnt_report) " +
				"VALUES(?,?,?,?,?)";

		//add bike to database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, bike.getId());
			pstmt.setInt(2, bike.getBikeLocation());
			pstmt.setInt(3, bike.getStation());
			pstmt.setInt(4, booleanToInt(bike.getMnt()));
			pstmt.setString(5, bike.getMntReport());
			pstmt.executeUpdate();

			//add bike to bike map
			bikesMap.put(bike.getId(), bike);

			return true;
		} catch (SQLException e) {
			System.out.println("Sorry, something went wrong with adding new bike to database.");
			return false;
		}
	}

	/**
	 * add a new ride to the system
	 *
	 * @param ride ride object to add
	 * @throws IOException failure during reading, writing and searching file or directory operations
	 * @throws ParseException fail to parse a String that is ought to have a special format
	 * @throws InterruptedException when a thread that is sleeping, waiting, or is occupied is interrupted
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 * @throws NoSuchAlgorithmException when a particular cryptographic algorithm is requested but is not available in the environment.
	 */
	static Boolean addRide(Ride ride) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException, SQLException {
		if (rideMap.get(ride.getRideId()) != null) {
			System.out.println("Ride with this id already exists.\nPlease try again with another username or log in.");
			ValleyBikeController.initialMenu();

			return false;
		} else { //id ride id valid, add to system
			String sql = "INSERT INTO Ride(ride_id, bike_id, username, is_returned, " +
					"ride_length, start_time_stamp, end_time_stamp, payment, station_to, station_from) " +
					"VALUES(?,?,?,?,?,?,?,?,?,?)";

			//add ride to database
			try (Connection conn = connectToDatabase();
				 PreparedStatement pstmt = conn.prepareStatement(sql)) {

				String ride_id = ride.getRideId().toString();
				pstmt.setString(1, ride_id);
				pstmt.setInt(2, ride.getBikeId());
				pstmt.setString(3, ride.getUsername());

				// change int binary to boolean
				int is_returned_int = booleanToInt(ride.getIsReturned());

				pstmt.setInt(4, is_returned_int);
				pstmt.setLong(5, ride.getRideLength());
				pstmt.setString(6, ride.getStartTimeStamp().toString());
				pstmt.setString(7, ride.getEndTimeStamp().toString());
				pstmt.setDouble(8, ride.getPayment());
				pstmt.setInt(9, ride.getStationFrom());
				pstmt.setInt(10, ride.getStationTo());

				pstmt.executeUpdate();

				//add ride to ride map
				rideMap.put(ride.getRideId(), ride);

				return true;
			} catch (SQLException e) {
				System.out.println("Sorry, something went wrong with adding new ride to database.");
				return false;
			}
		}
	}

	/**
	 * Move a bike to a different (or no) station
	 * Also sets station data to match this move
	 *
	 * @param bike bike that is being moved
	 * @param newStationValue station ID of the station the bike is moving to
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be foun
	 */
	static void moveStation(Bike bike, int newStationValue) throws ClassNotFoundException {
		//move bike from last station

		// check if bike has a current station
		// if bike's station is 0, that means that the bike is out with a customer
	    if (! Objects.equals(bike.getStation(),0)) { // if bike is at a station
	        Station oldStation = stationsMap.get(bike.getStation()); // get old station object
			oldStation.removeFromBikeList(bike.getId()); // remove bike from station's bike list
			// update new station bike list to database

			updateStationBikeList(bike.getStation());

			if(!updateStationBikeList(bike.getStation())){
				return;
			}
		}

	    //update station id registered to bike
        if(!updateBikeStationId(bike.getId(), newStationValue)){
        	return;
        }

		// check if new station is a '0,' which is a placeholder station
		if (! Objects.equals(newStationValue, 0)) {
			Station newStation = stationsMap.get(bike.getStation()); // get new station object
			newStation.addToBikeList(bike.getId()); //add to new station's bike list

			// update to database
			updateStationBikeList(bike.getStation());
			updateBikeLocation(bike.getId(), 0);

			if(!updateStationBikeList(bike.getStation())){
				return;
			}

			if(!updateBikeLocation(bike.getId(), 0)){
				return;
			}
		}
		else {
			if(!updateBikeLocation(bike.getId(), 2)){
				return;
			}
		}
	}

	/**
	 * Iterates through stations to determine ideal percentage of vehicles to capacity
	 * for the stations, and maps station ids to their actual percentages
	 *
	 * Then goes through and pulls extra vehicles from stations with percentages over ideal
	 * and sequentially adds those extra vehicles to stations with percentages
	 * under ideal until both have as close as possible to the ideal percentage
	 *
	 * This method prioritizes easily moving vehicles in order to get as many
	 * stations within the ideal percentage as possible
	 * However it does not prioritize stations further from the ideal percentage first
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be foun
	 */
	static void equalizeStations() throws ClassNotFoundException {
		Map<Integer, Integer> stationsCapacity = new TreeMap<>();
		Deque<Bike> extraBikes = new ArrayDeque<>();// Extras contains bike objects
		int idealPercentage = getPercentageData(stationsCapacity);

		// get our bike stack from high percentage stations
		reassignHighPercentage(stationsCapacity, idealPercentage, extraBikes);

		// distribute our bike stack to low percentage stations
		reassignLowPercentage(stationsCapacity, idealPercentage, extraBikes);
	}

	/**
	 * Helper method for equalizeStations()
	 * Iterates through station map and maps station ids to their current percentage
	 *
	 * @param stationsCapacity - Map to add station percentage data to
	 * @return ideal station percentage based on total vehicles and total capacity
	 */
	private static int getPercentageData(Map<Integer, Integer> stationsCapacity) {
		int totalVehicles = 0;
		int totalCapacity = 0;

		//loop through stations to incrementally add total bikes and get current percentage of capacity
		for (Integer key : stationsMap.keySet()) {
			Station station = stationsMap.get(key);

			int percentage = (int) (((float) (station.getBikes()) / station.getCapacity()) * 100);
			totalVehicles = totalVehicles + station.getBikes();
			totalCapacity = totalCapacity + station.getCapacity();

			//add station's current percentage-full to capacity map
			stationsCapacity.put(key, percentage);
		}
		//return ideal capacity percentage for system
		return (int) (((float) totalVehicles / totalCapacity) * 100);
	}

	/**
	 * Helper method for equalizeStations()
	 * Iterates through station percentages and takes vehicles from those
	 * whose percentage is over 10% away from idealPercentage
	 *
	 * @param stationsCapacity - Map of stations to actual percentages
	 * @param idealPercentage  - Percentage stations should ideally have
	 * @param extraBikes       - Stack of bikes taken from high percentage stations
	 * @return all extra bikes that need to get reassigned
	 */
	private static Deque<Bike> reassignHighPercentage(Map<Integer, Integer> stationsCapacity,
													  int idealPercentage, Deque<Bike> extraBikes) throws ClassNotFoundException {
		//loop through stations to get current capacity  take away bikes until matches ideal capacity
		for (Integer key : stationsCapacity.keySet()) {
			int percentage = stationsCapacity.get(key);
			Station station = stationsMap.get(key);

			LinkedList<Integer> bikesAtStation = station.getBikeList();

			if ((percentage - idealPercentage) > 0) { //if station has too many bikes, remove bikes
				int newPercentage = percentage;
				//continues to remove vehicles as long as removing a vehicle
				//moves the percentage closer to ideal percentage
				while (Math.abs(newPercentage - idealPercentage) >
						Math.abs(((int) (((float) (station.getBikes() - 1) / station.getCapacity()) * 100))
								- idealPercentage)) {
					if (!bikesAtStation.isEmpty()) { // if the station isn't empty
						// move one bike from station stack to extra stack
						int bikeID = bikesAtStation.pop();
						Bike bike = getBikeObj(bikeID);
						extraBikes.push(bike);
						moveStation(bike,0); // set bike's station to 0, or no station

						//new capacity percentage after reassigning
						newPercentage = (int) (((float) (station.getBikes()) / station.getCapacity()) * 100);
					}
				}
			}
		}
		//return all extra bikes that need to get reassigned
		return extraBikes;
	}

	/**
	 * Helper method for equalizeStations()
	 * <p>
	 * Reassigns extra bikes and pedelecs to stations with lower percentages
	 * Until their percentages are within appropriate range
	 *
	 * @param stationsCapacity - stations with percentages deemed too low
	 * @param idealPercentage  - percentage to aim for
	 * @param extraBikes       - stack of extra bikes to put in stations
	 */
	private static void reassignLowPercentage(Map<Integer, Integer> stationsCapacity, int idealPercentage, Deque<Bike> extraBikes) throws ClassNotFoundException {
		//loop through stations and add bikes until matches ideal capacity
		for (Integer stationKey : stationsCapacity.keySet()) {
			Station station = stationsMap.get(stationKey);

			int newPercentage = stationsCapacity.get(stationKey);

			// continues to add vehicles as long as adding a vehicle
			// moves the percentage closer to ideal percentage
			// and there are still extra vehicles to add
			while (Math.abs(newPercentage - idealPercentage) >
					Math.abs((int) (((float) (station.getBikes() + 1) /
							station.getCapacity()) * 100)) - idealPercentage) {

				if (!extraBikes.isEmpty()) { // while stack isn't empty
					Bike bike = extraBikes.pop(); // get a bike from our stack
					moveStation(bike, stationKey); // move this bike to the current station
				} else {
					return;
				} // return when stack is empty
				//save new capacity percentage
				newPercentage = (int) (((float) (station.getBikes()) / station.getCapacity()) * 100);
			}
		}
	}

	/**
	 * Resolve all maintenance requests by viewing bike ids
	 * in need of maintenance then setting them to not require
	 * maintenance anymore
	 * @throws ClassNotFoundException tries to load a class through its string name, but no definition for the specified class name could be found
	 */
	static void resolveMntReqs() throws ClassNotFoundException {
		// if there are maintenance requests
		if (mntReqs.size() > 0) {
			System.out.println("Here's a list of bike iDs in need of maintenance and their reports:");

			// loop through all the bike ids in need of maintenance
			for (Map.Entry<Integer, String> entry : mntReqs.entrySet()) {
				// view each id
				System.out.format("%-5d%-20s\n", entry.getKey(), entry.getValue());

				int bikeId = entry.getKey();

				// get bike object
				Bike bike = bikesMap.get(bikeId);

				// set bike maintenance values to none
				if(!updateBikeRqMnt(bikeId, false, "n")){
					return;
				}

				// bike now available for customers
				if(!updateBikeLocation(bikeId, 0)){
					return;
				}

				// get station object as well
				Station stat = stationsMap.get(bike.getStation());

				// get how many maintenance requests the station already had
				int originalMntRqs = stat.getMaintenanceRequest();

				// decrease it by one
				if(!updateStationMntRqsts(bike.getStation(), originalMntRqs - 1)){
					return;
				}
			}

			// done resolving, so clear the list
			mntReqs.clear();

			System.out.println("All maintenance requests have been resolved");
		} else { //if no mnt reqs to solve, tell user
			System.out.println("There are no maintenance requests at the moment.");
		}
	}

	/**
	 * returns membership type based on membership int
	 *
	 * @param membership int (1,2,3) representing membership type
	 * @return membership object corresponding to integer input
	 */
	static Membership checkMembershipType(int membership) {
		if (membership == 1) { //if int 1, membership is PAYG
			return new PayAsYouGoMembership();
		}
		if (membership == 2) { //if int 2, membership is Monthly
			return new MonthlyMembership();
		}
		if (membership == 3) { //if int 3, membership is Yearly
			return new YearlyMembership();
		}
		//if incorrect input, return null
		return null;
	}

	/**
	 * get length of ride list for a customer
	 *
	 * @param username username of account whose rides will be viewed
	 * @return return the size of a customer's ride list
	 */
	static int viewRideListLength(String username) {
		return customerAccountMap.get(username).getRideIdList().size();
	}

	/**
	 * View the account balance associated with a user's account
	 *
	 * @param username the unique username associated with the customer account
	 * @return the balance associate with the account accessed through the username
	 */
	static double viewAccountBalance(String username) {
		return customerAccountMap.get(username).getBalance();
	}

	/**
	 * View the membership type associated with a user's account
	 *
	 * @param username the unique username associated with the customer account
	 * @return the membership type associated with the account accessed through the username
	 */
	static Membership viewMembershipType(String username) {
		return customerAccountMap.get(username).getMembership();
	}

	/**
	 * View the credit card associated with a user's account
	 *
	 * @param username the unique username associated with the customer account
	 * @return the credit card number with the account accessed through the username
	 */
	static String viewCreditCard(String username) {
		return customerAccountMap.get(username).getCreditCard();
	}

	/**
	 * Helper method for controller class to get station object by
	 * finding it in the stations tree data structure and using station
	 * ID
	 *
	 * @param key station id
	 * @return station object
	 */
	static Station getStationObj(int key) {
		return stationsMap.get(key);
	}

	/**
	 * Helper method for controller class to get customer object by
	 * finding it in customer account map and returning it
	 *
	 * @param key username of desired account
	 * @return customer account matching inputted username
	 */
	static CustomerAccount getCustomerObj(String key) {
		return customerAccountMap.get(key);
	}

	/**
	 * Helper method for controller class to get internal object by
	 * finding it in internal account map and returning it
	 *
	 * @param key username of desired account
	 * @return internal account matching inputted username
	 */
	static InternalAccount getInternalObj(String key) {
		return internalAccountMap.get(key);
	}


	/**
	 * Helper method for controller class to get bike object by
	 * finding it in the bikes tree data structure and using station ID
	 *
	 * @param key station id
	 * @return station object
	 */
	static Bike getBikeObj(int key) {
		return bikesMap.get(key);
	}

	/**
	 * Returns ride object matching ride id
	 *
	 * @param key unique ride id
	 * @return ride object matching inputted id
	 */
	static Ride getRideObj(UUID key) {
		return rideMap.get(key);
	}

	/**
	 * Helper method for controller class to add bike id of a bike that
	 * requires maintenance to the maintenance requests list
	 *
	 * @param bikeID integer UD of bike
	 */
	static void addToMntRqs(int bikeID, String mntRq) {
		mntReqs.put(bikeID, mntRq);
	}

	/**
	 * returns whether station exists in system
	 *
	 * @param key station id to search for
	 * @return boolean representing whether station id already exists
	 */
	static Boolean stationsMapContains(int key) {
		return stationsMap.containsKey(key);
	}

	/**
	 * checks if a bike exists in our map with the parameter id
	 *
	 * @param key bike id
	 * @return true if bike with id exists
	 */
	static Boolean bikesMapContains(int key) {
		return bikesMap.containsKey(key);
	}
}
