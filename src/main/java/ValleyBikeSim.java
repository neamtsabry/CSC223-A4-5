import java.util.Scanner;
import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Date;
import java.util.Iterator;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Class that contains menu options and implementation for Simulator
 */
public class ValleyBikeSim {
	static Map<Integer, Station> stationsMap = new TreeMap<>();
	static Scanner input = new Scanner(System.in);
	
	/** 
	 * Reads in the stations csv file data and parses it into station objects
	 * mapped by id for easy access and manipulation throughout program
	 * 
	 * Then outputs welcome message and menu selector
	 */
	public static void main(String[] args) throws IOException, ParseException {

		FileReader fileReader = new FileReader("data-files/station-data.csv");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String stationLine;
		bufferedReader.readLine();
				
		while((stationLine = bufferedReader.readLine()) != null){
			String[] values = stationLine.split(",");
			Station stationOb = new Station( 
					values[1], Integer.parseInt(values[2]), Integer.parseInt(values[3]), 
					Integer.parseInt(values[4]), 
					Integer.parseInt(values[5]), Integer.parseInt(values[6]), 
					Integer.parseInt(values[7]), values[8]);
			stationsMap.put(Integer.parseInt(values[0]),stationOb);
		}
		
		bufferedReader.close();
		System.out.print("Welcome to the ValleyBike Simulator.");
				selector();
	}
	
	/**
	 * Selector() allows the user to see and choose from menu options
	 * Is called at the successful completion of every method
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void selector() throws IOException, ParseException{
		System.out.print("\nPlease choose from one of the following menu options:\n"
				+ "0. Quit Program.\n"
				+ "1. View station list.\n" 
				+ "2. Add station.\n" 
				+ "3. Save station list.\n"
				+ "4. Record ride.\n"
				+ "5. Resolve ride data.\n"
				+ "6. Equalize stations.\n");
		
		System.out.println("Please enter your selection (0-6):");
		int number = input.nextInt();
		selector(number);
	}
	
	/**
	 * selector(num) takes the menu option entered by the user 
	 * and calls the associated method.
	 *  
	 * @param num - menu option entered by user. 
	 * If num is not a valid selection it will reprint the original selector()
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void selector(int num) throws IOException, ParseException{
		switch(num){
			case 0:
				input.close();
				System.exit(0);
				break;
			case 1:
				viewList();
				break;
			case 2:
				addStation();
				break;
			case 3:
				saveList();
				break;
			case 4:
				recordRide();
				break;
			case 5: 
				resolveData();
				break;
			case 6:
				equalizeStations();
				break;
			default:
				System.out.println("Not a valid selection");
				selector();
			}
	}
	
	/**
	 * Iterates through tree map and outputs station data by ID order
	 * in a nicely formatted table
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void viewList() throws IOException, ParseException{
		System.out.format("%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-20s\n", "ID", "Bikes", "Pedelec", 
				"AvDocs", "MainReq", "Cap", "Kiosk","Name - Address");
		Iterator<Integer> keyIterator = stationsMap.keySet().iterator();
		while(keyIterator.hasNext()){
			Integer key = (Integer) keyIterator.next();
			Station station = stationsMap.get(key);
			System.out.format("%-15d%-15d%-15d%-15d%-15d%-15d%-15b%-20s\n",
					key, station.bikes, station.pedelecs,
					station.availableDocks, station.maintainenceRequest,
					station.capacity, station.kioskBoolean, 
					station.name + "-" +station.address);
		}
		selector();
	}
	
	/**
	 * Prompts user for all station data and then creates a new station
	 * object which is added to the stationMap
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void addStation() throws IOException, ParseException{
		
		Integer id = getResponse("station id");
		
		if(stationsMap.get(id) != null){
			System.out.println("Station with this ID already exists. \nWould you like to override "
					+ stationsMap.get(id).name + " with new data? (y/n):");
			String response = input.next();
			if(!response.equalsIgnoreCase("Y")){
				selector();
			}
		}
		
		System.out.println("Please enter station name: ");
		input.nextLine();
		String name = input.nextLine();
		
		Integer bikes = getResponse("bikes");
		
		Integer pedelecs = getResponse("pedelecs");
		
		Integer availableDocks = getResponse("available docks");
		
		Integer maintainenceRequest = getResponse("maintainence requests");
		
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
				maintainenceRequest,
				capacity,
				kiosk,
				address);
		
		stationsMap.put(id, stationOb);
		selector();
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
	public static void saveList() throws IOException, ParseException{
		FileWriter stationsWriter = new FileWriter("data-files/station-data.csv");
		Iterator<Integer> keyIterator = stationsMap.keySet().iterator();
		stationsWriter.write("ID,Name,Bikes,Pedelecs,Available Docks,"
				+ "Maintainence Request,Capacity,Kiosk,Address");
		while(keyIterator.hasNext()){
			Integer key = (Integer) keyIterator.next();
			Station station = stationsMap.get(key);
			stationsWriter.write("\n");
			stationsWriter.write(key.toString() + "," + station.getStationString());
		}
		stationsWriter.flush();
		stationsWriter.close();
		selector();
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
		System.out.println("Enter b for Bike or p for Pedelec: ");
		String rideType = input.next();
		System.out.println("From (Station ID): ");
		String from = input.next();
		System.out.println("To (Station ID): ");
		String to = input.next();
		
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
		selector();
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
		//Scanner input = new Scanner(System.in);
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
		selector();
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
		
		selector();	
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
	 * @param extraBikes - Bikes taken from high percentage stations
	 * @param extraPedelecs - Pedelecs taken from high percentage stations
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
			//continues to add vehicles as long as adding a vehicle
			//moves the percentage closer to ideal percentage
			//and there are still extra vehicles to add
			while((Math.abs(newPercentage - idealPercentage) > 
			Math.abs((int) (((float) (station.pedelecs + station.bikes + 1) / station.capacity) * 100)) 
					- idealPercentage)
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
	private static Integer getResponse(String request){
		System.out.println("Please enter number of "+ request + ": ");
		while (!input.hasNextInt()){
			System.out.println("That is not a number");
			System.out.println("Please enter number of "+ request + ": ");
			input.next();
		}
		Integer value = input.nextInt();
		return value;
	}
}