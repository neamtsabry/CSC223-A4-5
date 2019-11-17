import java.io.*;
import java.util.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Class that contains menu options and implementation for Simulator
 */
public class ValleyBikeSim {
	// data structure for keeping track of stations
	public static Map<Integer, Station> stationsMap = new TreeMap<>();

	// data structure for keeping track of bikes
	public static Map<Integer, Bike> bikesMap = new TreeMap<>();

	// list for storing bike ids of bikes that require maintenance
	public static ArrayList<Integer> mntReqs = new ArrayList<>();

	// scanner object to take user's input
	private static Scanner input = new Scanner(System.in);

	/** 
	 * Reads in the stations csv file data and parses it into station objects
	 * mapped by id for easy access and manipulation throughout program
	 * 
	 * Then outputs welcome message and menu selector
	 */
	public static void main(String[] args) throws IOException, ParseException {
		readStationData();

        readBikeData();

		ValleyBikeController.initialMenu();
	}

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
                    Integer.parseInt(values[7]),
                    values[8]);

            // add to the station tree
            stationsMap.put(Integer.parseInt(values[0]),stationOb);
        }

        // close our reader
        bufferedReader.close();
    }

    public static void readBikeData() throws IOException {
        // start reading our designated file
        FileReader fileReader = new FileReader("data-files/bike-data.csv");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        bufferedReader.readLine();

        // initialize string for station data
        String line;

        // while there's more lines to read in the file
        while((line = bufferedReader.readLine()) != null){
            // store comma separated values in string array
            String[] values = line.split(",");

            // start a new station with all the individual values we got
            Bike bikeOb = new Bike(
                    Integer.parseInt(values[1]),
                    Integer.parseInt(values[1]),
                    Integer.parseInt(values[2]),
                    values[3],
                    values[4]);

            // add to the station tree
            bikesMap.put(Integer.parseInt(values[0]), bikeOb);
        }

        // close our reader
        bufferedReader.close();
    }
	
	/**
	 * Iterates through tree map and outputs station data by ID order
	 * in a nicely formatted table
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void viewBikeList() throws IOException, ParseException{
		System.out.format("%-15s%-15s%-15s%-15s%-15s%-20s\n", "ID", "Location", "Station ID",
				"Main. Req", "Main. Report");

		// is there a better/more efficient way to loop through the tree?
		Iterator<Integer> keyIterator1 = bikesMap.keySet().iterator();

		while(keyIterator1.hasNext()){
			Integer key = (Integer) keyIterator1.next();
			Bike bike = bikesMap.get(key);
			System.out.format("%-15d%-15d%-15d%-15d%-15d%-15d%-15b%-20s\n",
					key, bike.id,
					bike.location,
					bike.station,
					bike.mnt,
					bike.mntReport);
		}

	}

    public static void viewStationList() throws IOException, ParseException{
        System.out.format("%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-20s\n", "ID", "Bikes", "Pedelec",
                "AvDocs", "MainReq", "Cap", "Kiosk","Name - Address");

        // is there a better/more efficient way to loop through the tree?
        Iterator<Integer> keyIterator2 = stationsMap.keySet().iterator();

        while(keyIterator2.hasNext()){
            Integer key = (Integer) keyIterator2.next();
            Station station = stationsMap.get(key);
            System.out.format("%-15d%-15d%-15d%-15d%-15d%-15d%-15b%-20s\n",
                    key, station.bikes,
                    station.pedelecs,
                    station.availableDocks,
                    station.maintenanceRequest,
                    station.capacity,
                    station.kioskBoolean,
                    station.name + "-" +station.address);
        }

//        starter();
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
		Integer id = getResponse("station id");

		// handle if the station already exists
		if(stationsMap.get(id) != null){
			// let user know
			System.out.println("Station with this ID already exists. \nWould you like to override "
					+ stationsMap.get(id).name + " with new data? (y/n):");

			// take their input
			String response = input.next();
			if(!response.equalsIgnoreCase("Y")){
				ValleyBikeController.internalAccount();
			}
		}

		System.out.println("Please enter station name: ");
		input.nextLine();
		String name = input.nextLine();

		Integer bikes = getResponse("bikes");

		Integer pedelecs = getResponse("pedelecs");

		Integer availableDocks = getResponse("available docks");

		Integer maintenanceRequest = getResponse("maintenance requests");

		Integer capacity = getResponse("capacity");

		Integer kiosk = getResponse("kiosk");

		System.out.print("Please enter station address: ");
		input.nextLine();
		String address = input.nextLine();

		Station stationOb = new Station(
				name,
				bikes,
				pedelecs,
				availableDocks,
				maintenanceRequest,
				capacity,
				kiosk,
				address);

		stationsMap.put(id, stationOb);
	}

	/**
	 * This method enables maintenance workers to add new bikes
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void addBike() throws IOException, ParseException{
		Integer id = getResponse("Bike id");

		// handle if the bike already exists
		if(bikesMap.get(id) != null){
			System.out.println("Bike with this ID already exists. \nWould you like to override bike "
					+ bikesMap.get(id) + " with new data? (y/n):");
			String response = input.next();

			if(!response.equalsIgnoreCase("Y")){
				ValleyBikeController.internalAccount();
			}
		}

		System.out.println("Does it require maintenance? (y/n): ");
		input.nextLine();
		String mnt = input.nextLine();

		String mntReport;

		if(!mnt.equalsIgnoreCase("Y")){
			mntReport = "none";
		} else {
			System.out.print("Please enter maintenance report.");
			input.nextLine();
			mntReport = input.nextLine();
		}

		// give appropriate choices for bike's location
		System.out.println("Please pick one of the following choices for the " +
				"status of the bike:\n" +
				"0: Docked/available at station\n" +
				"1: Live with customer\n" +
				"2: Docked/out of commission\n");

		Integer bikeLocation = getResponse("0-2");

		Integer stationId = getResponse("station id");

		// make sure it's a station that already exists too
		if(stationsMap.get(stationId) != null){
			System.out.println("Station with this ID already exists. \nWould you like to override "
					+ stationsMap.get(id) + " with new data? (y/n):");
			String response = input.next();

			if(!response.equalsIgnoreCase("Y")){
				ValleyBikeController.internalAccount();
			}
		}

		Bike bikeOb = new Bike(
				id,
				bikeLocation,
				stationId,
				mnt,
				mntReport
		);

		bikesMap.put(id, bikeOb);
	}
	
	/**
	 * Overwrites old station data in csv with updated data from stationMap
	 * 
	 * The choice to overwite all data instead of simply adding new stations
	 * to existing file was made because recording rides can update information
	 * in both old and added stations, and it was important to make sure those updates
	 * were reflected in the new saved list as well
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void saveStationList() throws IOException, ParseException{
		FileWriter stationsWriter = new FileWriter("data-files/station-data.csv");
		Iterator<Integer> keyIterator = stationsMap.keySet().iterator();

		stationsWriter.write("ID,Name,Bikes,Pedelecs,Available Docks,"
				+ "Maintenance Request,Capacity,Kiosk,Address");

		while(keyIterator.hasNext()){
			Integer key = (Integer) keyIterator.next();
			Station station = stationsMap.get(key);
			stationsWriter.write("\n");
			stationsWriter.write(key.toString() + "," + station.getStationString());
		}

		stationsWriter.flush();
		stationsWriter.close();
	}

	public static void saveBikeList() throws IOException, ParseException{
		FileWriter bikesWriter = new FileWriter("data-files/bike-data.csv");
		Iterator<Integer> keyIterator = bikesMap.keySet().iterator();

		bikesWriter.write("ID,Location,StationId,Req_Mnt,Mnt_Report");

		while(keyIterator.hasNext()){
			Integer key = (Integer) keyIterator.next();
			Bike bike = bikesMap.get(key);
			bikesWriter.write("\n");
			bikesWriter.write(key.toString() + "," + bike.getBikeString());
		}

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
	 * Chose to include bikes because new stations with bikes
	 * Could be added in future
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void recordRide() throws IOException, ParseException{
		//TODO get bike ID and validate

		// View stations
		// choose station to rent from
		// view available bike ids at station
		// choose bike to rent
		// confirm? Y/N (timestamps the rent out) --> check date and time now

		System.out.println("Enter b for Bike or p for Pedelec: ");
		String rideType = input.next();

		System.out.println("From (Station ID): ");
		String from = input.next();

		System.out.println("To (Station ID): ");
		String to = input.next();

		try{
			Station stationFrom = stationsMap.get(Integer.parseInt(from));
			Station stationTo = stationsMap.get(Integer.parseInt(to));

			if(stationFrom != null && stationTo != null){
				switch(rideType.toUpperCase()){
					case("B"):
						if (stationFrom.bikes > 0 && stationTo.availableDocks > 0){
							stationFrom.bikes = stationFrom.bikes-1;
							stationTo.bikes = stationTo.bikes+1;
							stationTo.availableDocks = stationTo.availableDocks - 1;
						} else {
							System.out.println("Ride not valid");
						}
						break;
					case("P"):
						if (stationFrom.pedelecs > 0 && stationTo.availableDocks > 0){
							stationFrom.pedelecs = stationFrom.pedelecs-1;
							stationTo.pedelecs = stationTo.pedelecs+1;
							stationTo.availableDocks = stationTo.availableDocks - 1;
						} else {
							System.out.println("Ride not valid");
						}
						break;
					default:
						System.out.println("Invalid vehicle type");
				}
			} else {
				System.out.println("At least one station entered does not exist in our system");
			}
		} catch(NumberFormatException e){
			System.out.println("Your input is invalid! Please input a number for the station ID next time.");
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

			int percentage = (int) (((float) (station.pedelecs + station.bikes) / station.capacity) * 100);
			totalVehicles = totalVehicles + station.pedelecs + station.bikes;
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
					Math.abs(((int) (((float) (station.pedelecs + station.bikes - 1) / station.capacity) * 100)) 
							- idealPercentage)){
						if(station.bikes > 0){
							station.bikes = station.bikes - 1;
							extras.set(0, extras.get(0)+1);
						} else {
							station.pedelecs = station.pedelecs - 1;
							extras.set(1, extras.get(1)+1);
						}
						newPercentage = (int) (((float) (station.pedelecs + station.bikes) / station.capacity) * 100);
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
					Math.abs((int) (((float) (station.pedelecs + station.bikes + 1) /
							station.capacity) * 100)) - idealPercentage)
					&& Integer.sum(extras.get(0), extras.get(1))>0){

				if(extras.get(0) > 0){
						station.bikes = station.bikes + 1;
						extras.set(0, extras.get(0)-1);
					} else{
						station.pedelecs = station.pedelecs + 1;
						extras.set(1, extras.get(1)-1);
					}

					newPercentage = (int) (((float) (station.pedelecs + station.bikes) / station.capacity) * 100);
			}
		}
	}
	
	/**
	 * Helper method to validate integer input for adding stations
	 * @param request - the input being requested
	 * @return the validated integer inputed by user
	 */
	public static Integer getResponse(String request){
		System.out.println("Please enter number of "+ request + ": ");
		while (!input.hasNextInt()){
			System.out.println("That is not a valid number");
			System.out.println("Please enter number of "+ request + ": ");
			input.next();
		}
		Integer value = input.nextInt();
		return value;
	}

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