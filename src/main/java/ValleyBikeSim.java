import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that tracks, updates, edits data involved in the ValleyBike Share system
 *
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

	private static Map<UUID, Ride> rideMap = new HashMap<>();

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

	/**
	 * Reads in the stations csv file data and parses it into station objects
	 * mapped by id for easy access and manipulation throughout program
	 * <p>
	 * Then outputs welcome message and menu selector
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
		System.out.print("\nWelcome to ValleyBike Share!");
		ValleyBikeController.initialMenu();
	}

	/**
	 * Method to create connection to SQL database
	 *
	 * @return connection to SQL database
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private static Connection connectToDatabase() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		String dbURL = "jdbc:sqlite:ValleyBike.db";
		return DriverManager.getConnection(dbURL);
	}

	/**
	 * Reads in info from database and converts to customer objects that get stored in data structure
	 *
	 * @param stmt //TODO
	 * @throws SQLException
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
			int balance = Integer.parseInt(rs.getString("balance"));
			String rideIdString = rs.getString("ride_id_string");
			ArrayList<UUID> rideIdList = new ArrayList<>();
			if (rideIdString != null && rideIdString.length() > 0){
				for (String ride : rideIdString.split(",")) {
					String s2 = ride.replace("-", "");
					UUID uuid = new UUID(
							new BigInteger(s2.substring(0, 16), 16).longValue(),
							new BigInteger(s2.substring(16), 16).longValue());
					rideIdList.add(uuid);
				}
			}
			//create customer account from info
			CustomerAccount customerAccount = new CustomerAccount(username, password, emailAddress, creditCard, membership, balance, lastRideIsReturned == 1, enabled == 1, rideIdList);

			//TODO AG - Here's where our null pointer is coming from! getLastPayment field is not being updated when we read in account data, so it stays null...
			//TODO AG - We also have to read in max rides for memberships

			// add to the customer account map
			customerAccountMap.put(username, customerAccount);
		}
	}

	private static Membership readMembershipData(String username) throws ClassNotFoundException, SQLException{
		String sql = "SELECT * FROM Membership WHERE username = ?";

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
				memberSince = LocalDate.parse(rows.getString("member_since"), formatter);
			}
		}
		return checkMembershipType(type, totalRidesLeft, lastPayment, memberSince);
	}
		/**
	 * Reads in info from database and converts to internal account objects that get stored in data structure
	 *
	 * @param stmt //TODO
	 * @throws SQLException
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
	 * @param stmt
	 * @throws SQLException
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

			if (bikeString != null) {
				for (String bikeId : bikeString.split(",")) {
					try{
                        bikeList.add(Integer.parseInt(bikeId));
                    } catch(NumberFormatException e){}
				}
			}

			//create new station instance
			Station station = new Station(name, reqMnt, capacity, kiosk, address, bikeList);

			// add to the station tree
			stationsMap.put(id, station);
		}
	}

	/**
	 * Reads in info from database and converts to bike objects that get stored in data structure
	 *
	 * @param stmt //TODO
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private static void readBikeData(Statement stmt) throws SQLException, ClassNotFoundException {
		ResultSet rs = stmt.executeQuery("SELECT * FROM Bike");
		//get values from each line in table
		while (rs.next()) {
			int id = rs.getInt("id");
			int location = rs.getInt("location");
			int stationId = rs.getInt("station_id");
			int reqMnt = rs.getInt("req_mnt");

			String maintenance = "n";

			if (reqMnt == 1) {
				maintenance = "y";
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
	 * @param stmt //TODO
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws ParseException
	 */
	private static void readRideData(Statement stmt) throws SQLException, ClassNotFoundException, ParseException {
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
			int station_to = rs.getInt("station_ti");

			// change string to unique UUID
			UUID uuid_id = UUID.fromString(id);
			// initiate boolean value to false
			boolean is_returned_bool = false;

			// if found 1 then set it to true
			if (is_returned == 1) {
				is_returned_bool = true;
			}

			// change starting time stamp string to instant
			Instant start_time_stamp_instant = LocalDateTime.parse(start_time_stamp,
					DateTimeFormatter.ofPattern("hh:mm a, MM/dd/yyy", Locale.US)
			).atZone(ZoneId.of("America/New_York")).toInstant();

			// initialize end time stamp as nul since the ride could not have been returned
			Instant end_time_stamp_instant = null;

			// if it was returned, set ending time stamp too
			if (is_returned_bool) {
				end_time_stamp_instant = LocalDateTime.parse(end_time_stamp,
						DateTimeFormatter.ofPattern("hh:mm a, MM/dd/yyy", Locale.US)
				).atZone(ZoneId.of("America/New_York")).toInstant();
			}

			// create new ride object with fields
			Ride ride = new Ride(uuid_id, bike_id, username,
					is_returned_bool, start_time_stamp_instant,
					end_time_stamp_instant, station_from, station_to);

			ride.setRideLength(rideLength);
			updateRidePayment(uuid_id, payment);

			// add to the bike tree
			rideMap.put(uuid_id, ride);
		}
	}

	/*
	 * Updates the number of maintenance requests at a station
	 *
	 * @param stationId the station id that will get updated
	 * @param mntRqsts  the number of maintenance requests the station should have
	 * @throws ClassNotFoundException
	 */
	static void updateStationMntRqsts(int stationId, int mntRqsts) throws ClassNotFoundException {
		String sql = "UPDATE Station SET trq_mnt = ? "
		+ "WHERE id = ?";

		//update sql database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			// set the corresponding param
			pstmt.setInt(2, mntRqsts);
			pstmt.setInt(2, stationId);
			// update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Sorry, could not update bike's station id in database at this time.");
		}
		//update station data in map
		stationsMap.get(stationId).setMaintenanceRequest(mntRqsts);
	}

	/**
	 * Adds a bike to a station
	 *
	 * @param stationId the station id that will get updated
	 * @param bikeId    the bike to be added to the station
	 * @throws ClassNotFoundException
	 */
	static void addBikeToStation(int stationId, int bikeId) throws ClassNotFoundException {
		String sql = "UPDATE Station SET bike_string = ? "
				+ "WHERE id = ?";
		//add bike to list of bikes at station in station map
		stationsMap.get(stationId).addToBikeList(bikeId);
		String bikeIdsString = stationsMap.get(stationId).getBikeListToString();

		//add bike to station in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			// set the corresponding param
			pstmt.setString(1, bikeIdsString);
			pstmt.setInt(2, bikeId);
			// update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Sorry, could not add ride id to list in database at this time.");
		}
		System.out.println("Your ride has been successfully added to your history.");
	}

	static void updateStationBikesNum(int stationId, int bikes) throws ClassNotFoundException{
		String sql = "UPDATE Station SET bikes = ? "
				+ "WHERE id = ?";

		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setInt(1, bikes);
			pstmt.setInt(2, stationId);

			// update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Sorry, could not update station's bikes nums in database at this time.");
		}
	}

	static void updateStationBikeList(int stationId, int bikeId) throws ClassNotFoundException{
		String sql = "UPDATE Station SET bike_string = ? "
				+ "WHERE id = ?";
		stationsMap.get(stationId).addToBikeList(bikeId);
		String bikeIdsString = stationsMap.get(stationId).getBikeListToString();

		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, bikeIdsString);
			pstmt.setInt(2, bikeId);
			// update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Sorry, could not add ride id to list in database at this time.");
		}

		System.out.println("Your ride has been successfully added to your history.");
	}


	/**
	 * Update the station id that the bike is registered at
	 *
	 * @param bikeId       the bike to update
	 * @param newStationId the station id to assign to the bike
	 * @throws ClassNotFoundException
	 */
	static void updateBikeStationId(int bikeId, int newStationId) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update bike's station id in database at this time.");
		}
		bikesMap.get(bikeId).setStation(newStationId);
	}

	/**
	 * Update whether bike is available at station, currently rented, or not available
	 *
	 * @param bikeId          the bike to receive a new location
	 * @param newBikeLocation integer representing bike location
	 * @throws ClassNotFoundException
	 */
	static void updateBikeLocation(int bikeId, int newBikeLocation) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update bike location in database at this time.");
		}

		bikesMap.get(bikeId).setBikeLocation(newBikeLocation);
	}

	/**
	 * Update whether a bike has a maintenance request
	 *
	 * @param bikeId  bike id to  update
	 * @param req_mnt boolean whether bike has a maintenance request
	 * @throws ClassNotFoundException
	 */
	static void updateBikeRqMnt(int bikeId, boolean req_mnt) throws ClassNotFoundException {
		String sql = "UPDATE Bike SET req_mnt = ? "
				+ "WHERE id = ?";

		//update maintenance request in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// change boolean to binary
			int req_mnt_int = 0;
			if (req_mnt) req_mnt_int = 1;

			// set the corresponding param
			pstmt.setInt(1, req_mnt_int);
			pstmt.setInt(2, bikeId);

			// update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Sorry, could not update bike maintenance in database at this time.");
		}
		//update maintenance request in bike map
		bikesMap.get(bikeId).setMnt(req_mnt);
		//TODO do we ever delete/add to maintenance requests map? NS
	}

	/**
	 * Update the string description of a bike's maintenance report
	 *
	 * @param bikeId     the bike that has the maintenance request
	 * @param mnt_report the new report string
	 * @throws ClassNotFoundException
	 */
	static void updateBikeMntReport(int bikeId, String mnt_report) throws ClassNotFoundException {
		String sql = "UPDATE Bike SET mnt_report = ? "
				+ "WHERE id = ?";

		//update maint req string in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, mnt_report);
			pstmt.setInt(2, bikeId);

			// update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Sorry, could not update bike maintenance report in database at this time.");
		}

		//update bike maint req in bike map
		bikesMap.get(bikeId).setMntReport(mnt_report);
		//TODO do we update string in maint reqs map? NS
	}

	/**
	 * Update whether a ride has been returned to a station
	 *
	 * @param rideId     the ride object being updated
	 * @param isReturned boolean representing whether the ride has been returned
	 * @throws ClassNotFoundException
	 */
	static void updateRideIsReturned(UUID rideId, Boolean isReturned) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
		}

		//update ride in ride map
		rideMap.get(rideId).setIsReturned(isReturned);
		//TODO is the ride being updated in the customer's ride list? NS or AG
	}

	/**
	 * update the time the ride was returned
	 *
	 * @param rideId         the ride object being updated
	 * @param end_time_stamp the ride end time
	 * @throws ClassNotFoundException
	 */
	static void updateRideEndTimeStamp(UUID rideId, Instant end_time_stamp) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
		}

		//update ride end timestamp in ride map
		rideMap.get(rideId).setEndTimeStamp(end_time_stamp);
		//TODO is ride end time stamp being update in customer ride list? NS or AG
	}

	/**
	 * Updates cost of ride
	 *
	 * @param rideId  ride being updated
	 * @param payment cost of ride
	 * @throws ClassNotFoundException
	 */
	static void updateRidePayment(UUID rideId, double payment) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
		}

		//update payment for ride in ride map
		rideMap.get(rideId).setPayment(payment);

		//TODO what is ride payment if ride was included in membership, not payg? NS or AG
	}


	/**
	 * update customer's email address
	 *
	 * @param username        username of account to be updated
	 * @param newEmailAddress new email address
	 * @throws ClassNotFoundException
	 */
	static void updateCustomerEmailAddress(String username, String newEmailAddress) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
		}

		//update customer email address in customer map
		customerAccountMap.get(username).setEmailAddress(newEmailAddress);
		System.out.println("Your email address has been successfully updated to " + newEmailAddress);
	}

	/**
	 * if customer deletes their account, disable account so cannot be logged into but preserve data
	 *
	 * @param username username of account to disable
	 * @throws NoSuchAlgorithmException
	 * @throws ClassNotFoundException
	 */
	static void disableCustomerAccount(String username) throws NoSuchAlgorithmException, ClassNotFoundException {
		CustomerAccount customerAccount = customerAccountMap.get(username);
		//remove customer from map of customers
		customerAccountMap.remove(username);
		MessageDigest md = MessageDigest.getInstance("MD5");
		//update password to random hashcode
		StringBuilder sb = new StringBuilder();
		byte[] hashInBytesPassword = md.digest(customerAccount.getPassword().getBytes(StandardCharsets.UTF_8));
		for (byte b : hashInBytesPassword) {
			sb.append(String.format("%02x", b));
		}
		updateCustomerPassword(username, sb.toString());
		//update email address to random hashcode
		sb.setLength(0);
		byte[] hashInBytesEmailAddress = md.digest(customerAccount.getEmailAddress().getBytes(StandardCharsets.UTF_8));
		for (byte b : hashInBytesEmailAddress) {
			sb.append(String.format("%02x", b));
		}
		//TODO internal email address? why?? AG
		updateInternalEmailAddress(username, sb.toString());
		//update username to random hashcode
		sb.setLength(0);
		byte[] hashInBytesUsername = md.digest(customerAccount.getUsername().getBytes(StandardCharsets.UTF_8));
		for (byte b : hashInBytesUsername) {
			sb.append(String.format("%02x", b));
		}
		updateCustomerUsername(username, sb.toString());
		//set customer account field to disabled
		updateCustomerDisabled(sb.toString());
	}

	/**
	 * Set account to disabled to customer cannot log in
	 *
	 * @param username username of account to be disabled
	 * @throws ClassNotFoundException
	 */
	static void updateCustomerDisabled(String username) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not delete account at this time.");
		}

		//update account in customer map
		//TODO the username was removed from the map in disableCustomerAccount- should that be changed?
		// the username passed in to this account has already been changed to hash as well- AG
		customerAccountMap.get(username).setEnabled(false);
		System.out.println("Your account has been successfully deleted.");
	}

	/**
	 * update internal employee's email address
	 *
	 * @param username        username of account to be updated
	 * @param newEmailAddress new email address for account
	 * @throws ClassNotFoundException
	 */
	static void updateInternalEmailAddress(String username, String newEmailAddress) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update email address in database at this time.");
		}

		//update email address in internal account map
		internalAccountMap.get(username).setEmailAddress(newEmailAddress);
		System.out.println("Your email address has been successfully updated to " + newEmailAddress);
	}

	/**
	 * update username for customer account
	 *
	 * @param username    username of account to be updated
	 * @param newUsername new username for account
	 * @throws ClassNotFoundException
	 */
	static void updateCustomerUsername(String username, String newUsername) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update username in database at this time.");
		}

		//update customer username in customer account map
		CustomerAccount customerAccount = customerAccountMap.get(username);
		customerAccount.setUsername(newUsername);
		customerAccountMap.remove(username);
		customerAccountMap.put(newUsername, customerAccount);
		System.out.println("Your username has been successfully updated to " + newUsername);
		//TODO do existing ride objects associated with user get their username updated? AG or NS

	}

	/**
	 * update customer's list of rides they've taken
	 *
	 * @param username username of account to be updated
	 * @param rideId   new ride to add to list
	 * @throws ClassNotFoundException
	 */
	static void updateRideIdList(String username, UUID rideId) throws ClassNotFoundException {
		String sql = "UPDATE Customer_Account SET ride_id_string = ? "
				+ "WHERE username = ?";

		String rideIdString = customerAccountMap.get(username).getRideIdListToString();

		//update customer's ride list in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, rideIdString);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Sorry, could not add ride id to list in database at this time.");
		}

		//add new ride to customer's ride list
		customerAccountMap.get(username).addNewRide(rideId);
		System.out.println("Your ride has been successfully added to your history.");
	}

	/**
	 * update field in customer account that states whether they currently have a rental
	 *
	 * @param username           username of account to update
	 * @param lastRideisReturned boolean representing whether last bike was returned
	 * @throws ClassNotFoundException
	 */
	static void updateLastRideisReturned(String username, boolean lastRideisReturned) throws ClassNotFoundException {
		//TODO neamat implement in correct place, this method also updates the customer map obj
		String sql = "UPDATE Customer_Account SET last_ride_is_returned = ? "
				+ "WHERE username = ?";

		int lastRideReturnedInt;
		if (lastRideisReturned) {
			lastRideReturnedInt = 1;
		} else {
			lastRideReturnedInt = 0;
		}

		//update field in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setInt(1, lastRideReturnedInt);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Sorry, could not add ride id to list in database at this time.");
		}
		//update field in customer account map
		customerAccountMap.get(username).setLastRideIsReturned(lastRideisReturned);
		System.out.println("Your ride has been successfully added to your history.");
	}

	/**
	 * update customer account password
	 *
	 * @param username    username of account to update
	 * @param newPassword new password for account
	 * @throws ClassNotFoundException
	 */
	static void updateCustomerPassword(String username, String newPassword) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update password in database at this time.");
		}

		//update customer password in customer map
		customerAccountMap.get(username).setPassword(newPassword);
		System.out.println("Your password has been successfully updated to " + newPassword);
	}

	/**
	 * update credit card for customer account
	 *
	 * @param username      username of account to update
	 * @param newCreditCard new credit card
	 * @throws ClassNotFoundException
	 */
	static void updateCustomerCreditCard(String username, String newCreditCard) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update credit card information in database at this time.");
		}

		//update customer credit card in customer account map
		customerAccountMap.get(username).setCreditCard(newCreditCard);
		System.out.println("Your credit card information has been successfully updated to " + newCreditCard);
	}

	/**
	 * update membership type for customer
	 *
	 * @param username      username of account to update
	 * @param newMembership new membership type for account
	 * @throws ClassNotFoundException
	 */
	static void updateCustomerMembership(String username, int newMembership) throws ClassNotFoundException {
		String sql = "UPDATE Customer_Account SET membership = ? "
				+ "WHERE username = ?";

		//update membership type in database
		try (Connection conn = connectToDatabase();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setInt(1, newMembership);
			pstmt.setString(2, username);
			// update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Sorry, could not update membership in database at this time.");
		}

		//update membership type associated with user and date representing start of membership
		customerAccountMap.get(username).setMembership(checkMembershipType(newMembership));
		customerAccountMap.get(username).getMembership().setMemberSince(LocalDate.now());
		System.out.println("Your credit card information has been successfully updated to " + Objects.requireNonNull(checkMembershipType(newMembership)).getMembershipString());
	}


	/**
	 * updates username of internal account
	 *
	 * @param username    username of account to update
	 * @param newUsername new internal username
	 * @throws ClassNotFoundException
	 */
	static void updateInternalUsername(String username, String newUsername) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update username in database at this time.");
		}

		//update internal account username in internal account map
		InternalAccount internalAccount = internalAccountMap.get(username);
		internalAccount.setUsername(newUsername);
		internalAccountMap.remove(username);
		internalAccountMap.put(newUsername, internalAccount);
		System.out.println("Your username has been successfully updated to " + newUsername);
	}

	/**
	 * update password for intenal account
	 *
	 * @param username    username for internal account to update
	 * @param newPassword new password for account
	 * @throws ClassNotFoundException
	 */
	static void updateInternalPassword(String username, String newPassword) throws ClassNotFoundException {
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
		} catch (SQLException e) {
			System.out.println("Sorry, could not update password in database at this time.");
		}

		//update password in internal account map
		internalAccountMap.get(username).setPassword(newPassword);
		System.out.println("Your password has been successfully updated to " + newPassword);
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
		return totalRideTime / rideIdList.size();
	}

	/**
	 * print formatted list of all customers
	 */
	static void viewAllCustomers() {
		//if there exist customers, print
		if (customerAccountMap.size() > 0) {
			// format table view
			System.out.format("%-20s\n", "Customer username");

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
    	switch (num){
			case 1: //search customer map
				if (customerAccountMap.containsKey(username)){
					return true;
				}
			case 2: //search internal map
				if (internalAccountMap.containsKey(username)){
					return true;
				}
			case 3: //search both maps
				if (customerAccountMap.containsKey(username)||internalAccountMap.containsKey(username)){
				return true;
			}
		}
        return false;
    }

	/**
	 * Method to check whether customer already has a bike rented and whether the rental has
	 * gone on for too long (in which caase they are charged)
	 *
	 * @param username is the unique username associated with the customer account
	 */
	static void checkBikeRented(String username) throws ParseException, InterruptedException {
		// get customer object
		CustomerAccount customer = ValleyBikeSim.getCustomerObj(username);
		// true if last ride was returned
		Boolean isReturned = customer.getIsReturned();
		if (!isReturned) {
			UUID ride = customer.getLastRideId();
			if (rideMap.get(ride).isRented24Hours()) {
				// if rental exceeds 24 hours, charge account 150 and notify user
				System.out.println("Your bike rental has exceeded 24 hours. You have been charged a late fee of " +
						"$150 to your credit card.");
				//TODO wht do we do with rented bike? delete from system? Keep in system at station 0?
				// do we reset customer's account so they can make more rentals now that they've paid the fine?
				//ASSUMPTION: In a real system, here we would send an email confirmation of their credit card charge
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
	 */
	static void checkMembershipRenewalTime() throws ClassNotFoundException {
		//check each user's membership to find whether their payment is due
		for (String username : customerAccountMap.keySet()) {
			// initiate key for iterator
			CustomerAccount user = customerAccountMap.get(username);
			if (user.getMembership().checkPaymentDue()) {
				if (ValleyBikeController.isValidCreditCard(user.getCreditCard())) {
					if (user.getMembership().getMembershipInt() == 2) {
						//TODO monthly things
						user.getMembership().setTotalRidesLeft(20);
					} else if (user.getMembership().getMembershipInt() == 3) {
						//TODO yearly things
						user.getMembership().setTotalRidesLeft(260);
					}
					user.getMembership().setLastPayment(LocalDate.now());
					//ASSUMPTION: In a real system, here emails would be sent out to all members whose memberships
					// have just been renewed, letting them know their card was charged
				} else {
					//if credit card cannot be charged, reset membership to pay-as-you-go
					updateCustomerMembership(username, 1);
					//ASSUMPTION: In a real system, here we would send out emails notifying users that
					//they had been switched to a PAYG member because their credit card was not valid
				}
			}
		}
	}

	/**
	 * Iterates through tree map and outputs bike data by ID order
	 * in a nicely formatted table
	 */
	static void viewBikeList() {
		// format table view
		System.out.format("%-10s%-10s%-10s%-10s%-10s\n", "ID", "Location", "Stat. ID",
				"Main. Req", "Main. Report");

		// while the iterator has a next value
		for (Integer key : bikesMap.keySet()) {
			// initiate key for iterator
			// use that key to find bike object in bike tree
			Bike bike = bikesMap.get(key);

			// format the view of the bike object values
			System.out.format("%-10d%-10d%-10d%-10s%-10s\n",
					key,
					bike.getBikeLocation(),
					bike.getStation(),
					bike.getMnt(),
					bike.getMntReport()
			);
		}
	}

	/**
	 * Loops through station objects in stations map data structure
	 * formats them to a table view for the user
	 *
	 * @throws ParseException
	 */
	static void viewStationList() {
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
	 * Adds new customer account to customer account map or asks the user to reenter information if account already exists.
	 *
	 * @param customerAccount this is the new customer account object to be added to the map
	 * @throws IOException    the initial menu in the controller throws IOException
	 * @throws ParseException the initial menu in the controller throws ParseException
	 */
	public static void addCustomerAccount(CustomerAccount customerAccount) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
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
				pstmt.setInt(5, customerAccount.getBalance());
				pstmt.setInt(6, booleanToInt(customerAccount.getIsReturned()));
				pstmt.setInt(7, booleanToInt(customerAccount.isEnabled()));
				pstmt.setString(8, customerAccount.getRideIdListToString());
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Sorry, something went wrong with adding new customer account to database.");
			}

			//if the username does not already exist
			//add the new customer account object to customer account map
			customerAccountMap.put(customerAccount.getUsername(), customerAccount);
		}
	}

	private static int booleanToInt(boolean myBoolean) {
		return myBoolean ? 1 : 0;
	}

	/**
	 * Adds new customer account to customer account map or asks the user to reenter information if account already exists.
	 *
	 * @throws IOException    the initial menu in the controller throws IOException
	 * @throws ParseException the initial menu in the controller throws ParseException
	 */
	public static void addInternalAccount(InternalAccount internalAccount, String username) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
		//if the username for the new customer account is already in the customer account map
		if (customerAccountMap.get(internalAccount.getUsername()) != null) {
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
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	static void createCustomerAccount(String username, String password, String emailAddress, String creditCard, int membership) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
    	Membership membershipType = checkMembershipType(membership);

    	//TODO: Pay for monthly and yearly membership

		//create instance of customer object
		CustomerAccount customerAccount = new CustomerAccount(username, password, emailAddress, creditCard, membershipType);
		//add customer account to customer account map
		addCustomerAccount(customerAccount);
	}


	static Membership checkMembershipType(int membership, int totalRidesLeft, LocalDate lastPayment, LocalDate memberSince){
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
	 * Verify username and password when a customer logs in to their account
	 *
	 * @param username is the username input by the user to log in
	 * @param password is the password input by the user to log in
	 * @throws IOException    the initial menu and user account home method in the controller throw IOException
	 * @throws ParseException the initial menu and user account home method in the controller throw ParseException
	 */
	static void customerLogIn(String username, String password) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
		//if the username entered by the user does not exist in the customer account map
		if (!customerAccountMap.containsKey(username)) {
			//print that the account does not exist
			System.out.println("This account does not exist.");
			//prompt the user to input new account information again or log in
			ValleyBikeController.initialMenu();
		}
		//if the username exists but the password entered by the user does not match the password associated with that username
		if (!password.equals(customerAccountMap.get(username).getPassword())) {
			//print incorrect password
			System.out.println("Incorrect password.");
			//prompt the user to input new account information again or log in
			ValleyBikeController.initialMenu();
		}
		//if the username and password both match with associated customer account object, lead the user to user account home
		ValleyBikeController.customerAccountHome(username);
	}

	static Ride viewLongestRide(String username){
		ArrayList<UUID> rideIdList = customerAccountMap.get(username).getRideIdList();
		long longestRideLength = 0;
		Ride longestRide = null;
		for (UUID ride: rideIdList){
			if (rideMap.get(ride).getRideLength() > longestRideLength){
				longestRideLength = rideMap.get(ride).getRideLength();
				longestRide = rideMap.get(ride);
			}
		}
		return longestRide;
	}


	/**
	 * Verify username and password when an internal staff logs in to their account
	 *
	 * @param username is the username input by the user to log in
	 * @param password is the password input by the user to log in
	 * @throws IOException    the initial menu and user account home method in the controller throw IOException
	 * @throws ParseException the initial menu and user account home method in the controller throw ParseException
	 */
	static void internalLogIn(String username, String password) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
		//if the username entered by the user does not exist in the internal account map
		if (!internalAccountMap.containsKey(username)) {
			//print that the account does not exist
			System.out.println("This account does not exist.");
			//take the user back to the initial menu
			return;
		}
		//if the username exists but the password entered by the user does not match the password associated with that username
		if (!password.equals(internalAccountMap.get(username).getPassword())) {
			//print incorrect password
			System.out.println("Incorrect password.");
			//take the user back to the initial menu
			return;
		}
		//if the username and password both match with associated customer account object, lead the user to internal account home
		ValleyBikeController.internalAccountHome(username);
	}

	/**
	 * add new station to system
	 *
	 * @param station station instance to be added to system
	 * @param id      id of new station
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	static void addStation(Station station, Integer id) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
		if (stationsMap.get(id) != null) { //if station id already exists, inform user
			System.out.println("Station with this id already exists.\nPlease try again with another username or log in.");
			ValleyBikeController.initialMenu();
		} else { //if station is valid, add to system
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
				pstmt.setInt(7, station.getKioskNum());
				pstmt.setString(8, station.getAddress());
                pstmt.setString(9, "");
                pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Sorry, something went wrong with adding new customer account to database.");
			}
			//add station to station map
			stationsMap.put(id, station);
		}
	}

	/**
	 * add new bike to system
	 *
	 * @param bike new bike object to add to system
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	static void addBike(Bike bike) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
		if (bikesMap.get(bike.getId()) != null){//if bike id already in system, inform user
			System.out.println("Bike with this id already exists.\nPlease try again with another username or log in.");
			//TODO is this ^ the message you want to say? Do you want to return to initial menu? NS
			ValleyBikeController.initialMenu();
		} else { //if bike valid, add to system
			String sql = "INSERT INTO Bike(id, location, station_id, req_mnt, mnt_report) " +
					"VALUES(?,?,?,?,?)";

			//add bike to database
			try (Connection conn = connectToDatabase();
				 PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setInt(1, bike.getId());
				pstmt.setInt(2, bike.getBikeLocation());
				pstmt.setInt(3, bike.getStation());
				pstmt.setBoolean(4, bike.getMnt());
				pstmt.setString(5, bike.getMntReport());
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Sorry, something went wrong with adding new customer account to database.");
			}

			//add bike to bike map
			bikesMap.put(bike.getId(), bike);
		}
	}

	/**
	 * add a new ride to the system
	 *
	 * @param ride ride object to add
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	static void addRide(Ride ride) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
		if (rideMap.get(ride.getRideId()) != null) {
			System.out.println("Ride with this id already exists.\nPlease try again with another username or log in.");
			ValleyBikeController.initialMenu();
			//TODO is this the message you want to say? Do you want to return to initial menu? NS
		} else { //id ride id valid, add to system
			String sql = "INSERT INTO Ride(ride_id, bike_id, username, is_returned, " +
					"ride_length, start_time_stamp, end_time_stamp, payment, station_to, station_from) " +
					"VALUES(?,?,?,?,?,?,?, ?,?, ?)";

			//add ride to database
			try (Connection conn = connectToDatabase();
				 PreparedStatement pstmt = conn.prepareStatement(sql)) {
				String ride_id = ride.getRideId().toString();
				pstmt.setString(1, ride_id);
				pstmt.setInt(2, ride.getBikeId());
				pstmt.setString(3, ride.getUsername());

				int is_returned_int = 0;

				if (ride.getIsReturned()) {
					is_returned_int = 1;
				}

				pstmt.setInt(5, is_returned_int);
				pstmt.setLong(6, ride.getRideLength());
				pstmt.setString(7, ride.getStartTimeStamp().toString());
				pstmt.setString(8, ride.getEndTimeStamp().toString());
				pstmt.setInt(9, ride.getStationFrom());
				pstmt.setInt(10, ride.getStationTo());

				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Sorry, something went wrong with adding new customer account to database.");
			}

			//add ride to ride map
			rideMap.put(ride.getRideId(), ride);
			//TODO has this ride been added to customer list already? NS
		}
	}

	/**
	 * Move a bike to a different (or no) station
	 * Also sets station data to match this move
	 *
	 * @param newStationValue - station ID of the station the bike is moving to
	 */
	static void moveStation(Bike bike, int newStationValue) throws ClassNotFoundException {
		//TODO ME why check if bike's station is 0 then get the station?
	    if (! Objects.equals(bike.getStation(),0)) { // check if bike had an old station; '0' represents a bike without a current station
	        Station oldStation = stationsMap.get(bike.getStation()); // get old station object
			oldStation.removeFromBikeList(bike); // remove bike from station's bike list
		}

        updateBikeStationId(bike.getId(), newStationValue);

		if (! Objects.equals(newStationValue, 0)) { // check if new station is a '0,' which is a placeholder station
			Station newStation = stationsMap.get(bike.getStation()); // get new station object
			updateStationBikeList(bike.getStation(), bike.getId());
			updateBikeLocation(bike.getId(), 0);
		}

		else {
			updateBikeLocation(bike.getId(), 2);
		}
	}

	/**
	 * Iterates through stations to determine ideal percentage of vehicles to capacity
	 * for the stations, and maps station ids to their actual percentages
	 * <p>
	 * Then goes through and pulls extra vehicles from stations with percentages over ideal
	 * and sequentially adds those extra vehicles to stations with percentages
	 * under ideal until both have as close as possible to the ideal percentage
	 * <p>
	 * This method prioritizes easily moving vehicles in order to get as many
	 * stations within the ideal percentage as possible
	 * However it does not prioritize stations further from the ideal percentage first
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
	 * @return
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
	 */
	static void resolveMntReqs() throws ClassNotFoundException {
		// if there are maintenance requests
		if (mntReqs != null) {
			System.out.println("Here's a list of bike iDs in need of maintenance and their reports.");
			// loop through all the bike ids in need of maintenance
			for (Map.Entry<Integer, String> entry : mntReqs.entrySet()) {
				// view each id
				System.out.format("%-5d%-20s\n", entry.getKey(), entry.getValue());

				int bikeId = entry.getKey();

				// get bike object
				Bike bike = bikesMap.get(bikeId);

				// set bike maintenance values to none
				updateBikeRqMnt(bikeId, false);
				updateBikeMntReport(bikeId, "n");

				// bike now available for customers
				bike.setBikeLocation(0);
				updateBikeLocation(bikeId, 0);

				// get station object as well
				Station stat = stationsMap.get(bike.getStation());

				// get how many maintenance requests the station already had
				int originalMntRqs = stat.getMaintenanceRequest();

				// decrease it by one
				updateStationMntRqsts(bike.getStation(), originalMntRqs - 1);
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
	 * Helper method for controller class to return a key set
	 * iterator
	 *
	 * @param isBike If true, we're working with a bike object.
	 *               If not, we're working with a station object.
	 * @return if isBike is true, we're returning a bikesMap iterator.
	 * if not, we're returning a stationsMap iterator.
	 */
	private static Iterator createIterator(Boolean isBike) {
		if (isBike) {
			return bikesMap.keySet().iterator();
		} else {
			return stationsMap.keySet().iterator();
		}
	}



	/**
	 * get length of ride list for a customer
	 *
	 * @param username username of account whose rides will be viewed
	 * @return return the size of a customer's ride list
	 */
	static int viewTotalRides(String username) {
		return customerAccountMap.get(username).getRideIdList().size();
		//TODO rename to get rideListLength ? NS
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



	//TODO remove this method if we are sure- it takes in data files eesh
	/**
	 * We are not currently using this method.
	 * <p>
	 * Takes in a ride data file name from user
	 * Parses and iterates through file and uses ride timestamps
	 * to determine average ride time.
	 * <p>
	 * Prints out number of rides for that day and average time
	 *
	 * @throws IOException
	 * @throws ParseException
	 */
	static void resolveData(String dataFile) throws IOException, ParseException {
		System.out.println("Enter the file name (including extension) of the file located"
				+ "in data-files: ");

		FileReader fileReader = new FileReader("data-files/" + dataFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String rideLine;

		int rides = 0;
		long totalTime = 0;
		long averageTime;

		bufferedReader.readLine();

		while ((rideLine = bufferedReader.readLine()) != null) {
			rides = rides + 1;
			String[] values = rideLine.split(",");

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date startDate = dateFormat.parse(values[3]);
			Timestamp startTime = new Timestamp(startDate.getTime());
			Date endDate = dateFormat.parse(values[4]);
			Timestamp endTime = new Timestamp(endDate.getTime());
			totalTime = totalTime + (endTime.getTime() - startTime.getTime());
		}

		bufferedReader.close();

		//Just averaging to the closest number of minutes here for simplicity
		averageTime = (totalTime / rides) / 60000;
		System.out.println("Number of rides: " + rides);
		System.out.println("Average ride time in minutes: " + averageTime);
	}

	/**
	 * Return a stack of bikes docked at a particular station
	 *
	 * @param statId The int ID of the station from which we will
	 *               collect the list of docked bikes.
	 * @return bikesAtStation - a stack of bikes docked at this station
	 */
	static Deque<Bike> getBikesAtStation(int statId) {
		//TODO We don't need this method!!!
		//initiate our bike stack
		Deque<Bike> bikesAtStation = new ArrayDeque<>();
		// initiate iterator
		Iterator bikeKeyIterator = ValleyBikeSim.createIterator(true);

		// keep looping until there is no next value
		while (bikeKeyIterator.hasNext()) {
			Integer key = (Integer) bikeKeyIterator.next();
			Bike bike = ValleyBikeSim.getBikeObj(key);
			if (statId == bike.getStation()) { // if the bike's station ID matches this station
				bikesAtStation.push(bike); // add it to stack
			}
		}
		return bikesAtStation;
	}
}