import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

public abstract class ValleyBikeController {

    private static Scanner input = new Scanner(System.in);

    /**
     * Basic option menu that shows at start of program and when no one is logged in
     */
    static void initialMenu() throws IOException, ParseException {
        //TODO back menu on all menus
        //TODO exit option on all menus
        System.out.print("\n Welcome to ValleyBike Share! \n"
                + "1. Create User Account\n"
                + "2. Log In\n"
                + "0. Exit program\n");
        System.out.println("Please enter your selection (0-2):");

        // if input is not a integer
        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input");
            initialMenu();
        }

        Integer num = input.nextInt();
        input.nextLine();
        switch(num) {
            case 1:
                createAccount();
                break;
            case 2:
                logIn();
                break;
            case 0:
                input.close();

                // save bike and station data
                ValleyBikeSim.saveBikeList();
                ValleyBikeSim.saveStationList();

                System.exit(0);
                break;
        }
    }

    //Do we want a separate method for creating user vs internal accounts? This one is User
    /**
     * Method for a customer to create an account
     */
    public static void createAccount() throws IOException, ParseException {
        //TODO membership types
        //TODO talk to Annika about the flow from model to controller
        String username = enterUsername();
        String password = enterPassword();
        String emailAddress = enterEmail();
        String creditCard = enterCreditCard();
        String membership = enterMembership();

        //create new customer account
        CustomerAccount customerAccount = new CustomerAccount(username, password, emailAddress, creditCard, membership);
        //add customer account to customer account map
        ValleyBikeSim.addCustomerAccount(customerAccount);

        System.out.println("Customer account successfully created!");
        //go to account menu
        String user = customerAccount.getUsername();
        userAccountHome(user);
    }

    private static String enterUsername(){
        System.out.println("Enter username (must be between 6-14 characters):");
        String username = input.nextLine();
        if (!isValidUsername(username)){
            System.out.println("Username is not valid.");
            enterUsername();
        }
        return username;
    }

    private static String enterPassword(){
        System.out.println("Enter password (must be between 6-14 characters):");
        String password = input.nextLine();
        if (!isValidPassword(password)){
            System.out.println("Password is not valid.");
            enterPassword();
        }
        return password;
    }

    private static String enterEmail(){
        // TODO let user know how to make valid email address
        System.out.println("Enter email address:");
        String emailAddress = input.nextLine();
        if (!isValidEmail(emailAddress)){
            System.out.println("Email address is not valid.");
            enterEmail();
        }
        return emailAddress;
    }

    private static String enterCreditCard(){
        System.out.println("Enter credit card number:");
        String creditCard = input.nextLine();
        if (!isValidCreditCard(creditCard)){
            System.out.println("Credit card is not valid.");
            enterCreditCard();
        }
        return creditCard;
    }

    private static String enterMembership(){
        System.out.println("Enter membership type:");
        String membership = input.nextLine();
        return membership;
    }

    private static boolean isValidUsername(String username){
        if (username.length() >= 6 && username.length() <= 14){
            return true;
        }
        return false;
    }

    private static boolean isValidPassword(String password){
        if (password.length() >= 6 && password.length() <= 14){
            return true;
        }
        return false;
    }

    private static boolean isValidCreditCard(String creditCard){
        if(Math.random() <= 0.95) {
            return true;
        }
        return false;
    }

    /**
     * This method checks if the email address input by the user is valid
     * @param emailAddress is the email address input by the user
     * @return returns boolean true if email address is valid and false otherwise
     */
    private static boolean isValidEmail(String emailAddress) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (emailAddress == null)
            return false;
        return pat.matcher(emailAddress).matches();
    }


    public static void logIn() throws IOException, ParseException {
        System.out.println("Press 1 to log in to customer account. \nPress 2 to log in to internal account.");
        int logIn = input.nextInt();
        input.nextLine();
        System.out.println("Enter username:");
        String username = input.nextLine();
        System.out.println("Enter password:");
        String password = input.nextLine();
        switch (logIn){
            case 1:
                ValleyBikeSim.customerLogIn(username, password);
                break;
            case 2:
                ValleyBikeSim.internalLogIn(username, password);
                break;
            default:
                System.out.println("Invalid option chosen.");
                logIn();
        }
    }


    /**
     * Standard menu page for a user after logging in
     * @param username: integer representing unique userID of account
     */
    public static void userAccountHome(String username) throws IOException, ParseException {
        System.out.println("Please choose from one of the following menu options: \n"
                + "1. Edit account info\n"
                + "2. View account balance\n"
                + "3. View station list\n"
                + "4. Rent a bike\n"
                + "5. Return a bike\n"
                + "6. Report a problem\n"
                + "7. Log out \n");

        System.out.println("Please enter your selection (1-6):");

        // if input is not a integer
        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input");
            userAccountHome(username);
        }
        Integer num = input.nextInt();
        switch(num) {
            case 1:
                //edit account info- return to create account or have separate method?
                editAccount(username);
                break;
            case 2:
                //view account balance
                ValleyBikeSim.viewAccountBalance(username);
                break;
            case 3:
                //view station list
                ValleyBikeSim.viewStationList();
                break;
            case 4:
                // user records a ride
                //TODO currently doesnt account for bike id or user id

                recordRide("from", "rent", false, username);
                break;
            case 5:

                //report a problem
                reportProblem(username);
                break;
            case 6:
                //log out, return to homepage
                initialMenu();
                break;
            case 7:
                ValleyBikeSim.viewBikeList();
        }

        //if function call finished and returned to this page, keep calling menu again until log out/exit
        userAccountHome(username);
    }

    private static void editAccount(String username) throws IOException, ParseException {
        //TODO save edited fields
        System.out.println("Press 1 to edit username.\nPress 2 to edit password." +
                "\nPress 3 to edit email address. \nPress 4 to edit credit card number. \nPress 5 to edit membership.");
        int edit = input.nextInt();
        input.nextLine();
        switch (edit){
            case 1:
                enterUsername();
                break;
            case 2:
                enterPassword();
                break;
            case 3:
                enterEmail();
                break;
            case 4:
                enterCreditCard();
                break;
            case 5:
                enterMembership();
                break;
        }
    }

    /**
     * @param: userID- the unique id associated with the user
     * View the account balance associated with a user's account
     */
    private static void viewAccountBalance(String username) {
        ValleyBikeSim.viewAccountBalance(username);
    }

    /**
     * Can be used for both renting and returning bike
     * Prompts the user for info as to achieve those tasks
     *
     * @param dest either "from" or "to"
     * @param action either "return" or "rent"
     * @param isReturned either true or false
     * @throws IOException
     * @throws ParseException
     */
    public static void recordRide(String dest, String action, Boolean isReturned, String username) throws IOException, ParseException{
        // View stations
        System.out.println("Here's a list of station IDs and their names");
        System.out.format("%-10s%-10s\n", "ID", "Name");

        // initiate iterator
        Iterator<Integer> keyIterator = ValleyBikeSim.createIterator(false);

        // while it has a next value
        while(keyIterator.hasNext()){
            Integer key = (Integer) keyIterator.next();
            Station station = ValleyBikeSim.getStationObj(key);
            System.out.format("%-10d%-10s\n", key, station.name);
        }

        // choose station to rent from
        int fromTo = getResponse("Please enter station id to " + action +
                " " + dest + ": ");

        // designated station, whether bike returned to or bike rented from
        Station stationFromTo = ValleyBikeSim.getStationObj(fromTo);

        // keep prompting user until the station obj is not null
        while(stationFromTo == null) {
            System.out.println("The station entered does not exist in our system.");
            fromTo = getResponse("Please enter station id to " + action + " " + dest + ": ");
            stationFromTo = ValleyBikeSim.getStationObj(fromTo);
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
            ValleyBikeSim.equalizeStations();
            System.out.println("All done!");
        }

        // view available bike ids at station
        System.out.println("Here's a list of bike IDs at this station");
        System.out.format("%-10s%-10s\n", "Station", "Bike ID");

        Iterator<Integer> keyIterator2 = ValleyBikeSim.createIterator(true);;

        while(keyIterator2.hasNext()){
            Integer key = (Integer) keyIterator2.next();
            Bike bike = ValleyBikeSim.getBikeObj(key);
            if(fromTo == bike.getStation()) {
                System.out.format("%-10s%-10d\n", fromTo, key);
            }

        }

        // choose bike to rent
        int b = getResponse("bike id");
        Bike someBike = ValleyBikeSim.getBikeObj(b);

        while(someBike == null) {
            System.out.println("The bike ID entered does not exist in our system.");
            b = getResponse("bike id");
            someBike = ValleyBikeSim.getBikeObj(b);
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
                ValleyBikeSim.addToMntRqs(b);

                // prompt user for maintenance report
                System.out.println("Please enter maintenance report.");
                mntReport = input.nextLine();
            }

            // thank user
            System.out.println("You're all done, thank you for using our services!");

            // go back to account home
            userAccountHome(username);
        } else{
            // change bike location to live with customer
            someBike.location = 1;

            // now bike is fully rented
            bikeRented(username, b);
        }
    }

    /**
     * User has bike checked out and can either return bike or report a problem with the bike
     * @param: int userID- the unique id associated with the user
     * @param: bikeID- unique ID associated with the bike that the user has checked out
     */
    private static void bikeRented(String username, int bikeID) throws IOException, ParseException {
        System.out.print("\n Enjoy your bike ride! \n"
                + "1. Return bike"
                + "2. Report a problem");
        System.out.println("Please enter your selection (1-2):");

        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input \n");
            bikeRented(username, bikeID);
        }

        Integer num = input.nextInt();
        switch(num) {
            case 1:
                //return bike
                recordRide("to", "return", true, username);
                break;
            case 2:
                //report a problem
                reportProblem(username);
                break;
            case 0:
                input.close();
                //save all files before exiting
                System.exit(0);
                break;
        }
    }

    /**
     *
     * @param: int userID- the unique id associated with the user
     * @param: bikeID- unique ID associated with the bike that the user has checked out
     *
     */


    /**
     *
     * @param username for user
     * user reports a problem with the bike they checked out
     */
    private static void reportProblem(String username) throws IOException, ParseException {
        ValleyBikeSim.viewStationList();

        // get station id from user
        Integer stationId = getResponse("Please input the bike's station ID:");

        // check station actually exists
        if(ValleyBikeSim.stationsMap.get(stationId) == null){
            System.out.println("Station with this ID doesn't exist. Please reenter ID:\n");
            String response = input.next();
        }

        // get bike id from user
        Integer bikeId = getResponse("Please input the bike's ID:");

        // check station actually exists
        if(ValleyBikeSim.bikesMap.get(bikeId) == null){
            System.out.println("Bike with this ID doesn't exist. Please reenter ID.\n");
            String response = input.next();
        }

        // get user report of the problematic bike
        System.out.println("Please input report of the problem.");
        String report = input.next();

        // get bike object
        Bike bike = ValleyBikeSim.bikesMap.get(bikeId);

        // set the maintenance report and requires maintenance to true
        bike.setMntReport(report);
        bike.setMnt(true);

        // increase maintenance requests for the station
        Station statObj = ValleyBikeSim.stationsMap.get(stationId);
        statObj.maintenanceRequest ++;

        // add to list of bikes that require maintenance
        ValleyBikeSim.mntReqs.add(bikeId);

        System.out.println("Maintenance report has been successfully!");
        System.out.println("Now let's help you return your bike!");

        recordRide("to", "return", true, username);
    }

    //should there be option to add bike/station
    /*
     * Homescreen for internal company employees
     *
     */
    static void internalAccountHome() throws IOException, ParseException {
        System.out.print("\n Choose from the following: \n"
                + "1. View customer balances \n"
                + "2. View customer activity \n"
                + "3. Add new station \n"
                + "4. Add new bike \n"
                + "5. Edit/Resolve maintenance requests \n"
                + "6. Equalize stations \n"
                + "7. Log out \n");
        System.out.println("Please enter your selection (1-5):");

        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input \n");
            internalAccountHome();
        }
        Integer num = input.nextInt();
        switch(num) {
            case 1:
                //TODO view customer balances
                break;
            case 2:
                //TODO view customer activity
                break;
            case 3:
                addStation();
                break;
            case 4:
                addBike();
                break;
            case 5:
                ValleyBikeSim.resolveMntReqs();

                break;
            case 6:
                //equalize stations
                ValleyBikeSim.equalizeStations();
                break;
            case 7:
                //log out
                initialMenu();
                break;
        }
        //if function call finishes and returns to internal account menu
        //call account menu again
        internalAccountHome();
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
        Integer id = getResponse("Please enter the ID for this station:");

        Station station = ValleyBikeSim.getStationObj(id);

        // handle if the station already exists
        while(station != null){
            // let user know
            System.out.println("Station with this ID already exists. \nWould you like to override "
                    + station.name + " with new data? (y/n):");

            // take their input
            String response = input.next();

            // if yes, then take user to the maintenance worker account
            if(response.toLowerCase().equalsIgnoreCase("y")){
                internalAccountHome();
            } else {
                // re-prompt user for station id
                getResponse("Please enter the ID for this station:");
                station = ValleyBikeSim.getStationObj(id);
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
        ValleyBikeSim.addNewStation(id, stationOb);
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

        // get bike associated with id if possible
        Bike bike = ValleyBikeSim.getBikeObj(id);

        // handle if the bike already exists
        if(bike != null){
            System.out.println("Bike with this ID already exists. \nWould you like to override bike "
                    + bike.id + " with new data? (y/n):");
            String response = input.next();

            // if yes, take user back to the internal account home
            // so they edit bike instead
            if(response.equalsIgnoreCase("Y")){
                internalAccountHome();
            }
        }

        // prompt for the station id bike will be located in
        Integer stationId = getResponse("Please enter the ID for the station the bike is located at:");

        Station station = ValleyBikeSim.getStationObj(stationId);

        // check if station doesn't exist
        while(station == null){
            System.out.println("Station with this ID doesn't exist. \nWould you like to add  "
                    + station.name + " as a new station? (y/n):");

            // get yes/no response
            String response = input.next();

            // if yes, then redirect to add station
            if(response.toLowerCase().equalsIgnoreCase("y")){
                internalAccountHome();
            }else {
                // re-prompt user for station id
                getResponse("Please enter the ID for this station:");
                station = ValleyBikeSim.getStationObj(id);
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
            station.maintenanceRequest ++;
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
            station.bikes ++;
            station.availableDocks ++;
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
        ValleyBikeSim.addNewBike(id, bikeOb);
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
}

