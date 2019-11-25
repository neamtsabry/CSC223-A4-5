import java.io.*;
import java.util.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Class that contains menu options and implementation for Simulator
 *
 * We're making the assumption in this bike that our system only has
 * pedelecs. Every time we use the word bike in this code, we mean
 * pedelecs/electric bikes.
 */
public class ValleyBikeSim {
	/** data structure for keeping track of stations */
	static Map<Integer, Station> stationsMap = new TreeMap<>();

	/** data structure for keeping track of bikes */
	static Map<Integer, Bike> bikesMap = new TreeMap<>();

	/** list for storing bike ids of bikes that require maintenance */
	static ArrayList<Integer> mntReqs = new ArrayList<>();

	/** data structure for keeping track of customer accounts */
	private static Map<String, CustomerAccount> customerAccountMap = new HashMap<>();

	/** data structure for keeping track of internal accounts */
	private static Map<String, InternalAccount> internalAccountMap = new HashMap<>();

	/** 
	 * Reads in the stations csv file data and parses it into station objects
	 * mapped by id for easy access and manipulation throughout program
	 * 
	 * Then outputs welcome message and menu selector
	 */
	public static void main(String[] args) throws IOException, ParseException {
		// read all required files
		readStationData();
        readBikeData();
        readCustomerAccountData();
        readInternalAccountData();

        // start the initial menu
		ValleyBikeController.initialMenu();
	}

	/**
	 * Reads external csv file with customer account data
	 *
	 * @throws IOException readLine throws IOException
	 */
	private static void readCustomerAccountData() throws IOException {
		// start reading our designated file
		FileReader fileReader = new FileReader("data-files/customer-account-data.csv");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		bufferedReader.readLine();

		// initialize string for customer account data
		String line;

		// while there's more lines to read in the file
		while((line = bufferedReader.readLine()) != null){
			// store comma separated values in string array
			String[] values = line.split(",");


			// start a new customer account with all the individual values we got
			CustomerAccount accountObj = new CustomerAccount(
					values[0],
					values[1],
					values[2],
					values[3],
					values[4],
					Integer.parseInt(values[5]));


			// add to the customer account map
			customerAccountMap.put(values[0],accountObj);
		}

		// close our reader
		bufferedReader.close();
	}

	/**
	 * Reads external csv file with internal account data
	 *
	 * @throws IOException readLine throws IOException
	 */
	private static void readInternalAccountData() throws IOException {
		// start reading our designated file
		FileReader fileReader = new FileReader("data-files/internal-account-data.csv");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		bufferedReader.readLine();

		// initialize string for internal account data
		String line;

		// while there's more lines to read in the file
		while((line = bufferedReader.readLine()) != null){
			// store comma separated values in string array
			String[] values = line.split(",");

			// start a new internal account with all the individual values we got
			InternalAccount accountObj = new InternalAccount(
					values[0],
					values[1],
					values[2]);

			// add to the internal account map
			internalAccountMap.put(values[0],accountObj);
		}

		// close our reader
		bufferedReader.close();
	}


	/**
	 * View the account balance associated with a user's account
	 *
	 * @param username the unique username associated with the customer account
	 *
	 * @return the balance associate with the account accessed through the username
	 */
	static int viewAccountBalance(String username) {
		return customerAccountMap.get(username).getBalance();
	}

	/**
	 * Reads external csv file with station data and adds it to the
     * tree data structure
	 *
	 * @throws IOException readLine throws IOException
	 */
	private static void readStationData() throws IOException {
        // start reading our designated file
        FileReader fileReader = new FileReader("data-files/station-data.csv");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        bufferedReader.readLine();

        // initialize string for station data
        String line;

        // while there's more lines to read in the file
        while((line = bufferedReader.readLine()) != null){
            // store comma separated values in string array
            String[] values = line.split(",");

            // start a new station with all the individual values we got
            Station stationOb = new Station(
                    values[1],
                    Integer.parseInt(values[2]),
                    Integer.parseInt(values[3]),
                    Integer.parseInt(values[4]),
                    Integer.parseInt(values[5]),
                    Integer.parseInt(values[6]),
                    values[7]);

            // add to the station tree
            stationsMap.put(Integer.parseInt(values[0]),stationOb);
        }

        // close our reader
        bufferedReader.close();
    }

	/**
	 * Reads external csv file with bike data
	 *
	 * @throws IOException readLine throws IOException
	 */
	private static void readBikeData() throws IOException {
		// start reading our designated file
        FileReader fileReader = new FileReader("data-files/bikeData.csv");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        bufferedReader.readLine();

        // initialize string for bike data
        String line;

        // while there's more lines to read in the file
        while((line = bufferedReader.readLine()) != null){
            // store comma separated values in string array
            String[] values = line.split(",");

            // start a new bike with all the individual values we got
            Bike bikeOb = new Bike(
                    Integer.parseInt(values[0]),
                    Integer.parseInt(values[1]),
                    Integer.parseInt(values[2]),
                    values[3],
                    values[4]);

            // add to the bike tree
            bikesMap.put(Integer.parseInt(values[0]), bikeOb);
        }

        // close our reader
        bufferedReader.close();
    }
	
	/**
	 * Iterates through tree map and outputs bike data by ID order
	 * in a nicely formatted table
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	static void viewBikeList() throws IOException, ParseException{
		// format table view
		System.out.format("%-15s%-15s%-15s%-15s%-15s\n", "ID", "Location", "Station ID",
				"Main. Req", "Main. Report");

		// initiate iterator
        Iterator<Integer> keyIterator1 = bikesMap.keySet().iterator();

		// while the iterator has a next value
		while(keyIterator1.hasNext()){
			// initiate key for iterator
            Integer key = (Integer) keyIterator1.next();
            // use that key to find bike object in bike tree
			Bike bike = bikesMap.get(key);

			// format the view of the bike object values
			System.out.format("%-15d%-15d%-15d%-15s%-15s\n",
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
	 * @throws IOException
	 * @throws ParseException
	 */
    static void viewStationList() throws IOException, ParseException{
		// format table view
        System.out.format("%-10s%-10s%-10s%-10s%-10s%-10s%-20s\n", "ID", "Bikes",
                "AvDocs", "MainReq", "Cap", "Kiosk","Name - Address");

		// initiate iterator
        Iterator<Integer> keyIterator2 = stationsMap.keySet().iterator();

		// while the iterator has a next value
		while(keyIterator2.hasNext()) {
			// initiate key for iterator
            Integer key = (Integer) keyIterator2.next();
			// use that key to find station object in station tree
			Station station = stationsMap.get(key);

			// format the view values of station object
			System.out.format("%-10d%-10d%-10d%-10d%-10d%-10d%-20s\n",
					key, station.getBikes(),
					station.getAvailableDocks(),
					station.getMaintenanceRequest(),
					station.getCapacity(),
					station.getKioskNum(),
					station.getStationName() + "-" + station.getAddress());
		}
    }

	/**
	 * Adds new customer account to customer account map or asks the user to reenter information if account already exists.
	 *
	 * @param customerAccount this is the new customer account object to be added to the map
	 *
	 * @throws IOException the initial menu in the controller throws IOException
	 * @throws ParseException the initial menu in the controller throws ParseException
	 */
    public static void addCustomerAccount(CustomerAccount customerAccount) throws IOException, ParseException{
    	//if the username for the new customer account is already in the customer account map
    	if (customerAccountMap.get(customerAccount.getUsername()) != null){
    		//print that the username already exists
			System.out.println("Customer account with this username already exists. \nPlease try again with another username or log in.");
			//prompt the user to input new account information again or log in
			ValleyBikeController.initialMenu();
		} else {
    		//if the username does not already exist
			//add the new customer account object to customer account map
    		customerAccountMap.put(customerAccount.getUsername(), customerAccount);
		}
	}

	/**
	 * Verify username and password when a customer logs in to their account
	 *
	 * @param username is the username input by the user to log in
	 * @param password is the password input by the user to log in
	 *
	 * @throws IOException the initial menu and user account home method in the controller throw IOException
	 * @throws ParseException the initial menu and user account home method in the controller throw ParseException
	 */
	public static void customerLogIn(String username, String password) throws IOException, ParseException{
		//if the username entered by the user does not exist in the customer account map
    	if (!customerAccountMap.containsKey(username)){
    		//print that the account does not exist
    		System.out.println("This account does not exist.");
    		//prompt the user to input new account information again or log in
    		ValleyBikeController.initialMenu();
		}
    	//if the username exists but the password entered by the user does not match the password associated with that username
    	if (!password.equals(customerAccountMap.get(username).getPassword())){
    		//print incorrect password
    		System.out.println("Incorrect password.");
			//prompt the user to input new account information again or log in
			ValleyBikeController.initialMenu();
		}
    	//if the username and password both match with associated customer account object, lead the user to user account home
		ValleyBikeController.customerAccountHome(username);
	}

	/**
	 * Verify username and password when an internal staff logs in to their account
	 *
	 * @param username is the username input by the user to log in
	 * @param password is the password input by the user to log in
	 *
	 * @throws IOException the initial menu and user account home method in the controller throw IOException
	 * @throws ParseException the initial menu and user account home method in the controller throw ParseException
	 */
	public static void internalLogIn(String username, String password) throws IOException, ParseException{
		//if the username entered by the user does not exist in the internal account map
		if (!internalAccountMap.containsKey(username)){
			//print that the account does not exist
			System.out.println("This account does not exist.");
			//take the user back to the initial menu
			ValleyBikeController.initialMenu();
		}
		//if the username exists but the password entered by the user does not match the password associated with that username
		if (!password.equals(internalAccountMap.get(username).getPassword())){
			//print incorrect password
			System.out.println("Incorrect password.");
			//take the user back to the initial menu
			ValleyBikeController.initialMenu();
		}
		//if the username and password both match with associated customer account object, lead the user to internal account home
		ValleyBikeController.internalAccountHome();
	}

	/**
	 * Overwrites old customer account data in csv with updated data from customerAccountMap
	 *
	 * @throws IOException
	 */
	public static void saveCustomerAccountList() throws IOException {
		// initiate fileWriter and iterator
		FileWriter customerAccountsWriter = new FileWriter("data-files/customer-account-data.csv");
		Iterator<String> keyIterator = customerAccountMap.keySet().iterator();

		// write the labels at the beginning of the file
		customerAccountsWriter.write("Username,Password,Email Address,Credit Card,Membership,Balance");

		// loop through customer accounts and transform customer account object
		while(keyIterator.hasNext()){
			String key = keyIterator.next();
			CustomerAccount customerAccount = customerAccountMap.get(key);
			customerAccountsWriter.write("\n");
			customerAccountsWriter.write(customerAccount.getCustomerAccountString());
		}

		// then end the fileWriter
		customerAccountsWriter.flush();
		customerAccountsWriter.close();
	}

	/**
	 * Overwrites old station data in csv with updated data from stationMap
	 * 
	 * The choice to overwrite all data instead of simply adding new stations
	 * to existing file was made because recording rides can update information
	 * in both old and added stations, and it was important to make sure those updates
	 * were reflected in the new saved list as well
	 * 
	 * @throws IOException
	 */
	static void saveStationList() throws IOException {
		// initiate fileWriter and iterator
		FileWriter stationsWriter = new FileWriter("data-files/station-data.csv");
		Iterator<Integer> keyIterator = stationsMap.keySet().iterator();

		// write the labels at the beginning of the file
		stationsWriter.write("ID,Name,Bikes,Available Docks,"
				+ "Maintenance Request,Capacity,Kiosk,Address");

		// loop through station tree and transform station object
		// to comma separated values for every row
		while(keyIterator.hasNext()){
			Integer key = (Integer) keyIterator.next();
			Station station = stationsMap.get(key);
			stationsWriter.write("\n");
			stationsWriter.write(key.toString() + "," + station.getStationString());
		}

		// then end the fileWriter
		stationsWriter.flush();
		stationsWriter.close();
	}

	/**
	 * Overwrites old bike data in external csv file with updated data from
	 * bike tree map
	 *
	 * The choice to overwrite all data instead of simply adding new bikes
	 * to existing file was made because recording rides can update information
	 * in both old and added bikes, and it was important to make sure those updates
	 * were reflected in the new saved list as well
	 *
	 * @throws IOException
	 * @throws ParseException
	 */
	static void saveBikeList() throws IOException, ParseException{
		// initiate fileWriter and iterator
		FileWriter bikesWriter = new FileWriter("data-files/bikeData.csv");
		Iterator<Integer> keyIterator = bikesMap.keySet().iterator();

		// write the labels at the beginning of the file
		bikesWriter.write("ID,Location,StationId,Req_Mnt,Mnt_Report");

		// loop through bike tree and transform bike object
		// to comma separated values for every row
		while(keyIterator.hasNext()){
			Integer key = (Integer) keyIterator.next();
			Bike bike = bikesMap.get(key);
			bikesWriter.write("\n");
			bikesWriter.write(bike.getBikeString());
		}

		// then end the fileWriter
		bikesWriter.flush();
		bikesWriter.close();
	}

	/**
	 * Takes in a ride data file name from user
	 * Parses and iterates through file and uses ride timestamps 
	 * to determine average ride time.
	 * 
	 * Prints out number of rides for that day and average time
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	static void resolveData(String dataFile) throws IOException, ParseException{
		System.out.println("Enter the file name (including extension) of the file located"
				+ "in data-files: ");

		FileReader fileReader = new FileReader("data-files/"+ dataFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String rideLine;

		int rides = 0;
		long totalTime = 0;
		long averageTime = 0;

		bufferedReader.readLine();

		while((rideLine = bufferedReader.readLine()) != null){
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
		averageTime = (totalTime/rides)/60000;
		System.out.println("Number of rides: " + rides);
		System.out.println("Average ride time in minutes: " + averageTime);
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
	 * 
	 * Decided to include both bikes and pedelecs, but not to differentiate
	 * between the two when looking at percentages, because there were not
	 * separate designated capacities for bikes and pedelecs in the stations
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void equalizeStations() throws IOException, ParseException{
		Map<Integer, Integer> stationsCapacity = new TreeMap<>();
		//Extras contains number of extra bikes (position 0) and extra pedelecs (position 1)
		ArrayList<Integer> extras = new ArrayList<>();
		extras.add(0);
		extras.add(0);
		
		int idealPercentage = getPercentageData(stationsCapacity);		
		reassignHighPercentage(stationsCapacity, idealPercentage, extras);
		reassignLowPercentage(stationsCapacity, idealPercentage, extras);
		
	}
	
	/**
	 * Helper method for equalizeStations()
	 * Iterates through station map and maps station ids to their current percentage
	 * 
	 * @param stationsCapacity - Map to add station percentage data to
	 * @return ideal station percentage based on total vehicles and total capacity
	 */
	private static int getPercentageData(Map<Integer, Integer> stationsCapacity){
		int totalVehicles = 0;
		int totalCapacity = 0;

        Iterator<Integer> keyIterator = stationsMap.keySet().iterator();

        while(keyIterator.hasNext()){
            Integer key = (Integer) keyIterator.next();
			Station station = stationsMap.get(key);

			int percentage = (int) (((float) (station.getBikes()) / station.getCapacity()) * 100);
			totalVehicles = totalVehicles + station.getBikes();
			totalCapacity = totalCapacity + station.getCapacity();

			stationsCapacity.put(key, percentage);
		}
		int idealPercentage = (int) (((float) totalVehicles/totalCapacity) * 100);

		return idealPercentage;
	}
	
	/**
	 * Helper method for equalizeStations()
	 * Iterates through station percentages and takes vehicles from those
	 * whose percentage is over 10% away from idealPercentage
	 * 
	 * @param stationsCapacity - Map of stations to actual percentages
	 * @param idealPercentage - Percentage stations should ideally have
	 * @param extras - Bikes/peds taken from high percentage stations
	 */
	private static void reassignHighPercentage(Map<Integer, Integer> stationsCapacity, 
			int idealPercentage, ArrayList<Integer> extras){
		Iterator<Integer> capacityIterator = stationsCapacity.keySet().iterator();
		while (capacityIterator.hasNext()){
			Integer key = (Integer) capacityIterator.next();
			int percentage = (Integer) stationsCapacity.get(key);
			Station station = stationsMap.get(key);

			if((percentage - idealPercentage) > 0){
                int newPercentage = percentage;
                //continues to remove vehicles as long as removing a vehicle
                //moves the percentage closer to ideal percentage
                while(Math.abs(newPercentage - idealPercentage) >
                Math.abs(((int) (((float) (station.getBikes() - 1) / station.getCapacity()) * 100))
                        - idealPercentage)){
                    if(station.getBikes() > 0){
                        station.setBikes(station.getBikes() - 1);
                        extras.set(0, extras.get(0)+1);
                    } else {
                        extras.set(1, extras.get(1)+1);
                    }
                    newPercentage = (int) (((float) (station.getBikes()) / station.getCapacity()) * 100);
				}
			}
		}
	}
	
	/**
	 * Helper method for equalizeStations()
	 * 
	 * Reassigns extra bikes and pedelecs to stations with lower percentages
	 * Until their percentages are within appropriate range
	 * 
	 * @param stationsCapacity - stations with percentages deemed too low
	 * @param idealPercentage - percentage to aim for
	 */
	private static void reassignLowPercentage(Map<Integer, Integer> stationsCapacity,
			int idealPercentage, ArrayList<Integer> extras){
		Iterator<Integer> capacityIterator = stationsCapacity.keySet().iterator();
		while (capacityIterator.hasNext()){
			Integer key = (Integer) capacityIterator.next();
			Station station = stationsMap.get(key);

			int newPercentage = stationsCapacity.get(key);

			// continues to add vehicles as long as adding a vehicle
			// moves the percentage closer to ideal percentage
			// and there are still extra vehicles to add

			while((Math.abs(newPercentage - idealPercentage) >
					Math.abs((int) (((float) (station.getBikes() + 1) /
							station.getCapacity()) * 100)) - idealPercentage)
					&& Integer.sum(extras.get(0), extras.get(1))>0){

				if(extras.get(0) > 0){
						station.setBikes(station.getBikes() + 1);
						extras.set(0, extras.get(0)-1);
					} else{
						extras.set(1, extras.get(1)-1);
					}

					newPercentage = (int) (((float) (station.getBikes()) / station.getCapacity()) * 100);
			}
		}
	}

	/**
	 * Resolve all maintenance requests by viewing bike ids
	 * in need of maintenance then setting them to not require
	 * maintenance anymore
	 */
	static void resolveMntReqs(){
		// if there are maintenance requests
		if(mntReqs != null) {
			System.out.println("Here's a list of bike iDs in need of maintenance");
			// loop through all the bike ids in need of maintenance
			for(int bikeId : mntReqs){
				// view each id
				System.out.format("%-5d\n", bikeId);

				// get bike object
				Bike bike = bikesMap.get(bikeId);

				// set bike maintenance values to none
				bike.setMnt(false);
				bike.setMntReport("");

				// bike now available for customers
				bike.setBikeLocation(0);

                // get station object as well
                Station stat = stationsMap.get(bike.getStation());

                // get how many maintenance requests the station already had
                int originalMntRqs = stat.getMaintenanceRequest();

                // decrease it by one
                stat.setMaintenanceRequest(originalMntRqs -1 );
			}

			// done resolving, so clear the list
			mntReqs.clear();

			System.out.println("All maintenance requests have been resolved");
		} else{
			System.out.println("There are no maintenance requests at the moment.");
		}
	}

	/**
	 * Helper method for controller class to get station object by
	 * finding it in the stations tree data structure and using station
	 * ID
	 *
	 * @param key station id
	 * @return station object
	 */
	static Station getStationObj(int key){
		return stationsMap.get(key);
	}

	/**
	 * Helper method for controller class to add new station object
	 * to stations tree data structure
	 *
	 * @param id station id
	 * @param stationOb station object
	 */
	static void addNewStation(int id, Station stationOb){
		stationsMap.put(id, stationOb);
	}
    /**
	 * Helper method for controller class to get bike object by
	 * finding it in the bikes tree data structure and using station ID
	 * @param key station id
	 * @return station object
	 */
	static Bike getBikeObj(int key){
		return bikesMap.get(key);
	}

	/**
	 * Helper method for controller class to add new bike object
	 * to bikes tree data structure
	 * @param id station id
	 * @param bikeObj bike object
	 */
	static void addNewBike(int id, Bike bikeObj){
		bikesMap.put(id, bikeObj);
	}

	/**
	 * Helper method for controller class to return a key set
	 * iterator
	 * @param isBike If true, we're working with a bike object.
	 *               If not, we're working with a station object.
	 * @return if isBike is true, we're returning a bikesMap iterator.
	 *         if not, we're returning a stationsMap iterator.
	 *
	 */
	static Iterator createIterator(Boolean isBike){
		if(isBike){
			return bikesMap.keySet().iterator();
		} else{
			return stationsMap.keySet().iterator();
		}
	}

	/**
	 * Helper method for controller class to add bike id of a bike that
	 * requires maintenance to the maintenance requests list
	 *
	 * @param bikeID integer UD of bike
	 */
	static void addToMntRqs(int bikeID){ mntReqs.add(bikeID); }
}