import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
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
        System.out.print("\n Welcome to ValleyBike Share! \n"
                + "1. Create Customer Account\n"
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
                //create a new customer account
                createAccount();
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
    public static void createAccount() throws IOException, ParseException {
        //Assumption: a new internal account cannot be created by a user who is not logged into an internal account
        //i.e. only internal staff can create new internal accounts

        //TODO separate create customer account and create internal account method and implement them in the correct places
        //TODO membership types

        //each field has its own method which calls itself until a valid input is entered
        String username = enterUsername();
        String password = enterPassword();
        String emailAddress = enterEmail();
        String creditCard = enterCreditCard();
        String membership = enterMembership();

        //once all the required fields have been input by the user, create new customer account
        //Assumption: initially the balance in customer account is always 0
        CustomerAccount customerAccount = new CustomerAccount(username, password, emailAddress, creditCard, membership);

        //add customer account to customer account map
        ValleyBikeSim.addCustomerAccount(customerAccount);

        //Let the user know the account has been successfully created
        System.out.println("Customer account successfully created!");

        //go to account menu
        String user = customerAccount.getUsername();
        customerAccountHome(user);
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
    private static String enterMembership(){
        //TODO membership needs to be choose an option between monthly, yearly, pay-as-you-go
        System.out.println("Enter membership type:");
        String membership = input.nextLine();
        return membership;
    }

    /**
     * Validates if username is between 6 and 14 characters
     *
     * @param username is the username input by the user
     *
     * @return true if username is valid and false otherwise
     */
    private static boolean isValidUsername(String username){
        if (username.length() >= 6 && username.length() <= 14){
            //if valid username return true
            return true;
        }
        //else return false
        return false;
    }

    /**
     * Validates if password is between 6 and 14 characters
     *
     * @param password is the password input by the user
     *
     * @return true if password is valid and false otherwise
     */
    private static boolean isValidPassword(String password){
        if (password.length() >= 6 && password.length() <= 14){
            //if valid password return true
            return true;
        }
        //else return false
        return false;
    }

    /**
     * Validates credit card number input by user
     *
     * @param creditCard is the credit card number input by user
     *
     * @return true if credit card is valid and false otherwise
     */
    private static boolean isValidCreditCard(String creditCard){
        //TODO check credit card validity for every transaction

        //90% of the time the method accepts the credit card
        //10% of the time the method rejects the credit card
        //This method makes a random decision and is not a real credit card validator
        if(Math.random() <= 0.90) {
            return true;
        }
        return false;
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

    /**
     * This is the log in menu that allows the user to log in to either customer or internal account
     *
     * @throws IOException customer log in and internal log in methods in model throw IOException
     * @throws ParseException customer log in and internal log in methods in model throw ParseException
     */
    public static void logIn() throws IOException, ParseException {
        //prompt the user to choose which kind of account they want to log into
        System.out.println("Press 1 to log in to customer account. \nPress 2 to log in to internal account.");
        int logIn = input.nextInt();
        input.nextLine();

        //prompt the user to input their username and password
        System.out.println("Enter username:");
        String username = input.nextLine();
        System.out.println("Enter password:");
        String password = input.nextLine();

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
            default:
                //if they did not choose either 1 or 2
                //make them choose again by recursively calling log in
                System.out.println("Invalid option chosen.");
                logIn();
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
        //menu option for customer account home
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
            customerAccountHome(username);
        }
        Integer num = input.nextInt();
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
                // user records a ride
                //TODO currently doesnt account for bike id or user id

                recordRide("from", "rent", false, username);
                break;
            case 5:

                //report a problem
                reportProblem(username);
                break;
            case 6:
                //return to homepage to log out
                initialMenu();
                break;
            case 7:
                ValleyBikeSim.viewBikeList();
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
            customerAccountHome(username);
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

    /**
     * Main menu for internal accounts
     *
     * @throws IOException addStation, addBike, equalizeStations and initialMenu throw IOException
     * @throws ParseException addStation, addBike, equalizeStations and initialMenu throw ParseException
     */
    static void internalAccountHome() throws IOException, ParseException {
        //prompt user to pick option from main internal menu
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
                //add station to station list
                addStation();
                break;
            case 4:
                //add bike to bike list
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
                //go to initial menu to log out
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

    public void resolveData() throws IOException, ParseException {
        String dataFile = input.next();
        ValleyBikeSim.resolveData(dataFile);
    }
}

