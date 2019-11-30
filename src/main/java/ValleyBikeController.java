import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.*;
import java.util.regex.Pattern;

public abstract class ValleyBikeController {

    /** initialize input of type scanner */
    private static Scanner input = new Scanner(System.in);

    /**
     * Basic option menu that shows at start of program and when no one is logged in
     * Allows user to create a new account or log in or exit
     *
     * @throws IOException create account, log in, save bike list and save station list methods throw IOException
     * @throws ParseException create account, log in, save bike list and save station list methods throw ParseException
     */
    static void initialMenu() throws IOException, ParseException {
        //TODO back menu on all menus
        //TODO exit option on all menus

        //check whether it's time to renew customer's memberships
        ValleyBikeSim.checkMembershipRenewal();

        System.out.print("\n Welcome to ValleyBike Share! \n"
                + "1. Create Customer Account (Partially works) \n"
                + "2. Log In\n"
                + "0. Exit program\n");
        //prompt the user to pick an int option
        int num = getResponse("Please enter your selection (0-2):");
        input.nextLine();

        switch(num) {
            case 1:
                //create a new customer account
<<<<<<< HEAD
                createAccount();
                // rentBike("aliciagrubb");
                // System.out.println("---------------------------");
                // returnBike("aliciagrubb");
=======
                // createAccount();
                addStation();
>>>>>>> fa5b52d071fa2fe84f3bc9ecb19bfc5fee733cb1
                break;
            case 2:
                //log in to existing customer or internal account
                logIn();
                break;
            case 0:
                //exit program
                input.close();

                //TODO save customer account list and internal account list

                // save bike and station data
                ValleyBikeSim.saveBikeList();
                ValleyBikeSim.saveStationList();

                System.exit(0);
                break;
            default:
                System.out.println("That is an invalid option. Please try again.");
                initialMenu();
        }
    }

    /**
     * Gets all the required field information from the user to create a new customer account object
     * Calls method to add the new customer account to customer account map
     * Leads to customer account home after successful log in
     *
     * @throws IOException add customer account and user account home methods throw IOException
     * @throws ParseException add customer account and user account home methods throw ParseException
     */
    static void createAccount() throws IOException, ParseException {
        //Assumption: a new internal account cannot be created by a user who is not logged into an internal account
        //i.e. only internal staff can create new internal accounts

        //TODO separate create customer account and create internal account method and implement them in the correct places
        //TODO Check if username already exists right away

        //each field has its own method which calls itself until a valid input is entered
        String username = enterUsername();
        String password = enterPassword();
        String emailAddress = enterEmail();
        String creditCard = enterCreditCard();
        int membership = enterMembership();

        //once all the required fields have been input by the user, create new customer account
        //Assumption: initially the balance in customer account is always 0
        ValleyBikeSim.createCustomerAccount(username, password, emailAddress, creditCard, membership);

        //Let the user know the account has been successfully created
        System.out.println("Customer account successfully created!");

        customerAccountHome(username);
    }

    /**
     * This is the log in menu that allows the user to log in to either customer or internal account
     *
     * @throws IOException customer log in and internal log in methods in model throw IOException
     * @throws ParseException customer log in and internal log in methods in model throw ParseException
     */
    static void logIn() throws IOException, ParseException {
        //prompt the user to choose which kind of account they want to log into
        int logIn = getResponse("Press 1 to log in to customer account. \nPress 2 to log in to internal account. \nPress 0 to log out.");
        input.nextLine();

        //if user wants to log out take them to initial menu to log out
        if (logIn == 0){
            initialMenu();
        }

        //this is not inside the switch case because if it is in the switch case,
        //it informs the user that they picked an invalid option after asking for username and password
        if (logIn != 1 && logIn != 2){
            //if they did not choose either 1 or 2
            //make them choose again by recursively calling log in
            System.out.println("Invalid option chosen.");
            logIn();
        }

        //prompt the user to input their username and password
        String username = enterUsername();
        String password = enterPassword();

        switch (logIn){
            case 1:
                //if they want to log in to customer account
                //validate their username and password in the customer account map
                ValleyBikeSim.customerLogIn(username, password);
                break;
            case 2:
                //if they want to log in to internal account
                //validate their username and password in the internal account map
                ValleyBikeSim.internalLogIn(username, password);
                break;
        }
    }


    /**
     * Standard menu page for a customer account after logging in
     *
     * @param username unique username associated with the customer account
     *
     * @throws IOException editCustomerAccount, viewStationList, recordRide, reportProblem, initialMenu, viewBikeList throw IOException
     * @throws ParseException editCustomerAccount, viewStationList, recordRide, reportProblem, initialMenu, viewBikeList throw ParseException
     */
    public static void customerAccountHome(String username) throws IOException, ParseException {

        //checks whether user has a rental, and if so whether it exceeds 24 hours
        Boolean isReturned = ValleyBikeSim.checkBikeRented(username);

        //menu option for customer account home
        System.out.println("Please choose from one of the following menu options: \n"
                + "1. Edit account info (Partially works) \n"
                + "2. View account balance\n"
                + "3. View station list\n");

        if (customer.getIsReturned()) { System.out.println("4. Rent a bike\n"); }
        else { System.out.println("4. Return bike\n"); }

        System.out.println("5. Report a problem\n"
                + "6. Log out \n");

        System.out.println("Please enter your selection (1-6):");

        // if input is not a integer
        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input");
            customerAccountHome(username);
        }

        int num = input.nextInt();
        switch(num) {
            case 1:
                //edit account info
                editCustomerAccount(username);
                break;
            case 2:
                //view account balance
                System.out.println("Your account balance is "+ ValleyBikeSim.viewAccountBalance(username));
                break;
            case 3:
                //view station list
                ValleyBikeSim.viewStationList();
                break;
            case 4:
                // if customer has no ongoing rentals, help user rent a bike
                if (customer.getIsReturned()) { rentBike(username); }
                else { returnBike(username); } // if customer has ongoing rental, help user return bike
                //TODO save data after renting and returning
                break;
                /*
            case 5:
                // return bike
                if(isReturned){
                    System.out.println("You have no rented bikes to return.");
                } else{
                    returnBike(username);
                }
                 */
            case 6:
                //TODO report a problem
                break;
            case 7:
                //return to homepage to log out
                //TODO save data when logging out
                initialMenu();
                break;
            default:
                customerAccountHome(username);
                break;
        }

        //if function call finished and returned to this page, keep calling menu again until log out/exit
        customerAccountHome(username);
    }

    /**
     * Menu page for editing customer account information
     *
     * @param username is the unique username associated with the customer account
     */
    private static void editCustomerAccount(String username){
        //TODO save edited fields
        //TODO add a return to customer home option
        //TODO recursively call itself to edit multiple fields
        //TODO handle edge case of not entering int
        //prompt user to choose which field they want to edit
        System.out.println("Press 1 to edit username.\nPress 2 to edit password." +
                "\nPress 3 to edit email address. \nPress 4 to edit credit card number. \nPress 5 to edit membership.");
        int edit = input.nextInt();
        input.nextLine();
        switch (edit){
            case 1:
                //edit username
                enterUsername();
                break;
            case 2:
                //edit password
                enterPassword();
                break;
            case 3:
                //edit email address
                enterEmail();
                break;
            case 4:
                //edit credit card number
                enterCreditCard();
                break;
            case 5:
                //edit membership type
                enterMembership();
                break;
        }
    }

    private static UUID lastRideId;

    /**
     * Can be used for both renting and returning bike
     * Prompts the user for info as to achieve those tasks
     *
     * @throws IOException
     * @throws ParseException
     */
    public static void rentBike(String username) throws IOException, ParseException{

        //check membership, and if pay-as-you-go make sure credit card is still valid before continuing
        int membership = ValleyBikeSim.viewMembershipType(username).getMembershipInt();
        if (membership == 1) {
            String creditCard = ValleyBikeSim.viewCreditCard(username);
            //check validity of credit card, send them back to home screen if not valid
            if (!isValidCreditCard(creditCard)) {
                System.out.println("Sorry, your credit card is not valid. Please make sure the credit card saved" +
                        " in your account is correct, then try again.");
                customerAccountHome(username);
            }
        } //if there is no problem, continue with rental

        // View stations
        System.out.println("Here's a list of station IDs and their names");
        ValleyBikeSim.viewStationList(); // view station list

        // choose station to rent from
        int statId = getResponse("Please pick a station from the above list to rent a bike from. " +
                " Enter the station ID ('11'): ");

        // Validate user input for station ID
        // keep prompting user until input matches the ID of an available station
        Station stationFrom = ValleyBikeSim.getStationObj(statId); // get station obj (or null) from input
        // if station doesn't exist or doesn't have any bikes
        while((stationFrom == null)||(Objects.equals(stationFrom.getBikes(), 0))) {
            if (stationFrom == null) {
            System.out.println("The station ID entered does not exist in our system.");}
            else if (Objects.equals(stationFrom.getBikes(), 0)) {
                System.out.println("The station entered does not have any bikes.");
            }
            ValleyBikeSim.viewStationList();
            statId = getResponse("Please pick a station from list shown above " +
                    "to rent a bike from");
<<<<<<< HEAD
            stationFrom = ValleyBikeSim.getStationObj(statId);}
=======
            stationFrom = ValleyBikeSim.getStationObj(statId);
        }

        //TODO call the maintenance worker after theyve rented the bike instead of before
        // because they could decide not to rent a bike after choosing station, before choosing bike

        // if there's more than one bike at station
        if (stationFrom.getBikes() > 1){
            // station now has one less bike
            stationFrom.setBikes(stationFrom.getBikes()-1);
            // and one more available dock
            stationFrom.setAvailableDocks(stationFrom.getAvailableDocks()+1);
        } else {
            // if there's less, notify maintenance worker to resolve data
            System.out.println("Station is almost empty!");
            System.out.println("Notifying maintenance worker to resolve this...");
            // doesn't work
            ValleyBikeSim.equalizeStations();
            System.out.println("All done!");
        }
>>>>>>> fa5b52d071fa2fe84f3bc9ecb19bfc5fee733cb1

        // View available bike ids at station
        System.out.println("Here's a list of bike IDs at this station");
        System.out.format("%-10s%-10s\n", "Station", "Bike ID");

        // Get list iterator of bikes at station
        LinkedList<Bike> bikeList = stationFrom.getBikeList();
        ListIterator<Bike> bikesAtStation = bikeList.listIterator();

        // Print bikes at station
        while(bikesAtStation.hasNext()){
            Bike bike = bikesAtStation.next();
            System.out.format("%-10s%-10d\n", statId, bike.getId());
        }

        // Choose bike to rent
        int bikeID = getResponse("Please enter the ID number of the bike you" +
                " would like to rent ('11'): ");
        Bike someBike = ValleyBikeSim.getBikeObj(bikeID); // get bike object or null from bike ID

        while (!bikeList.contains(someBike)){
            System.out.println("The bike ID entered is not at this station.");
            bikeID = getResponse("Please enter the ID number of the bike you" +
                    " would like to rent ('11'): ");
            someBike = ValleyBikeSim.getBikeObj(bikeID);
        }

        someBike.moveStation(0); // move bike to station '0' <- our "non-station" ID

        // time stamp recorded
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        String formattedDate = sdf.format(date);

        lastRideId = UUID.randomUUID();

        // create new ride object
        Ride ride = new Ride(lastRideId,
                bikeID,
                username,
                false);

        ride.setStartTimeStamp(formattedDate);

        // Add ride to customer account
        // assume username is always valid
        CustomerAccount customer = ValleyBikeSim.getCustomerObj(username); // get customer account object
        customer.addNewRide(lastRideId); // add ride to customer account
        customer.setIsReturned(false); // set customer account to show a bike has yet to be returned

        ValleyBikeSim.addToRideMap(lastRideId, ride);

        // now bike is fully rented
        // bikeRented(username, b, ride.getRideId());
        System.out.println("You have rented bike #" + bikeID + " from station #" + statId + ". Enjoy your ride!");
        System.out.println();

        // equalize stations if there's one bike or less left at station after bike is rented
        // notify maintenance worker to redistribute bikes
        // right now this work is automated by the equalizeStations() function
        if (stationFrom.getBikes() <= 1){
            System.out.println("This station is almost empty!");
            System.out.println("Notifying maintenance worker to resolve this...");
            ValleyBikeSim.equalizeStations();
            System.out.println("The bikes have now been redistributed between the stations.");
            System.out.println();
        }
    }

    /**
     * User has bike checked out and can either return bike or report a problem with the bike
     * @param: int userID- the unique id associated with the user
     * @param: bikeID- unique ID associated with the bike that the user has checked out
     */
//    private static void bikeRented(String username, int bikeID, int rideID) throws IOException, ParseException {
//        // view options to either return bike or report problem
//        System.out.println("Hope you enjoyed your bike ride! \n"
//                + "1. Return bike\n"
//                + "2. Report a problem\n");
//        // get response
//        int num = getResponse("Please enter your selection (1-2):");
//
//        switch(num) {
//            case 1:
//                //return bike
//                returnBike(username, bikeID, rideID);
//                break;
//            case 2:
//                //report a problem
//                reportProblem(username, bikeID);
//                break;
//            default:
//                bikeRented(username, bikeID, rideID);
//                break;
//        }
//    }

    /**
     * Prompts user for ride enough to return a bike
     *
     * @throws IOException
     * @throws ParseException
     * @param username for user
     */
    public static void returnBike(String username) throws IOException, ParseException{
        Ride rideObj = ValleyBikeSim.getRideObj(lastRideId);

        System.out.println("Here's a list of station IDs and their names");

        // view station list
        ValleyBikeSim.viewStationList();

        // choose station to rent from
        int statId = getResponse("Please enter station you're returning the " +
                "bike to");

        // designated station, whether bike returned to or bike rented from
        Station stationTo = ValleyBikeSim.getStationObj(statId);

        // keep prompting user until the station obj is not null
        while(stationTo == null) {
            System.out.println("The station entered does not exist in our system.");
            statId = getResponse("Please enter the ID of the station to which " +
                    "you're returning the bike ('11'): ");
            stationTo = ValleyBikeSim.getStationObj(statId);
        }

<<<<<<< HEAD
=======
        //TODO only call maintenance worker when bike is finished being returned

        // if there's more than one bike at station
        if (stationTo.getBikes() > 1){
            // station now has one less bike
            stationTo.setBikes(stationTo.getBikes()+1);
            // and one more available dock
            stationTo.setAvailableDocks(stationTo.getAvailableDocks()+1);
        } else {
            // if there's less, notify maintenance worker to resolve data
            System.out.println("Station is almost empty!");
            System.out.println("Notifying maintenance worker to resolve this...");
            ValleyBikeSim.equalizeStations();
            System.out.println("All done!");
        }
>>>>>>> fa5b52d071fa2fe84f3bc9ecb19bfc5fee733cb1

        int bikeId = rideObj.getBikeId();

        // get rented bike
        Bike someBike = ValleyBikeSim.getBikeObj(bikeId);

        // move bike to new station
        someBike.moveStation(statId);

        // time stamp recorded
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        String formattedDate = sdf.format(date);

        // set ride to be returned
        rideObj.setIsReturned(true);
        rideObj.setEndTimeStamp(formattedDate);

        // set the same in customer account
        CustomerAccount customer = ValleyBikeSim.getCustomerObj(username);
        customer.setIsReturned(true);

<<<<<<< HEAD
        System.out.println("Bike #" + bikeId + " has been returned to station #" + statId + ". Thank you!");
        System.out.println();

        if (stationTo.getAvailableDocks() <= 1){
            // if there's 1 available docks or less at station after bike is returned
            // notify maintenance worker to redistribute bikes
            // right now this work is automated by the equalizeStations() function
            System.out.println("Station is almost full!");
            System.out.println("Notifying maintenance worker to resolve this...");
            ValleyBikeSim.equalizeStations();
            System.out.println("The bikes have now been redistributed between the stations.");
            System.out.println();
        }
=======
        //check membership to determine how to charge for rental
        int membership = ValleyBikeSim.viewMembershipType(username).getMembershipInt();
        int ridesLeft = ValleyBikeSim.viewMembershipType(username).getTotalRidesLeft();
        //if pay-as-you-go or no rides remaining on membership, charge by time
        if (membership == 1 || ridesLeft == 0) {
            long rideLength = rideObj.getRideLength();
            long paymentDue = rideLength * (long) .30;
            double balance = ValleyBikeSim.viewAccountBalance(username) + paymentDue;
            rideObj.setPayment(paymentDue);
            //TODO update user balance using balance
        } else {
            ValleyBikeSim.viewMembershipType(username).setTotalRidesLeft(ridesLeft - 1);
            //otherwise merely decrement rides remaining in membership
        }

        System.out.println("You're all done! Thank you for returning this bike.");
>>>>>>> fa5b52d071fa2fe84f3bc9ecb19bfc5fee733cb1

        //TODO remember to save

        // take user back to their account home
        customerAccountHome(username);
    }


    /**
     * Report problem for regular user by adding bike's id to
     * maintenance request list and setting its fields to requiring
     * maintenance.
     *
     * @param username for user
     * user reports a problem with the bike they checked out
     */
    private static void reportProblem(String username) throws IOException, ParseException {
        // prompt user for maintenance report
        System.out.println("Please enter maintenance report.");
        input.nextLine();
        String mntReport = input.nextLine();

        int bikeId = getResponse("Please enter the id of the bike you" +
                " would like to rent.");

        // add to maintenance requests
        ValleyBikeSim.addToMntRqs(bikeId, mntReport);


        // get bike object
        Bike bike = ValleyBikeSim.getBikeObj(bikeId);

        // set bike's maintenance report and maintenance to true
        bike.setMnt(true);
        bike.setMntReport(mntReport);

        // bike is now out of commission until fixed
        bike.setBikeLocation(1);

        // increase maintenance requests for the station
        Station statObj = ValleyBikeSim.getStationObj(bike.getStation());
        statObj.setMaintenanceRequest(statObj.getMaintenanceRequest()+1);

        // let user know the process is done
        System.out.println("Maintenance report has been successfully filed!");
        System.out.println("Now let's help you return your bike!");

        //TODO save rent data after done

        // now return bike
        // returnBike(username, bikeId);
    }

    /**
     * Main menu for internal accounts
     *
     * @throws IOException addStation, addBike, equalizeStations and initialMenu throw IOException
     * @throws ParseException addStation, addBike, equalizeStations and initialMenu throw ParseException
     */
    static void internalAccountHome(String username) throws IOException, ParseException {
        //prompt user to pick option from main internal menu

        System.out.print("\n Choose from the following: \n"
                + "1. Create new internal account \n"
                + "2. Edit account information \n"
                + "3. View customer balances \n"
                + "4. View customer activity \n"
                + "5. Add new station \n"
                + "6. Add new bike \n"
                + "7. View station list \n"
                + "8. View bike list \n"
                + "9. Edit/Resolve maintenance requests \n"
                + "10. Equalize stations (Coming Soon!) \n"
                + "0. Log out \n");
        System.out.println("Please enter your selection (1-9):");

        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input \n");
            internalAccountHome(username);
        }
        int num = input.nextInt();
        switch(num) {
            case 1:
                //TODO create new internal account
                break;
            case 2:
                //TODO edit internal account
                break;
            case 3:
                //TODO view customer balances
                break;
            case 4:
                //TODO view customer activity
                break;
            case 5:
                //add station to station list
                addStation();
                break;
            case 6:
                //add bike to bike list
                addBike();
                break;
            case 7:
                ValleyBikeSim.viewStationList();
                break;
            case 8:
                ValleyBikeSim.viewBikeList();
                break;
            case 9:
                // resolve maintenance requests
                ValleyBikeSim.resolveMntReqs();
                break;
            case 10:
                //equalize stations
                ValleyBikeSim.equalizeStations();
                break;
            case 0:
                //go to initial menu to log out
                initialMenu();
                break;
        }
        //if function call finishes and returns to internal account menu
        //call account menu again
        internalAccountHome(username);
    }

    /**
     * Prompts user for all station data and then creates a new station
     * object which is added to the stationMap
     *
     * @throws IOException
     * @throws ParseException
     */
    private static void addStation() throws IOException, ParseException{
        // use helper function to check input is valid and save it
        int id = getResponse("Please enter the ID for this station:");

        // handle if the station already exists
        while(ValleyBikeSim.stationsMapContains(id)){
            // let user know
            System.out.println("Station with this ID already exists.");

            // re-prompt user for station id
            id = getResponse("Please re-enter the ID for this station:");
        }

        // prompt user for station name
        System.out.println("Please enter station name: ");
        input.nextLine();
        String name = input.nextLine();

        // assume new station starts off with no maintenance requests
        Integer maintenanceRequest = 0;

        // prompt capacity for station
        Integer capacity = getResponse("What is the station's capacity?");

        // number of kiosks
        Integer kiosk = getResponse("How many kiosks?");

        // prompt for the station's address
        System.out.println("Please enter station address: ");
        input.nextLine();
        String address = input.nextLine();

<<<<<<< HEAD
        // create new station object with received data from user
        Station stationOb = new Station(
                name,
                maintenanceRequest,
                capacity,
                kiosk,
                address);

        // add to the station tree
        ValleyBikeSim.addNewStation(id, stationOb);

        System.out.println("Station has been added!");
=======
        // confirmation
        System.out.println("Are you sure you want to add a station with the info entered?(y/n)");
        String confirm = input.nextLine();

        switch(confirm.toLowerCase()){
            case "y":
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
                ValleyBikeSim.addNewStation(id, stationOb);

                System.out.println("Station has been added!");
                break;
            case"n":
                System.out.println("The station was not added, taking you back to your account home...");
                break;
            default:
                System.out.println("You have to answer with y (yes) or n (no)");
                break;
        }
>>>>>>> fa5b52d071fa2fe84f3bc9ecb19bfc5fee733cb1
    }

    /**
     * This method enables maintenance workers to add new bikes
     *
     * @throws IOException
     * @throws ParseException
     */
    private static void addBike() throws IOException, ParseException{
        // get new bike's id
        int id = getResponse("Please enter the bike's ID");

        // if the bike already exists
        while(ValleyBikeSim.bikesMapContains(id)){
            // ask if user wants to overwrite bike
            System.out.println("Bike with this ID already exists.");

            // prompt user to re-enter bike id
            id = getResponse("Please re-enter the bike's ID");
        }

        // View stations
        System.out.println("Here's a list of stations and their info.");

        // view station list
        ValleyBikeSim.viewStationList();

        // prompt for the station id bike will be located in
        int stationId = getResponse("Please enter the ID for the station the bike is located at:");

        // check if station doesn't exist
        while(ValleyBikeSim.getStationObj(stationId) == null){
            // let user know and prompt them to reenter the id
            System.out.println("Station with this ID doesn't exist");
            stationId = getResponse("Please re-enter the ID for this station:");
        }

        Station station = ValleyBikeSim.getStationObj(stationId);

        // prompt user if it requires maintenance
//        System.out.println("Does it require maintenance? (y/n): ");
//        input.nextLine();

        // assume bike starts off as not needing maintenance
        String mnt = "n";

        // initiate maintenance report string
        String mntReport = " ";

        // if it does require maintenance
//        if(mnt.toLowerCase().equalsIgnoreCase("y")){
//            // prompt user to enter maintenance report
//            System.out.println("Please enter maintenance report:");
//            input.nextLine();
//            mntReport = input.nextLine();
//
//            // this increases the bike's station maintenance requests
//            station.setMaintenanceRequest(station.getMaintenanceRequest()+1);
//
//        }

        // give appropriate choices for bike's location
//        System.out.println("Please pick one of the following choices for the " +
//                "status of the bike:\n" +
//                "0: Docked/available at station\n" +
//                "2: Docked/out of commission\n");

        // assume bike starts off as available
        int bikeLocation = 0;

        // while the answer is not between the options, keep prompting user
//        while(!(bikeLocation == 0 | bikeLocation == 2)){
//            System.out.println("Your answer has to be either 0 or 2");
//
//            // give appropriate choices for bike's location
//            System.out.println("Please pick one of the following choices for the " +
//                    "status of the bike:\n" +
//                    "0: Docked/available at station\n" +
//                    "1: Docked/out of commission\n");
//            bikeLocation = getResponse("Please enter one of the above options:");
//        }



        // create new bike object based on user's inputs
        Bike bikeOb = new Bike(
                id,
                bikeLocation,
                stationId,
                mnt,
                mntReport
        );
        station.addToBikeList(bikeOb);
        // add to bike tree structure
        ValleyBikeSim.addNewBike(id, bikeOb);
    }

    /**
     * Helper method to validate user integer input
     *
     * @param request - the input being requested
     * @return the validated integer inputted by user
     */
    private static Integer getResponse(String request){
        System.out.println(request);
        while (!input.hasNextInt()){
            System.out.println("That is not a valid number. Please try again.");
            System.out.println(request);
            input.next();
        }
        Integer value = input.nextInt();
        return value;
    }

    /**
     * Helper method to validate input is between two values
     *
     * @param a first value
     * @param b second value
     * @param request what should be asked of the user
     * @return return the validated input
     */
    public static Integer getResponseBetween(int a, int b, String request){
        int num = getResponse(request);
        while(num < a || num > b){
            System.out.println("You have to enter an option between " + a + " and " + b);
            num = getResponse(request);
        }

        return num;
    }

    /**
     * We are not currently using this method.
     * Calls ValleyBikeSim's resolveData method to resolve ride data
     *
     * @throws IOException
     * @throws ParseException
     */
    public void resolveData() throws IOException, ParseException {
        String dataFile = input.next();
        ValleyBikeSim.resolveData(dataFile);
    }

    /**
     * Prompts user to input username
     * Validates if username is between 6-14 characters
     * Recursively calls itself until valid username input by user
     *
     * @return valid username input by user
     */
    private static String enterUsername(){
        //prompts user to input username
        System.out.println("Enter username (must be between 6-14 characters):");
        String username = input.nextLine();

        //validates if username is between 6-24 characters
        if (!isValidUsername(username)){

            //recursively calls itself until valid username input by user
            System.out.println("Username is not valid.");
            enterUsername();
        }

        //return valid username input by user
        return username;
    }

    /**
     * Prompts user to input password
     * Validates if password is between 6-14 characters
     * Recursively calls itself until valid password input by user
     *
     * @return valid password input by user
     */
    private static String enterPassword(){
        //prompts user to input password
        System.out.println("Enter password (must be between 6-14 characters):");
        String password = input.nextLine();

        //validates if password is between 6-24 characters
        if (!isValidPassword(password)){

            //recursively calls itself until valid password input by user
            System.out.println("Password is not valid.");
            enterPassword();
        }

        //return valid password input by user
        return password;
    }

    /**
     * Prompts user to input email address
     * Validates if email address is in correct format
     * Recursively calls itself until valid email address input by user
     *
     * @return valid email address input by user
     */
    private static String enterEmail(){
        // TODO let user know how to make valid email address
        //prompts user to input email address
        System.out.println("Enter email address:");
        String emailAddress = input.nextLine();

        //validates if email address is in correct format
        if (!isValidEmail(emailAddress)){

            //recursively calls itself until valid email address input by user
            System.out.println("Email address is not valid.");
            enterEmail();
        }

        //return valid email address input by user
        return emailAddress;
    }

    /**
     * Prompts user to input credit card
     * Validates if credit card is correct
     * Recursively calls itself until valid email address input by user
     *
     * @return valid credit card input by user
     */
    private static String enterCreditCard(){
        //prompts user to input email address
        System.out.println("Enter credit card number:");
        String creditCard = input.nextLine();

        //validates if credit card is correct
        if (!isValidCreditCard(creditCard)){

            //recursively calls itself until valid credit card input by user
            System.out.println("Credit card is not valid.");
            enterCreditCard();
        }

        //return valid credit card input by user
        return creditCard;
    }

    /**
     * Prompts user to input membership
     *
     * @return membership string input by user
     */
    private static int enterMembership(){
        //TODO membership needs to be choose an option between monthly, yearly, pay-as-you-go
        System.out.println("Choose membership type: \n" +
                "1. Pay-as-you-go Membership \n" +
                "2. Monthly Membership \n" +
                "3. Yearly Membership");
        //prompt the user to pick an int option
        int num = getResponse("Please enter your selection (1-3):");
        input.nextLine();
        return num;
    }

    /**
     * Validates if username is between 6 and 14 characters
     *
     * @param username is the username input by the user
     *
     * @return true if username is valid and false otherwise
     */
    private static boolean isValidUsername(String username){
        return username.length() >= 6 && username.length() <= 14;
    }

    /**
     * Validates if password is between 6 and 14 characters
     *
     * @param password is the password input by the user
     *
     * @return true if password is valid and false otherwise
     */
    private static boolean isValidPassword(String password){
        return password.length() >= 6 && password.length() <= 14;
    }

    /**
     * Validates credit card number input by user
     *
     * @param creditCard is the credit card number input by user
     *
     * @return true if credit card is valid and false otherwise
     */
    static boolean isValidCreditCard(String creditCard){
        //TODO check credit card validity for every transaction

        //90% of the time the method accepts the credit card
        //10% of the time the method rejects the credit card
        //This method makes a random decision and is not a real credit card validator
        return Math.random() <= 0.95;
    }

    /**
     * Checks if the email address input by the user is valid
     *
     * @param emailAddress is the email address input by the user
     *
     * @return true if email address is valid and false otherwise
     */
    private static boolean isValidEmail(String emailAddress) {
        //regular expression to check format of the email address string input by user
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);

        //if the string is null return false
        if (emailAddress == null) return false;

        //if the email address entered by user matches the pattern from the regex
        //then return true, else return false
        return pat.matcher(emailAddress).matches();
    }

    //TODO should we make an isValidStation and isValidBike too?

}
