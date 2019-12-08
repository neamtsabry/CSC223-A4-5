import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.*;
import java.util.regex.Pattern;

public abstract class ValleyBikeController {

    /** initialize input of type scanner */
    private static Scanner input = new Scanner(System.in);

    private static Deque<Integer> menuPath = new ArrayDeque<Integer>();

    /**
     * Checks where user was last by checking our menuPath stack
     * and returns them to this point
     *
     * @throws IOException create account, log in, save bike list and save station list methods throw IOException
     * @throws ParseException create account, log in, save bike list and save station list methods throw ParseException
     */
    private static void returnToLastMenu(String username) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
    /* We add to the menuPath stack whenever we may need to
    remember this landing page in order to return to it.

    menuPath INDEX
    ------------------------------------------------
    The #s in menuPath refer to the following pages:
    1 - initial menu
        11 - create account
        12 - log in menu

    2 - customer account home
        21 - edit account info
        22 - view account balance
        23 - view station list
        241 - rent bike
        242 - return bike
        25 - report problem

    3 - internal account home
        31 -
        32 -
        33 -

     */

        if (menuPath.isEmpty()){ initialMenu(); }

        switch(menuPath.pop()) {
            case 1:
                initialMenu();
            case 11:
                CreateCustomerAccount();
            case 12:
                logIn();
            case 21:
                editCustomerAccount(username);
            case 3:
                internalAccountHome(username);
            default:
                initialMenu();
        }
    }

    /**
     * Basic option menu that shows at start of program and when no one is logged in
     * Allows user to create a new account or log in or exit
     *
     * @throws IOException create account, log in, save bike list and save station list methods throw IOException
     * @throws ParseException create account, log in, save bike list and save station list methods throw ParseException
     */
    static void initialMenu() throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //TODO back menu on all menus
        //TODO exit option on all menus

        //check whether it's time to renew customer's memberships
        ValleyBikeSim.checkMembershipRenewalTime();

        System.out.print("\nPlease choose from one of the following menu options: \n"
                + "1. Create Customer Account\n"
                + "2. Log In\n"
                + "0. Exit program\n");
        //prompt the user to pick an int option
        int num = getResponse("Please enter your selection (0-2):");
        input.nextLine();

        switch(num) {
            case 1:
                //create a new customer account
                CreateCustomerAccount();
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
//                ValleyBikeSim.saveBikeList();
//                ValleyBikeSim.saveStationList();

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
    private static void CreateCustomerAccount() throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //Assumption: a new internal account cannot be created by a user who is not logged into an internal account
        //i.e. only internal staff can create new internal accounts

        //TODO Check if username already exists right away

        //remember this menu in case we need to return
        menuPath.push(1);

        //each field has its own method which calls itself until a valid input is entered
        String username = enterUsername();
        String password = enterPassword();
        String emailAddress = enterEmail();
        String creditCard = enterCreditCard();
        int membership = enterMembership();

        //once all the required fields have been input by the user, create new customer account
        //Assumption: initially the balance in customer account is always 0
        //TODO set last Payment to null?
        ValleyBikeSim.createCustomerAccount(username, password, emailAddress, creditCard, membership);
        Membership membershipType = ValleyBikeSim.checkMembershipType(membership);
        //set date they joined this membership
        membershipType.setMemberSince(LocalDate.now());
        membershipType.setLastPayment(LocalDate.now());
        CustomerAccount customerAccount = new CustomerAccount(username, password, emailAddress, creditCard, membershipType);
        //add customer account to customer account map
        ValleyBikeSim.addCustomerAccount(customerAccount);

        //Let the user know the account has been successfully created
        System.out.println("Customer account successfully created!");

        menuPath.pop();// we no longer need to remember this menu

        customerAccountHome(username);
    }

    /**
     * This is the log in menu that allows the user to log in to either customer or internal account
     *
     * @throws IOException customer log in and internal log in methods in model throw IOException
     * @throws ParseException customer log in and internal log in methods in model throw ParseException
     */
    private static void logIn() throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //prompt the user to choose which kind of account they want to log into

        System.out.println("\nPlease choose from one of the following menu options:");
        int logIn = getResponse("1. Log in to customer account.\n" +
                "2. Log in to internal account.\n" +
                "0. Return to menu.\n" +
                "Please enter your selection (0-2):");
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
            System.out.println("That is not a valid input. Please try again.");
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
        //if function call finished and returned to this page, keep calling menu again until 'return to menu' called
        logIn();
    }


    /**
     * Standard menu page for a customer account after logging in
     *
     * @param username unique username associated with the customer account
     *
     * @throws IOException editCustomerAccount, viewStationList, recordRide, reportProblem, initialMenu, viewBikeList throw IOException
     * @throws ParseException editCustomerAccount, viewStationList, recordRide, reportProblem, initialMenu, viewBikeList throw ParseException
     */
    static void customerAccountHome(String username) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {

        //checks whether user has a rental, and if so whether it exceeds 24 hours
        ValleyBikeSim.checkBikeRented(username);

        CustomerAccount customer = ValleyBikeSim.getCustomerObj(username);
        //menu option for customer account home
        System.out.println("\nPlease choose from one of the following menu options:\n"
                + "1. View and edit account info\n"
                + "2. View account balance\n"
                + "3. View station list");

        if (customer.getIsReturned()) { System.out.println("4. Rent a bike"); }
        else { System.out.println("4. Return bike"); }

        System.out.println("5. Report a problem\n"
                + "6. View total number of rides\n"
                + "7. View average ride time\n"
                + "8. View your most popular ride time.\n"
                + "9. Delete account.\n"
                + "0. Log out\n" +
                "Please enter your selection (0-5):");

        // if input is not a integer
        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("That is not a valid input. Please try again.");
            customerAccountHome(username);
        }

        int num = input.nextInt();
        switch(num) {
            case 1:
                //print current account info
                viewCustomerAccount(username);
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
                else {
                    UUID lastRideId = customer.getLastRideId();
                    returnBike(username, lastRideId);
                } // if customer has ongoing rental, help user return bike
                //TODO save data after renting and returning
                break;
            case 5:
                reportProblem(username);
                break;
            case 6:
                break;
            case 7:
                //TODO
                break;
            case 8:
                break;
            case 9:
                ValleyBikeSim.disableCustomerAccount(username);
                break;
            case 0:
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

    private static void createInternalAccount() throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //Assumption: a new internal account cannot be created by a user who is not logged into an internal account
        //i.e. only internal staff can create new internal accounts
        //TODO Grace
        //TODO Check if username already exists right away

        //each field has its own method which calls itself until a valid input is entered
        String username = enterUsername();
        String password = enterPassword();
        String emailAddress = enterEmail();

        InternalAccount internalAccount = new InternalAccount(username, password, emailAddress);
        ValleyBikeSim.addInternalAccount(internalAccount, username);

        //Let the user know the account has been successfully created
        System.out.println("Internal account successfully created!");

        menuPath.pop();// we no longer need to remember this menu

        internalAccountHome(username);
    }

    /**
     * Menu page for editing customer account information
     *
     * @param username is the unique username associated with the customer account
     */
    private static void viewCustomerAccount(String username) {
        CustomerAccount customer = ValleyBikeSim.getCustomerObj(username);
        System.out.println("\nCUSTOMER ACCOUNT INFORMATION:" +
                "\nUsername: " + customer.getUsername());
        System.out.print("Password: ");
        for (int i=0; i<customer.getPassword().length(); i++){
            System.out.print("*");
        }
        System.out.println("\nEmail Address: " + customer.getEmailAddress());
        //TODO Must validate CC is 16 characters long for this 'last 4 #s of cc' substring to work
        //System.out.println("Credit Card: " + customer.getCreditCard().substring(11));
        System.out.println("Membership: " + customer.getMembership().getMembershipString());
    }


    /**
     * Menu page for editing customer account information
     *
     * @param username is the unique username associated with the customer account
     */
    private static void editCustomerAccount(String username) throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //TODO save edited fields
        //TODO add a return to customer home option
        //TODO recursively call itself to edit multiple fields
        //TODO handle edge case of not entering int
        //prompt user to choose which field they want to edit

        System.out.println("\nPlease choose from one of the following menu options:\n" +
                "1. Edit username.\n" +
                "2. Edit password.\n" +
                "3. Edit email address.\n" +
                "4. Edit credit card number.\n" +
                "5. Edit membership.\n" +
                "0. Return to account home." +
                "Please enter your selection (0-5):");
        int edit = input.nextInt();
        input.nextLine();
        switch (edit){
            case 1:
                //remember this menu so we can return later
                menuPath.push(21);

                //edit username
                String newUsername = enterUsername();
                ValleyBikeSim.updateCustomerUsername(username, newUsername);
                break;
            case 2:
                //remember this menu so we can return later
                menuPath.push(21);

                //edit password
                String newPassword = enterPassword();
                ValleyBikeSim.updateCustomerPassword(username, newPassword);
                break;
            case 3:
                //remember this menu so we can return later
                menuPath.push(21);

                //edit email address
                String newEmail = enterEmail();
                ValleyBikeSim.updateCustomerEmailAddress(username, newEmail);
                break;
            case 4:
                //remember this menu so we can return later
                menuPath.push(21);

                //edit credit card number
                String newCreditCard = enterCreditCard();
                ValleyBikeSim.updateCustomerCreditCard(username, newCreditCard);
                break;
            case 5:
                //remember this menu so we can return later
                menuPath.push(21);

                //edit membership type
                int newMembership = enterMembership();
                ValleyBikeSim.updateCustomerMembership(username, newMembership);
                break;
            case 0:
                return;
            default:
                System.out.println("That is not a valid input. Please try again.");
        }
        //if function call finished and returned to this page, keep calling home again until 'return to menu' is chosen
        editCustomerAccount(username);
    }

    private static void editInternalAccount(String username) throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //TODO add a return to customer home option
        //TODO handle edge case of not entering int
        //TODO Grace
        //prompt user to choose which field they want to edit

        System.out.println("\nPlease choose from one of the following menu options:\n" +
                "1. Edit username.\n" +
                "2. Edit password.\n" +
                "3. Edit email address.\n" +
                "0. Return to account home." +
                "Please enter your selection (0-5):");
        int edit = input.nextInt();
        input.nextLine();
        switch (edit){
            case 1:
                //edit username
                String newUsername = enterUsername();
                ValleyBikeSim.updateInternalUsername(username, newUsername);
                break;
            case 2:
                //edit password
                String newPassword = enterPassword();
                ValleyBikeSim.updateInternalPassword(username, newPassword);
                break;
            case 3:
                //edit email address
                String newEmail = enterEmail();
                ValleyBikeSim.updateInternalEmailAddress(username, newEmail);
                break;
            case 0:
                return;
            default:
                System.out.println("That is not a valid input. Please try again.");
        }
        //if function call finished and returned to this page, keep calling home again until 'return to menu' is chosen
        editInternalAccount(username);
    }

    /**
     * Can be used for both renting and returning bike
     * Prompts the user for info as to achieve those tasks
     *
     * @throws IOException
     * @throws ParseException
     */
    private static void rentBike(String username) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //check membership, and if pay-as-you-go make sure credit card is still valid before continuing
        int membership = ValleyBikeSim.viewMembershipType(username).getMembershipInt();
        if (membership == 1) {
            String creditCard = ValleyBikeSim.viewCreditCard(username);
            //check validity of credit card, send them back to home menu if not valid
            if (!isValidCreditCard(creditCard)) {
                System.out.println("Sorry, your credit card is not valid. Please make sure the credit card saved" +
                        " in your account is correct, then try again.");
                return; // return to customerAccountHome
            }
        } //if there is no problem, continue with rental

        // View stations
        System.out.println("STATION LIST:");
        ValleyBikeSim.viewStationList(); // view station list

        // choose station to rent from or go back
        int statId = getResponse("Please pick a station from the above list to rent a bike from.\n" +
                "Enter the station ID ('11') or '0' to return to menu: ");

        // if user entered 0, return to menu
        if (Objects.equals(statId, 0)){ return; }

        // Validate user input for station ID
        // keep prompting user until input matches the ID of an available station
        Station stationFrom = ValleyBikeSim.getStationObj(statId); // get station obj (or null) from input

        // if station doesn't exist or doesn't have any bikes
        while((stationFrom == null)||(Objects.equals(stationFrom.getBikes(), 0))) {
            if (stationFrom == null) System.out.println("The station ID entered does not exist in our system.");

            //if station doesn't have bikes, equalize stations and have user re-select station
            else if (Objects.equals(stationFrom.getBikes(), 0)) {
                System.out.println("The station entered does not have any bikes.\n" +
                        "Notifying maintenance worker to resolve this...");
                ValleyBikeSim.equalizeStations();
                System.out.println("The bikes have now been redistributed between the stations.\n");
            }

            ValleyBikeSim.viewStationList();
            statId = getResponse("Please pick a station to rent a bike from.\n" +
                    "Enter the station ID ('11') or '0' to return to menu: ");
            stationFrom = ValleyBikeSim.getStationObj(statId);
        }

        // View available bike ids at station
        System.out.println("Here's a list of bike IDs at Station #" + statId);
        System.out.format("%-10s%-10s\n", "Bike ID");

        // Get list iterator of bikes at station
        LinkedList<Integer> bikeList = stationFrom.getBikeList();
        ListIterator<Integer> bikesAtStation = bikeList.listIterator();

        // Print bikes at station
        while(bikesAtStation.hasNext()){
            int bikeInt = bikesAtStation.next();
            System.out.format("%-10s%-10d\n", bikeInt);
        }

        // Choose bike to rent
        int bikeID = getResponse("Please enter the ID number of the bike you" +
                " would like to rent ('11') or '0' to return to menu: ");

        // if user entered 0, return to menu
        if (Objects.equals(bikeID, 0)){ return; }

        Bike someBike = ValleyBikeSim.getBikeObj(bikeID); // get bike object or null from bike ID

        while (!bikeList.contains(someBike)){
            System.out.println("The bike ID entered is not at this station.");
            bikeID = getResponse("Please enter the ID number of the bike you" +
                    " would like to rent ('11') or '0' to return to menu: ");

            // if user entered 0, return to menu
            if (Objects.equals(bikeID, 0)){ return; }

            someBike = ValleyBikeSim.getBikeObj(bikeID);
        }

        someBike.moveStation(0); // move bike to station '0' <- our "non-station" ID

        UUID rideId = UUID.randomUUID();

        // create new ride object
        Ride ride = new Ride(rideId,
                bikeID,
                username,
                false,
                Instant.now(),
                null);

        // add ride to map as well as database
        ValleyBikeSim.addRide(ride);

        // Add ride to customer account
        // assume username is always valid
        CustomerAccount customer = ValleyBikeSim.getCustomerObj(username); // get customer account object
        ValleyBikeSim.updateRideIdList(username, rideId);
        ValleyBikeSim.updateLastRideisReturned(username, false);

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
     * Prompts user for ride enough to return a bike
     *
     * @throws IOException
     * @throws ParseException
     * @param username for user
     */
    private static void returnBike(String username, UUID lastRideId) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        Ride rideObj = ValleyBikeSim.getRideObj(lastRideId);

        // View stations
        System.out.println("STATION LIST:");
        ValleyBikeSim.viewStationList(); // view station list

        // choose station to rent from
        int statId = getResponse("Please enter station to which you're returning the bike " +
                "or '0' to return to the menu");

        // if user entered 0, return to menu
        if (Objects.equals(statId, 0)){ return; }

        // designated station, whether bike returned to or bike rented from
        Station stationTo = ValleyBikeSim.getStationObj(statId);

        // keep prompting user until the station obj is not null
        while(stationTo == null) {
            System.out.println("The station entered does not exist in our system.");
            statId = getResponse("Please enter station to which you're returning the bike " +
                    "or '0' to return to the menu");

            // if user entered 0, return to menu
            if (Objects.equals(statId, 0)){ return; }

            stationTo = ValleyBikeSim.getStationObj(statId);
        }

        // get rented bike
        int bikeId = rideObj.getBikeId(); //get bike ID from ride
        Bike someBike = ValleyBikeSim.getBikeObj(bikeId); //get bike object from ID

        // move bike to new station
        someBike.moveStation(statId);

        // update ride to be returned and set its end timee stamp
        ValleyBikeSim.updateRideIsReturned(lastRideId, true);
        ValleyBikeSim.updateRideEndTimeStamp(lastRideId, Instant.now());

        // set the same in customer account
        CustomerAccount customer = ValleyBikeSim.getCustomerObj(username);
        ValleyBikeSim.updateLastRideisReturned(username, true);

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

        //check membership to determine how to charge for rental
        int membership = ValleyBikeSim.viewMembershipType(username).getMembershipInt();
        int ridesLeft = ValleyBikeSim.viewMembershipType(username).getTotalRidesLeft();
        //if pay-as-you-go or no rides remaining on membership, charge by time
        if (ridesLeft == 0) {
            long rideLength = rideObj.getRideLength();
            long paymentDue = rideLength * (long) .30;
            double balance = ValleyBikeSim.viewAccountBalance(username) + paymentDue;

            // already done in update
            // rideObj.setPayment(paymentDue);

            ValleyBikeSim.updateRidePayment(lastRideId, paymentDue);

            //TODO update user balance using balance (AM)
        } else {
            ValleyBikeSim.viewMembershipType(username).setTotalRidesLeft(ridesLeft - 1);
            //otherwise merely decrement rides remaining in membership
        }

        System.out.println("You're all done! Thank you for returning this bike.");

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
    private static void reportProblem(String username) throws IOException, ParseException, ClassNotFoundException {
        // prompt user for maintenance report
        System.out.println("Please enter maintenance report or '0' to cancel:");
        input.nextLine();
        String mntReport = input.nextLine();

        // if user entered 0, return to menu
        if (mntReport.contentEquals("0")){
            System.out.println("Report problem has been canceled.");
            return;
        }

        int bikeId = getResponse("Please enter the ID of the bike you" +
                " are experiencing problems with ('11') or '0' to return to the menu:");

        // if user entered 0, return to menu
        if (Objects.equals(bikeId, 0)){ return; }

        Bike someBike = ValleyBikeSim.getBikeObj(bikeId); // get bike object or null from bike ID

        while (someBike != null){ //input is not a bike ID
            System.out.println("The bike ID entered does not exist in our system. Please try again.");
            bikeId = getResponse("Please enter the ID of the bike you" +
                    " are experiencing problems with ('11') or '0' to cancel:");

            someBike = ValleyBikeSim.getBikeObj(bikeId);

            // if user entered 0, return to menu
            if (Objects.equals(bikeId, 0)){
                System.out.println("Report problem has been canceled.");
                return;
            }
        }

        // add to maintenance requests
        ValleyBikeSim.addToMntRqs(bikeId, mntReport);

        // get bike object
        Bike bike = ValleyBikeSim.getBikeObj(bikeId);

        // set bike's maintenance report and maintenance to true
        ValleyBikeSim.updateBikeRqMnt(bikeId, true);
        ValleyBikeSim.updateBikeMntReport(bikeId, mntReport);

        // bike is now out of commission until fixed
        ValleyBikeSim.updateBikeLocation(bikeId, 1);

        // increase maintenance requests for the station
        Station statObj = ValleyBikeSim.getStationObj(bike.getStation());
        ValleyBikeSim.updateStationMntRqsts(bike.getStation(), statObj.getMaintenanceRequest()+1);

        // let user know the process is done
        System.out.println("Maintenance report has been successfully filed!");
    }

    /**
     * Main menu for internal accounts
     *
     * @throws IOException addStation, addBike, equalizeStations and initialMenu throw IOException
     * @throws ParseException addStation, addBike, equalizeStations and initialMenu throw ParseException
     */
    static void internalAccountHome(String username) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
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
                + "10. Equalize stations\n"
                + "11. View total number of users\n"
                + "12. View total number of maintenance requests\n"
                + "13. View most popular ride time of the day\n"
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
                createInternalAccount();
                break;
            case 2:
                editInternalAccount(username);
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
            case 11:
                break;
            case 12:
                //TODO
                break;
            case 13:
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
    private static void addStation() throws IOException, ParseException, ClassNotFoundException, InterruptedException, NoSuchAlgorithmException {
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
        Integer capacity = getResponseBetween(5, 37, "What is the station's capacity?");

        // number of kiosks
        Integer kiosk = getResponse("How many kiosks?");

        // prompt for the station's address
        System.out.println("Please enter station address: ");
        input.nextLine();
        String address = input.nextLine();

        // confirmation
        System.out.println("Are you sure you want to add a station with the info entered?(y/n)");
        String confirm = input.nextLine();

        switch(confirm.toLowerCase()){
            case "y":
                // create new station object with received data from user
                Station stationOb = new Station(
                        name,
                        maintenanceRequest,
                        capacity,
                        kiosk,
                        address);

                // add new station to database and tree
                ValleyBikeSim.addStation(stationOb, id);

                System.out.println("Station has been added!");
                break;
            case"n":
                System.out.println("The station was not added, taking you back to your account home...");
                break;
            default:
                System.out.println("You have to answer with y (yes) or n (no)");
                break;
        }
    }

    /**
     * This method enables maintenance workers to add new bikes
     *
     * @throws IOException
     * @throws ParseException
     */
    private static void addBike() throws IOException, ParseException, ClassNotFoundException, InterruptedException, NoSuchAlgorithmException {
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

        // assume bike starts off as not needing maintenance
        String mnt = "n";

        // initiate maintenance report string
        String mntReport = " ";

        // assume bike starts off as available
        int bikeLocation = 0;

        // create new bike object based on user's inputs
        Bike bikeOb = new Bike(
                id,
                bikeLocation,
                stationId,
                mnt,
                mntReport
        );

        // update database and station object with bike list
        ValleyBikeSim.updateStationBikeList(stationId, id);

        // add to bike tree structure
        ValleyBikeSim.addBike(bikeOb);
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
        return input.nextInt();
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
//    public void resolveData() throws IOException, ParseException {
//        String dataFile = input.next();
//        ValleyBikeSim.resolveData(dataFile);
//    }

    /**
     * Prompts user to input username
     * Validates if username is between 6-14 characters
     * Recursively calls itself until valid username input by user
     *
     * @return valid username input by user
     */
    private static String enterUsername() throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //prompts user to input username
        System.out.println("Enter username (must be between 6-14 characters) or '0' to cancel:");
        String username = input.nextLine();

        // check for '0' input and return to previous menu
        if (username.contentEquals("0")) { returnToLastMenu(null); }

        //validates if username is between 6-24 characters
        while (!isValidUsername(username)){

            //recursively calls itself until valid username input by user
            System.out.println("Username is not valid.");

            //prompts user to input username
            System.out.println("Enter username (must be between 6-14 characters):");
            username = input.nextLine();

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
    private static String enterPassword() throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //prompts user to input password
        System.out.println("Enter password (must be between 6-14 characters) or '0' to cancel:");
        String password = input.nextLine();

        // check for '0' input and return to previous menu
        if (password.contentEquals("0")) { returnToLastMenu(null); }

        //validates if password is between 6-24 characters
        while (!isValidPassword(password)){

            //recursively calls itself until valid password input by user
            System.out.println("Password is not valid.");

            //prompts user to input password
            System.out.println("Enter password (must be between 6-14 characters):");
            password = input.nextLine();
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
    private static String enterEmail() throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        // TODO let user know how to make valid email address
        //prompts user to input email address
        System.out.println("Enter email address or '0' to cancel:");
        String emailAddress = input.nextLine();

        // check for '0' input and return to previous menu
        if (emailAddress.contentEquals("0")) { returnToLastMenu(null); }

        //validates if email address is in correct format
        while (!isValidEmail(emailAddress)){

            //recursively calls itself until valid email address input by user
            System.out.println("Email address is not valid.");

            //prompts user to input email address
            System.out.println("Enter email address:");
            emailAddress = input.nextLine();
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
    private static String enterCreditCard() throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //prompts user to input email address
        System.out.println("Enter credit card number or '0' to cancel:");
        String creditCard = input.nextLine();

        // check for '0' input and return to previous menu
        if (creditCard.contentEquals("0")) { returnToLastMenu(null); }

        //validates if credit card is correct
        while (!isValidCreditCard(creditCard)){

            //recursively calls itself until valid credit card input by user
            System.out.println("Credit card is not valid.");

            //prompts user to input email address
            System.out.println("Enter credit card number:");
            creditCard = input.nextLine();
        }

        //return valid credit card input by user
        return creditCard;
    }

    /**
     * Prompts user to input membership
     *
     * @return membership string input by user
     */
    private static int enterMembership() throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //TODO membership needs to be choose an option between monthly, yearly, pay-as-you-go
        //TODO validate membership input
        System.out.println("Choose membership type: \n" +
                "1. Pay-as-you-go Membership \n" +
                "2. Monthly Membership \n" +
                "3. Yearly Membership");
        //prompt the user to pick an int option
        int num = getResponse("Please enter your selection (1-3) or '0' to go back:");
        input.nextLine();

        // check for '0' input and return to previous menu
        if (Objects.equals(num, 0)) { returnToLastMenu(null); }

        return num;
    }

    /**
     * Validates if username is between 6 and 14 characters
     *
     * @param username is the username input by the user
     *
     * @return true if username is valid and false otherwise
     */
    public static boolean isValidUsername(String username){
        if(username != null) return username.length() >= 6 && username.length() <= 14;
        return false;
    }

    /**
     * Validates if password is between 6 and 14 characters
     *
     * @param password is the password input by the user
     *
     * @return true if password is valid and false otherwise
     */
    public static boolean isValidPassword(String password){
        if(password != null) return password.length() >= 6 && password.length() <= 14;
        return false;
    }

    /**
     * Validates credit card number input by user
     * @return true if credit card is valid and false otherwise
     *
     * Citation for following code:
     * https://github.com/eix128/gnuc-credit-card-checker/blob/master/CCCheckerPro/src/com/gnuc/java/ccc/Luhn.java
     */
    static boolean isValidCreditCard(String ccNumber){
        if (ccNumber == null) {
            return false;
        }

        try {
            int num = Integer.parseInt(ccNumber);
        } catch (NumberFormatException nfe) {
            return false;
        }

        if(ccNumber.length() == 16){
            //90% of the time the method accepts the credit card
            //10% of the time the method rejects the credit card
            //This method makes a random decision and is not a real credit card validator
            return Math.random() <= 0.95;
        }

        return false;
    }

    /**
     * Checks if the email address input by the user is valid
     *
     * @param emailAddress is the email address input by the user
     *
     * @return true if email address is valid and false otherwise
     *
     * //note made this public for the sake of testing
     */
    public static boolean isValidEmail(String emailAddress) {
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
