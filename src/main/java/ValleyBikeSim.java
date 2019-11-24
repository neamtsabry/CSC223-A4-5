import java.io.*;
import java.util.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Class that contains menu options and implementation for Simulator
 */
public class ValleyBikeSim {
	/** data structure for keeping track of stations */
	protected static Map<Integer, Station> stationsMap = new TreeMap<>();

	/** data structure for keeping track of bikes */
	protected static Map<Integer, Bike> bikesMap = new TreeMap<>();

	/** list for storing bike ids of bikes that require maintenance */
	protected static ArrayList<Integer> mntReqs = new ArrayList<>();

	/** scanner object to take user's input */
	protected static Scanner input = new Scanner(System.in);

	/** data structure for keeping track of customer accounts */
	protected static Map<String, CustomerAccount> customerAccountMap = new HashMap<>();

	/** data structure for keeping track of internal accounts */
	protected static Map<String, InternalAccount> internalAccountMap = new HashMap<>();

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
	 *
	 * @throws IOException
	 */
	public static void readCustomerAccountData() throws IOException {
		// start reading our designated file
		FileReader fileReader = new FileReader("data-files/customer-account-data.csv");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		bufferedReader.readLine();

		// initialize string for station data
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


			// add to the station tree
			customerAccountMap.put(values[0],accountObj);
		}

		// close our reader
		bufferedReader.close();
	}

	/**
	 *
	 * @throws IOException
	 */
	public static void readInternalAccountData() throws IOException {
		// start reading our designated file
		FileReader fileReader = new FileReader("data-files/internal-account-data.csv");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		bufferedReader.readLine();

		// initialize string for station data
		String line;

		// while there's more lines to read in the file
		while((line = bufferedReader.readLine()) != null){
			// store comma separated values in string array
			String[] values = line.split(",");

			// start a new customer account with all the individual values we got
			InternalAccount accountObj = new InternalAccount(
					values[0],
					values[1],
					values[2]);

			// add to the station tree
			internalAccountMap.put(values[0],accountObj);
		}

		// close our reader
		bufferedReader.close();
	}

	/**
	 * @param: userID- the unique id associated with the user
	 * View the account balance associated with a user's account
	 */
	public static int viewAccountBalance(String username) {
		return customerAccountMap.get(username).getBalance();
	}

	/**
	 * Reads external csv file with station data
	 *
	 * @throws IOException
	 */
	public static void readStationData() throws IOException {
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
                    values[8]);

            // add to the station tree
            stationsMap.put(Integer.parseInt(values[0]),stationOb);
        }

        // close our reader
        bufferedReader.close();
    }

	/**
	 * Reads external csv file with bike data
	 *
	 * @throws IOException
	 */
	public static void readBikeData() throws IOException {
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

            // start a new station with all the individual values we got
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
	public static void viewBikeList() throws IOException, ParseException{
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
					bike.location,
					bike.station,
					bike.mnt,
					bike.mntReport
			);
		}

	}

	/**
	 *
	 * @throws IOException
	 * @throws ParseException
	 */
    public static void viewStationList() throws IOException, ParseException{
		// format table view
    	System.out.format("%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-20s\n", "ID", "Bikes",
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
			System.out.format("%-15d%-15d%-15d%-15d%-15d%-15d%-15b%-20s\n",
					key, station.bikes,
					station.availableDocks,
					station.maintenanceRequest,
					station.capacity,
					station.kioskBoolean,
					station.name + "-" + station.address);
		}
    }

	/**
	 *
	 * @param customerAccount
	 * @throws IOException
	 * @throws ParseException
	 */
    public static void addCustomerAccount(CustomerAccount customerAccount) throws IOException, ParseException{
    	if (customerAccountMap.get(customerAccount.getUsername()) != null){
			System.out.println("Customer account with this username already exists. \nPlease try again with another username or log in.");

			ValleyBikeController.createAccount();
		} else {
    		customerAccountMap.put(customerAccount.getUsername(), customerAccount);
		}
	}

	public static void customerLogIn(String username, String password) throws IOException, ParseException{
    	if (!customerAccountMap.containsKey(username)){
    		System.out.println("This account does not exist.");
    		ValleyBikeController.logIn();
		}
    	if (!password.equals(customerAccountMap.get(username).getPassword())){
    		System.out.println("Incorrect password.");
			ValleyBikeController.logIn();
		}
		ValleyBikeController.userAccountHome(username);
	}

	public static void internalLogIn(String username, String password) throws IOException, ParseException{
		if (!internalAccountMap.containsKey(username)){
			System.out.println("This account does not exist.");
			ValleyBikeController.logIn();
		}
		if (!password.equals(internalAccountMap.get(username).getPassword())){
			System.out.println("Incorrect password.");
			ValleyBikeController.logIn();
		}
		ValleyBikeController.internalAccountHome();
	}

	/**
	 * Overwrites old customer account data in csv with updated data from customerAccountMap
	 *
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void saveCustomerAccountList() throws IOException, ParseException{
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
	 * Prompts user for all station data and then creates a new station
	 * object which is added to the stationMap
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void addStation() throws IOException, ParseException{
		// use helper function to check input is valid and save it
		//TODO this seems to have controller/view things in it
		Integer id = getResponse("Please enter the ID for this station:");
		// handle if the station already exists
		if(stationsMap.get(id) != null){
			// let user know
			System.out.println("Station with this ID already exists. \nWould you like to override "
					+ stationsMap.get(id).name + " with new data? (y/n):");

			// take their input
			String response = input.next();

			// if yes, then take user to the maintenance worker account
			if(response.toLowerCase().equalsIgnoreCase("y")){
				ValleyBikeController.internalAccountHome();
			}
		}

		// prompt user for station name
		System.out.println("Please enter station name: ");
		input.nextLine();
		String name = input.nextLine();

		// prompt user for number of bikes
		Integer bikes = getResponse("How many bikes?");

		// prompt for number of available docks at station
		Integer availableDocks = getResponse("How many available docks?");

		// number of maintenance requests
		Integer maintenanceRequest = getResponse("How many maintenance requests?");

		// prompt capacity for station
		Integer capacity = getResponse("What is the station's capacity?");

		// number of kiosks
		Integer kiosk = getResponse("How many kiosks?");

		// prompt for the station's address
		System.out.print("Please enter station address: ");
		input.nextLine();
		String address = input.nextLine();

		// create new station object with received data from user
		Station stationOb = new Station(
				name,
				bikes,
				availableDocks,
				maintenanceRequest,
				capacity,
				kiosk,
				address);

		// add to the station tree
		stationsMap.put(id, stationOb);
	}

	/**
	 * This method enables maintenance workers to add new bikes
	 *
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void addBike() throws IOException, ParseException{
		// get new bike's id
		Integer id = getResponse("Please enter the bike's ID");

		// handle if the bike already exists
		if(bikesMap.get(id) != null){
			System.out.println("Bike with this ID already exists. \nWould you like to override bike "
					+ bikesMap.get(id) + " with new data? (y/n):");
			String response = input.next();

			// if yes, take user back to the internal account home
			// so they edit bike instead
			if(response.equalsIgnoreCase("Y")){
				ValleyBikeController.internalAccountHome();
			}
		}

		// prompt for the station id bike will be located in
		Integer stationId = getResponse("Please enter the ID for the station the bike is located at:");

		// get station object with that id
		Station myStation = stationsMap.get(stationId);;

		// check if station doesn't exist
		while(myStation == null){
			System.out.println("Station with this ID doesn't exist. \nWould you like to add  "
					+ stationsMap.get(id) + " as a new station? (y/n):");
			String response = input.next();

			// if yes, then redirect to add station
			if(response.toLowerCase().equalsIgnoreCase("y")){
				addStation();
			}
		}

		// prompt user if it requires maintenance
		System.out.println("Does it require maintenance? (y/n): ");
		input.nextLine();
		String mnt = input.nextLine();

		// initiate maintenance report string
		String mntReport = "";

		// if it does require maintenance
		if(mnt.toLowerCase().equalsIgnoreCase("y")){
			// prompt user to enter maintenance report
			System.out.println("Please enter maintenance report:");
			input.nextLine();
			mntReport = input.nextLine();

			// this increases the bike's station maintenance requests
			myStation.maintenanceRequest ++;
		}

		// give appropriate choices for bike's location
		System.out.println("Please pick one of the following choices for the " +
				"status of the bike:\n" +
				"0: Docked/available at station\n" +
				"1: Live with customer\n" +
				"2: Docked/out of commission\n");

		// prompt user to select an option
		Integer bikeLocation = getResponse("Please enter one of the above options:");

		// while the answer is not between the options, keep prompting user
		while(!(bikeLocation == 0 | bikeLocation == 1 || bikeLocation ==2)){
			System.out.println("Your answer has to be between 0 and 2");
			bikeLocation = getResponse("Please enter one of the above options:");
		}

		// increase number of bikes and available docks
		// if location of bike is available and docked
		if(bikeLocation == 0){
			// increase number of bikes for station
			myStation.bikes ++;
			myStation.availableDocks ++;
		}

		// create new bike object based on user's inputs
		Bike bikeOb = new Bike(
				id,
				bikeLocation,
				stationId,
				mnt,
				mntReport
		);

		// add to bike tree structure
		bikesMap.put(id, bikeOb);
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
	 * @throws ParseException
	 */
	public static void saveStationList() throws IOException, ParseException{
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
	public static void saveBikeList() throws IOException, ParseException{
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
	 * Takes in ride data from user
	 * Checks to see if stations are valid
	 * Then if they are it checks for vehicle type and space
	 * If ride is valid it updates station info and creates ride object
	 * Else if ride is invalid it prints an error message
	 *
	 * @param - destination whether
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void recordRide(String dest, String action, Boolean isReturned) throws IOException, ParseException{
		// View stations
		System.out.println("Here's a list of station IDs and their names");
		System.out.format("%-10s%-10s\n", "ID", "Name");

		// initiate iterator
		Iterator<Integer> keyIterator = stationsMap.keySet().iterator();

		// while it has a next value
		while(keyIterator.hasNext()){
			Integer key = (Integer) keyIterator.next();
			Station station = stationsMap.get(key);
			System.out.format("%-10d%-10s\n", key, station.name);
		}

		// choose station to rent from
		System.out.println("Please enter station id to " + action +
				" " + dest + ": ");
		String fromTo = input.next();

		// designated station, whether bike returned to or bike rented from
		Station stationFromTo = stationsMap.get(Integer.parseInt(fromTo));

		// keep prompting user until the station obj is not null
		while(stationFromTo == null) {
			System.out.println("The station entered does not exist in our system.");
			fromTo = input.next();
			stationFromTo = stationsMap.get(Integer.parseInt(fromTo));
		}

		// if there's more than one bike at station
		if (stationFromTo.bikes > 1){
			// station now has one less bike
			stationFromTo.bikes --;
			// and one more available dock
			stationFromTo.availableDocks ++;
		} else {
			// if there's less, notify maintenance worker to resolve data
			System.out.println("Station is almost empty!");
			System.out.println("Notifying maintenance worker to resolve this...");
			equalizeStations();
			System.out.println("All done!");
		}

		// view available bike ids at station
		System.out.println("Here's a list of bike IDs at this station");
		System.out.format("%-10s%-10s\n", "Station", "Bike ID");

		Iterator<Integer> keyIterator2 = bikesMap.keySet().iterator();

		while(keyIterator2.hasNext()){
			Integer key = (Integer) keyIterator2.next();
			Bike bike = bikesMap.get(key);
			if(Integer.parseInt(fromTo) == bike.getStation()) {
				System.out.format("%-10s%-10d\n", fromTo, key);
			}

		}

		// choose bike to rent
		int b = getResponse("bike id");
		Bike someBike = bikesMap.get(b);

		while(someBike == null) {
			System.out.println("The bike ID entered does not exist in our system.");
			b = getResponse("bike id");
			someBike = bikesMap.get(b);
		}

		// time stamp recorded
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		String formattedDate = sdf.format(date);

		// make following adjustments if bike is to be returned
		if(isReturned){
			// if returned, then bike location is available and docked
			someBike.location = 0;

			// prompt user if it needs maintenance
			System.out.println("Does it require maintenance? (y/n): ");
			input.nextLine();
			String mnt = input.nextLine();

			String mntReport;

			// if it does require maintenance
			if(mnt.equalsIgnoreCase("Y")){
				// add to maintenance requests
				mntReqs.add(b);

				// prompt user for maintenance report
				System.out.print("Please enter maintenance report.");
				input.nextLine();
				mntReport = input.nextLine();
			}

		} else{
			// change bike location to live with customer
			someBike.location = 1;
		}
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
	public static void resolveData() throws IOException, ParseException{
		System.out.println("Enter the file name (including extension) of the file located"
				+ "in data-files: ");
		String dataFile = input.next();
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

			int percentage = (int) (((float) (station.bikes) / station.capacity) * 100);
			totalVehicles = totalVehicles + station.bikes;
			totalCapacity = totalCapacity + station.capacity;

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
					Math.abs(((int) (((float) (station.bikes - 1) / station.capacity) * 100))
							- idealPercentage)){
						if(station.bikes > 0){
							station.bikes = station.bikes - 1;
							extras.set(0, extras.get(0)+1);
						} else {
							extras.set(1, extras.get(1)+1);
						}
						newPercentage = (int) (((float) (station.bikes) / station.capacity) * 100);
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
					Math.abs((int) (((float) (station.bikes + 1) /
							station.capacity) * 100)) - idealPercentage)
					&& Integer.sum(extras.get(0), extras.get(1))>0){

				if(extras.get(0) > 0){
						station.bikes = station.bikes + 1;
						extras.set(0, extras.get(0)-1);
					} else{
						extras.set(1, extras.get(1)-1);
					}

					newPercentage = (int) (((float) (station.bikes) / station.capacity) * 100);
			}
		}
	}
	
	/**
	 * Helper method to validate integer input
	 * @param request - the input being requested
	 * @return the validated integer inputed by user
	 */
	public static Integer getResponse(String request){
		System.out.println(request);
		while (!input.hasNextInt()){
			System.out.println("That is not a valid number");
			System.out.println(request);
			input.next();
		}
		Integer value = input.nextInt();
		return value;
	}

	/**
	 *
	 */
	public static void resolveMntReqs(){
		if(mntReqs != null) {
			// automatically resolve all mnt reqs
			for(int req : mntReqs){
				System.out.println(req +" ");
				Bike bike = bikesMap.get(req);
				bike.setMnt(false);
				bike.setMntReport("");
			}

			mntReqs.clear();
			System.out.println("All maintenance requests have been resolved");
		} else{
			System.out.println("There are no maintenance requests at the moment.");
		}
	}
}